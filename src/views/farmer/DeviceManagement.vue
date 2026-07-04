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

const devices = ref([
  { id: 'D001', name: '温湿度传感器', type: 'sensor', status: 'online', lastUpdate: '2分钟前', data: { temp: 25.6, humidity: 68 } },
  { id: 'D002', name: '土壤湿度传感器', type: 'sensor', status: 'online', lastUpdate: '5分钟前', data: { soilMoisture: 72 } },
  { id: 'D003', name: '智能灌溉控制器', type: 'controller', status: 'online', lastUpdate: '10分钟前', data: { status: '自动模式' } },
  { id: 'D004', name: '光照传感器', type: 'sensor', status: 'offline', lastUpdate: '2小时前', data: { light: 450 } },
  { id: 'D005', name: 'CO2传感器', type: 'sensor', status: 'online', lastUpdate: '3分钟前', data: { co2: 420 } },
  { id: 'D006', name: '智能通风系统', type: 'controller', status: 'online', lastUpdate: '8分钟前', data: { status: '运行中' } }
])
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
        <h1>设备管理</h1>
        <p>监控和管理您的智能设备</p>
      </header>

      <div class="stats-row">
        <div class="stat-box">
          <span class="stat-num online">{{ devices.filter(d => d.status === 'online').length }}</span>
          <span class="stat-label">在线设备</span>
        </div>
        <div class="stat-box">
          <span class="stat-num offline">{{ devices.filter(d => d.status === 'offline').length }}</span>
          <span class="stat-label">离线设备</span>
        </div>
        <div class="stat-box">
          <span class="stat-num total">{{ devices.length }}</span>
          <span class="stat-label">设备总数</span>
        </div>
      </div>

      <section class="devices-section">
        <h2>设备列表</h2>
        <div class="devices-grid">
          <div v-for="device in devices" :key="device.id" class="device-card">
            <div class="device-header">
              <div class="device-info">
                <span class="device-id">{{ device.id }}</span>
                <h3 class="device-name">{{ device.name }}</h3>
              </div>
              <span :class="['status-indicator', device.status]">
                {{ device.status === 'online' ? '在线' : '离线' }}
              </span>
            </div>
            <div class="device-data">
              <div v-if="device.type === 'sensor'" class="data-grid">
                <div v-if="device.data.temp" class="data-item">
                  <span class="data-label">温度</span>
                  <span class="data-value">{{ device.data.temp }}°C</span>
                </div>
                <div v-if="device.data.humidity" class="data-item">
                  <span class="data-label">湿度</span>
                  <span class="data-value">{{ device.data.humidity }}%</span>
                </div>
                <div v-if="device.data.soilMoisture" class="data-item">
                  <span class="data-label">土壤湿度</span>
                  <span class="data-value">{{ device.data.soilMoisture }}%</span>
                </div>
                <div v-if="device.data.light" class="data-item">
                  <span class="data-label">光照</span>
                  <span class="data-value">{{ device.data.light }} lux</span>
                </div>
                <div v-if="device.data.co2" class="data-item">
                  <span class="data-label">CO2</span>
                  <span class="data-value">{{ device.data.co2 }} ppm</span>
                </div>
              </div>
              <div v-else class="data-grid">
                <div class="data-item">
                  <span class="data-label">状态</span>
                  <span class="data-value">{{ device.data.status }}</span>
                </div>
              </div>
            </div>
            <div class="device-footer">
              <span class="last-update">最后更新: {{ device.lastUpdate }}</span>
              <button class="control-btn">查看详情</button>
            </div>
          </div>
        </div>
      </section>
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
  max-width: 1200px;
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

.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
}

.stat-box {
  flex: 1;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.stat-num {
  font-size: 32px;
  font-weight: 700;
  display: block;
}

.stat-num.online {
  color: #48bb78;
}

.stat-num.offline {
  color: #ff6b6b;
}

.stat-num.total {
  color: #aa3bff;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.devices-section h2 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0 0 20px;
}

.devices-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.device-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
}

.device-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.device-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.device-id {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.device-name {
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.status-indicator {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-indicator.online {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
}

.status-indicator.offline {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.device-data {
  margin-bottom: 16px;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.data-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  padding: 10px;
}

.data-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  display: block;
  margin-bottom: 4px;
}

.data-value {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.device-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.last-update {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.control-btn {
  padding: 6px 14px;
  background: rgba(170, 59, 255, 0.2);
  border: 1px solid rgba(170, 59, 255, 0.4);
  border-radius: 6px;
  color: #aa3bff;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.control-btn:hover {
  background: rgba(170, 59, 255, 0.3);
}
</style>
