<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import ParticleBackground from '../components/ParticleBackground.vue'
import { getEnvironmentData, getCropProductionData, getPlantingAreaData, getEnvironmentHistory } from '../api/farmData'
import { environmentData, productionData, areaData, historyData } from '../mock/farmData'

const router = useRouter()
const userStore = useUserStore()
const isVisible = ref(false)

const environmentRef = ref(null)
const productionRef = ref(null)
const areaRef = ref(null)
const historyRef = ref(null)

let environmentChart = null
let productionChart = null
let areaChart = null
let historyChart = null

const envData = ref({})
const prodData = ref({})
const areaDataState = ref({})
const historyDataState = ref([])

onMounted(() => {
  setTimeout(() => {
    isVisible.value = true
  }, 100)
  
  loadData()
  
  nextTick(() => {
    initCharts()
  })
})

onUnmounted(() => {
  if (environmentChart) environmentChart.dispose()
  if (productionChart) productionChart.dispose()
  if (areaChart) areaChart.dispose()
  if (historyChart) historyChart.dispose()
})

async function loadData() {
  try {
    const envRes = await getEnvironmentData().catch(() => environmentData)
    const prodRes = await getCropProductionData().catch(() => productionData)
    const areaRes = await getPlantingAreaData().catch(() => areaData)
    const histRes = await getEnvironmentHistory().catch(() => historyData)
    
    envData.value = envRes.data || envRes
    prodData.value = prodRes.data || prodRes
    areaDataState.value = areaRes.data || areaRes
    historyDataState.value = histRes.data || histRes
    
    nextTick(() => {
      updateCharts()
    })
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

function initCharts() {
  if (environmentRef.value) {
    environmentChart = echarts.init(environmentRef.value)
    renderEnvironmentChart()
  }
  
  if (productionRef.value) {
    productionChart = echarts.init(productionRef.value)
    renderProductionChart()
  }
  
  if (areaRef.value) {
    areaChart = echarts.init(areaRef.value)
    renderAreaChart()
  }
  
  if (historyRef.value) {
    historyChart = echarts.init(historyRef.value)
    renderHistoryChart()
  }
  
  window.addEventListener('resize', handleResize)
}

function handleResize() {
  environmentChart?.resize()
  productionChart?.resize()
  areaChart?.resize()
  historyChart?.resize()
}

function updateCharts() {
  renderEnvironmentChart()
  renderProductionChart()
  renderAreaChart()
  renderHistoryChart()
}

function renderEnvironmentChart() {
  if (!environmentChart || !envData.value.temperature) return
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['温度', '湿度', 'CO2', '土壤pH'],
      textStyle: { color: 'rgba(255,255,255,0.7)' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['当前值'],
      axisLabel: { color: 'rgba(255,255,255,0.7)' },
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } }
    },
    yAxis: [
      {
        type: 'value',
        name: '温度/湿度',
        axisLabel: { color: 'rgba(255,255,255,0.7)' },
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
      },
      {
        type: 'value',
        name: 'CO2(ppm)/pH',
        axisLabel: { color: 'rgba(255,255,255,0.7)' },
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '温度',
        type: 'bar',
        data: [envData.value.temperature.value],
        itemStyle: { color: '#ee6666' }
      },
      {
        name: '湿度',
        type: 'bar',
        data: [envData.value.humidity.value],
        itemStyle: { color: '#5470c6' }
      },
      {
        name: 'CO2',
        type: 'bar',
        yAxisIndex: 1,
        data: [envData.value.co2.value],
        itemStyle: { color: '#91cc75' }
      },
      {
        name: '土壤pH',
        type: 'bar',
        yAxisIndex: 1,
        data: [envData.value.soilPh.value],
        itemStyle: { color: '#fac858' }
      }
    ]
  }
  
  environmentChart.setOption(option)
}

