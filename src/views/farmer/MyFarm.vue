<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import ParticleBackground from '../../components/ParticleBackground.vue'
import ThreeScene from '../../components/ThreeScene.vue'
import { getCompanyInfo } from '../../api/home'

const router = useRouter()
const userStore = useUserStore()

const farmInfo = ref({
  name: '加载中...',
  area: '--',
  location: '加载中...',
  crops: [],
  workers: 0
})

const cropList = ref([
  { name: '水稻', status: '生长中', progress: 65 },
  { name: '西红柿', status: '成熟期', progress: 90 },
  { name: '草莓', status: '种植期', progress: 30 },
  { name: '黄瓜', status: '收获期', progress: 95 }
])

onMounted(async () => {
  try {
    const response = await getCompanyInfo()
    if (response.data) {
      farmInfo.value = {
        name: response.data.name || '阳光农场',
        area: response.data.area || '--',
        location: response.data.address || '浙江省杭州市余杭区',
        crops: [],
        workers: response.data.workers || 0
      }
    }
  } catch (error) {
    farmInfo.value = {
      name: '阳光农场',
      area: '50亩',
      location: '浙江省杭州市余杭区',
      crops: ['水稻', '蔬菜', '水果'],
      workers: 8
    }
  }
})

function handleLogout() {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}

function goBack() {
  router.push('/dashboard')
}
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
          <button class="menu-btn" @click="goBack">返回首页</button>
          <button class="logout-btn" @click="handleLogout">退出登录</button>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <header class="header">
        <h1>我的农场</h1>
        <p>管理您的农场信息和作物</p>
      </header>

      <div class="farm-card">
        <h2>{{ farmInfo.name }}</h2>
        <div class="farm-details">
          <div class="detail-item">
            <span class="detail-icon">📍</span>
            <span>{{ farmInfo.location }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-icon">🌾</span>
            <span>面积: {{ farmInfo.area }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-icon">👥</span>
            <span>工人: {{ farmInfo.workers }}人</span>
          </div>
        </div>
        <div class="crops-tag">
          <span v-for="crop in farmInfo.crops" :key="crop" class="crop-tag">
            {{ crop }}
          </span>
        </div>
      </div>

      <section class="crops-section">
        <h2>作物管理</h2>
        <div class="crops-list">
          <div v-for="crop in cropList" :key="crop.name" class="crop-card">
            <div class="crop-header">
              <span class="crop-name">{{ crop.name }}</span>
              <span :class="['crop-status', crop.status === '收获期' ? 'harvest' : crop.status === '成熟期' ? 'mature' : crop.status === '生长中' ? 'growing' : 'planting']">
                {{ crop.status }}
              </span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: crop.progress + '%' }"></div>
            </div>
            <span class="progress-text">{{ crop.progress }}%</span>
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
  max-width: 1000px;
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

.farm-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 40px;
}

.farm-card h2 {
  font-size: 24px;
  font-weight: 600;
  color: white;
  margin: 0 0 16px;
}

.farm-details {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.detail-icon {
  font-size: 16px;
}

.crops-tag {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.crop-tag {
  padding: 4px 12px;
  background: rgba(78, 205, 196, 0.2);
  border: 1px solid rgba(78, 205, 196, 0.4);
  border-radius: 20px;
  color: #4ecdc4;
  font-size: 12px;
}

.crops-section h2 {
  font-size: 20px;
  font-weight: 600;
  color: white;
  margin: 0 0 20px;
}

.crops-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.crop-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
}

.crop-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.crop-name {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.crop-status {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.crop-status.harvest {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.crop-status.mature {
  background: rgba(255, 193, 7, 0.2);
  color: #ffc107;
}

.crop-status.growing {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
}

.crop-status.planting {
  background: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.progress-bar {
  height: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #aa3bff, #00d4ff);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.progress-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}
</style>
