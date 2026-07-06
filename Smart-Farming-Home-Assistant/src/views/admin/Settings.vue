<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import ParticleBackground from '../../components/ParticleBackground.vue'
import ThreeScene from '../../components/ThreeScene.vue'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}

function goBack() {
  router.push('/dashboard')
}

function saveSettings() {
  ElMessage.success('设置保存成功')
}

const settings = ref({
  systemName: '智慧农业助手',
  autoSave: true,
  notification: true,
  dataRefresh: 30
})
</script>

<template>
  <div class="page-container">
    <ParticleBackground />
    <ThreeScene />

    <nav class="navbar">
      <div class="nav-content">
        <div class="logo" @click="router.push('/')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
            <path d="M2 17l10 5 10-5"></path>
            <path d="M2 12l10 5 10-5"></path>
          </svg>
          <span>智慧农业</span>
        </div>
        <div class="nav-links">
          <button class="menu-btn" @click="goBack">
            返回首页
          </button>
          <button class="logout-btn" @click="handleLogout">
            退出登录
          </button>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <header class="header">
        <h1>系统设置</h1>
        <p>配置系统各项参数</p>
      </header>

      <div class="settings-container">
        <div class="setting-section">
          <h2>基本设置</h2>
          
          <div class="setting-item">
            <label>系统名称</label>
            <input
              v-model="settings.systemName"
              type="text"
              class="setting-input"
            />
          </div>
        </div>

        <div class="setting-section">
          <h2>功能开关</h2>
          
          <div class="setting-item">
            <label>自动保存</label>
            <div class="toggle-switch">
              <input
                v-model="settings.autoSave"
                type="checkbox"
                class="toggle-input"
              />
              <span class="toggle-slider"></span>
            </div>
          </div>

          <div class="setting-item">
            <label>通知提醒</label>
            <div class="toggle-switch">
              <input
                v-model="settings.notification"
                type="checkbox"
                class="toggle-input"
              />
              <span class="toggle-slider"></span>
            </div>
          </div>
        </div>

        <div class="setting-section">
          <h2>数据刷新</h2>
          
          <div class="setting-item">
            <label>刷新间隔（秒）</label>
            <select v-model="settings.dataRefresh" class="setting-select">
              <option :value="10">10秒</option>
              <option :value="30">30秒</option>
              <option :value="60">1分钟</option>
              <option :value="300">5分钟</option>
            </select>
          </div>
        </div>

        <button class="save-btn" @click="saveSettings">
          保存设置
        </button>
      </div>
    </main>
  </div>
</template>

<style scoped>
.page-container {
  min-height: 100vh;
  position: relative;
  z-index: 10;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 16px 24px;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
}

.logo svg {
  width: 32px;
  height: 32px;
  color: #aa3bff;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-btn {
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-btn:hover {
  background: rgba(170, 59, 255, 0.3);
  border-color: #aa3bff;
}

.logout-btn {
  padding: 10px 20px;
  background: rgba(255, 107, 107, 0.2);
  border: 1px solid rgba(255, 107, 107, 0.4);
  border-radius: 8px;
  color: #ff6b6b;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 107, 107, 0.3);
}

.main-content {
  padding: 100px 24px 40px;
  max-width: 600px;
  margin: 0 auto;
}

.header {
  margin-bottom: 40px;
}

.header h1 {
  font-size: 32px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.header p {
  color: rgba(255, 255, 255, 0.6);
  margin-top: 8px;
}

.settings-container {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
}

.setting-section {
  margin-bottom: 24px;
}

.setting-section h2 {
  font-size: 16px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.setting-item label {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

.setting-input,
.setting-select {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  font-size: 14px;
  min-width: 200px;
}

.setting-input:focus,
.setting-select:focus {
  outline: none;
  border-color: #aa3bff;
}

.toggle-switch {
  position: relative;
  width: 48px;
  height: 24px;
}

.toggle-input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.2);
  transition: 0.3s;
  border-radius: 24px;
}

.toggle-slider:before {
  position: absolute;
  content: '';
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background: white;
  transition: 0.3s;
  border-radius: 50%;
}

.toggle-input:checked + .toggle-slider {
  background: #aa3bff;
}

.toggle-input:checked + .toggle-slider:before {
  transform: translateX(24px);
}

.save-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 16px;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(170, 59, 255, 0.5);
}
</style>