function renderProductionChart() {
  if (!productionChart || !prodData.value.trend) return
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'line' }
    },
    legend: {
      data: ['产量'],
      textStyle: { color: 'rgba(255,255,255,0.7)' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: prodData.value.trend.map(item => item.month),
      axisLabel: { color: 'rgba(255,255,255,0.7)' },
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } }
    },
    yAxis: {
      type: 'value',
      name: '产量(kg)',
      axisLabel: { color: 'rgba(255,255,255,0.7)' },
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
      splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
    },
    series: [
      {
        name: '产量',
        type: 'line',
        smooth: true,
        data: prodData.value.trend.map(item => item.value),
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(170, 59, 255, 0.5)' },
            { offset: 1, color: 'rgba(170, 59, 255, 0.05)' }
          ])
        },
        itemStyle: { color: '#aa3bff' },
        lineStyle: { width: 3 }
      }
    ]
  }
  
  productionChart.setOption(option)
}

function renderAreaChart() {
  if (!areaChart || !areaDataState.value.crops) return
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}亩 ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: '0%',
      textStyle: { color: 'rgba(255,255,255,0.7)' }
    },
    series: [
      {
        name: '种植面积',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: 'rgba(0,0,0,0.3)',
          borderWidth: 2
        },
        label: {
          show: true,
          color: 'rgba(255,255,255,0.9)',
          formatter: '{b}\n{c}亩'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: true,
          lineStyle: { color: 'rgba(255,255,255,0.3)' }
        },
        data: areaDataState.value.crops.map(item => ({
          value: item.area,
          name: item.name,
          itemStyle: { color: item.color }
        }))
      }
    ]
  }
  
  areaChart.setOption(option)
}

function renderHistoryChart() {
  if (!historyChart || historyDataState.value.length === 0) return
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['温度', '湿度', 'CO2'],
      textStyle: { color: 'rgba(255,255,255,0.7)' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: historyDataState.value.map(item => item.time),
      axisLabel: { color: 'rgba(255,255,255,0.7)', rotate: 45 },
      axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } }
    },
    yAxis: [
      {
        type: 'value',
        name: '温度(°C)/湿度(%)',
        axisLabel: { color: 'rgba(255,255,255,0.7)' },
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
        splitLine: { lineStyle: { color: 'rgba(255,255,255,0.1)' } }
      },
      {
        type: 'value',
        name: 'CO2(ppm)',
        axisLabel: { color: 'rgba(255,255,255,0.7)' },
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.2)' } },
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '温度',
        type: 'line',
        smooth: true,
        data: historyDataState.value.map(item => item.temperature),
        itemStyle: { color: '#ee6666' },
        lineStyle: { width: 2 }
      },
      {
        name: '湿度',
        type: 'line',
        smooth: true,
        data: historyDataState.value.map(item => item.humidity),
        itemStyle: { color: '#5470c6' },
        lineStyle: { width: 2 }
      },
      {
        name: 'CO2',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: historyDataState.value.map(item => item.co2),
        itemStyle: { color: '#91cc75' },
        lineStyle: { width: 2 }
      }
    ]
  }
  
  historyChart.setOption(option)
}

function handleLogout() {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}

const adminMenus = [
  { name: '用户管理', path: '/admin/users', icon: '👥' },
  { name: '系统设置', path: '/admin/settings', icon: '⚙️' },
  { name: 'AI助手', path: '/ai-chat', icon: '🤖' },
  { name: '个人信息', path: '/profile', icon: '👤' }
]

const farmerMenus = [
  { name: '我的农场', path: '/farmer/my-farm', icon: '🌾' },
  { name: '设备管理', path: '/farmer/devices', icon: '📡' },
  { name: '农作物管理', path: '/farmer/crops', icon: '🌱' },
  { name: 'AI助手', path: '/ai-chat', icon: '🤖' },
  { name: '个人信息', path: '/profile', icon: '👤' }
]
</script>

<template>
  <div class="dashboard-container">
    <ParticleBackground />

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
          <button
            v-for="menu in (userStore.role === 'super_admin' ? adminMenus : farmerMenus)"
            :key="menu.name"
            class="menu-btn"
            @click="router.push(menu.path)"
          >
            <span>{{ menu.icon }}</span>
            <span>{{ menu.name }}</span>
          </button>
          <button class="logout-btn" @click="handleLogout">
            退出登录
          </button>
        </div>
      </div>
    </nav>

    <main class="main-content" :class="{ visible: isVisible }">
      <header class="header">
        <h1>欢迎回来，{{ userStore.username }}</h1>
        <p class="role-tag">
          {{ userStore.role === 'super_admin' ? '超级管理员' : '农场主' }}
        </p>
      </header>

      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-icon">🌡️</span>
          <div class="stat-info">
            <span class="stat-value">{{ envData.temperature?.value || '--' }}<span class="stat-unit">°C</span></span>
            <span class="stat-label">空气温度</span>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">💧</span>
          <div class="stat-info">
            <span class="stat-value">{{ envData.humidity?.value || '--' }}<span class="stat-unit">%</span></span>
            <span class="stat-label">空气湿度</span>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">🌬️</span>
          <div class="stat-info">
            <span class="stat-value">{{ envData.co2?.value || '--' }}<span class="stat-unit">ppm</span></span>
            <span class="stat-label">二氧化碳</span>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">🪴</span>
          <div class="stat-info">
            <span class="stat-value">{{ envData.soilPh?.value || '--' }}<span class="stat-unit"></span></span>
            <span class="stat-label">土壤pH值</span>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">📦</span>
          <div class="stat-info">
            <span class="stat-value">{{ prodData.totalProduction?.toLocaleString() || '--' }}<span class="stat-unit">kg</span></span>
            <span class="stat-label">总产量</span>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">🏡</span>
          <div class="stat-info">
            <span class="stat-value">{{ areaDataState.totalArea?.toLocaleString() || '--' }}<span class="stat-unit">亩</span></span>
            <span class="stat-label">种植面积</span>
          </div>
        </div>
      </div>

      <div class="charts-grid">
        <div class="chart-card">
          <h3 class="chart-title">环境监测数据</h3>
          <div ref="environmentRef" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <h3 class="chart-title">农作物产量趋势</h3>
          <div ref="productionRef" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <h3 class="chart-title">种植面积分布</h3>
          <div ref="areaRef" class="chart-container"></div>
        </div>

        <div class="chart-card wide">
          <h3 class="chart-title">24小时环境变化趋势</h3>
          <div ref="historyRef" class="chart-container"></div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.dashboard-container {
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
  max-width: 1600px;
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
  display: flex;
  align-items: center;
  gap: 8px;
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
  max-width: 1600px;
  margin: 0 auto;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease-out;
}

.main-content.visible {
  opacity: 1;
  transform: translateY(0);
}

.header {
  margin-bottom: 40px;
}

.header h1 {
  font-size: 36px;
  font-weight: 600;
  color: white;
  margin: 0;
}

.role-tag {
  display: inline-block;
  margin-top: 12px;
  padding: 6px 16px;
  background: rgba(170, 59, 255, 0.2);
  border: 1px solid rgba(170, 59, 255, 0.4);
  border-radius: 20px;
  color: #aa3bff;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  background: rgba(255, 255, 255, 0.1);
}

.stat-icon {
  font-size: 36px;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: white;
}

.stat-unit {
  font-size: 14px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.6);
  margin-left: 4px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 24px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 24px;
}

.chart-card.wide {
  grid-column: 1 / -1;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: white;
  margin: 0 0 16px;
}

.chart-container {
  width: 100%;
  height: 300px;
}

@media (max-width: 768px) {
  .header h1 {
    font-size: 28px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 250px;
  }

  .nav-links {
    gap: 8px;
  }

  .menu-btn {
    padding: 8px 12px;
    font-size: 12px;
  }

  .logout-btn {
    padding: 8px 12px;
    font-size: 12px;
  }
}
</style>