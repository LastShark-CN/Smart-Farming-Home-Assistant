<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import ParticleBackground from '../../components/ParticleBackground.vue'
import ThreeScene from '../../components/ThreeScene.vue'
import request from '../../utils/request'

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

const devices = ref([])

const onlineCount = computed(() => devices.value.filter(d => d.status === 'online').length)
const offlineCount = computed(() => devices.value.filter(d => d.status === 'offline').length)
const totalCount = computed(() => devices.value.length)

const showAddModal = ref(false)
const showDetailModal = ref(false)
const selectedDevice = ref(null)

const form = ref({
  deviceCode: '',
  name: '',
  type: 'sensor',
  remark: ''
})

const deviceTypes = [
  { label: '传感器', value: 'sensor' },
  { label: '控制器', value: 'controller' },
  { label: '摄像头', value: 'camera' }
]

async function loadDevices() {
  try {
    const response = await request.get('/api/devices')
    if (response.data) {
      devices.value = response.data.map(d => ({
        ...d,
        data: typeof d.data === 'string' ? JSON.parse(d.data || '{}') : d.data
      }))
    }
  } catch (error) {
    console.error('加载设备列表失败:', error)
    devices.value = []
  }
}

function formatTime(dateTime) {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  return `${days}天前`
}

async function handleAdd() {
  if (!form.value.deviceCode || !form.value.name) {
    ElMessage.warning('请填写设备编号和名称')
    return
  }
  try {
    const response = await request.post('/api/devices', {
      deviceCode: form.value.deviceCode,
      name: form.value.name,
      type: form.value.type,
      remark: form.value.remark,
      data: '{}'
    })
    if (response.code === 200) {
      showAddModal.value = false
      ElMessage.success('设备添加成功')
      form.value = { deviceCode: '', name: '', type: 'sensor', remark: '' }
      loadDevices()
    }
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

async function handleDelete(device) {
  try {
    await ElMessageBox.confirm(`确定要删除设备 ${device.name} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await request.delete(`/api/devices/${device.id}`)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadDevices()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

async function toggleStatus(device) {
  const newStatus = device.status === 'online' ? 'offline' : 'online'
  try {
    const response = await request.put(`/api/devices/${device.id}/status`, {
      status: newStatus
    })
    if (response.code === 200) {
      device.status = newStatus
      ElMessage.success('状态更新成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

function showDeviceDetail(device) {
  selectedDevice.value = device
  showDetailModal.value = true
}

onMounted(() => {
  loadDevices()
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
        <h1>设备管理</h1>
        <p>监控和管理您的智能设备</p>
      </header>

      <div class="stats-row">
        <div class="stat-box">
          <span class="stat-num online">{{ onlineCount }}</span>
          <span class="stat-label">在线设备</span>
        </div>
        <div class="stat-box">
          <span class="stat-num offline">{{ offlineCount }}</span>
          <span class="stat-label">离线设备</span>
        </div>
        <div class="stat-box">
          <span class="stat-num total">{{ totalCount }}</span>
          <span class="stat-label">设备总数</span>
        </div>
      </div>

      <section class="devices-section">
        <div class="section-header">
          <h2>设备列表</h2>
          <button class="add-btn" @click="showAddModal = true">添加设备</button>
        </div>
        <div class="devices-grid">
          <div v-for="device in devices" :key="device.id" class="device-card">
            <div class="device-header">
              <div class="device-info">
                <span class="device-id">{{ device.deviceCode }}</span>
                <h3 class="device-name">{{ device.name }}</h3>
              </div>
              <button :class="['status-btn', device.status]" @click="toggleStatus(device)">
                {{ device.status === 'online' ? '在线' : '离线' }}
              </button>
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
                  <span class="data-value">{{ device.data.status || '运行中' }}</span>
                </div>
              </div>
            </div>
            <div class="device-footer">
              <span class="last-update">最后更新: {{ formatTime(device.lastUpdate) }}</span>
              <div class="device-actions">
                <button class="action-btn detail" @click="showDeviceDetail(device)">详情</button>
                <button class="action-btn delete" @click="handleDelete(device)">删除</button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>添加设备</h3>
          <button class="close-btn" @click="showAddModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>设备编号</label>
            <input v-model="form.deviceCode" type="text" placeholder="请输入设备编号" />
          </div>
          <div class="form-group">
            <label>设备名称</label>
            <input v-model="form.name" type="text" placeholder="请输入设备名称" />
          </div>
          <div class="form-group">
            <label>设备类型</label>
            <select v-model="form.type">
              <option v-for="type in deviceTypes" :key="type.value" :value="type.value">
                {{ type.label }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>备注</label>
            <textarea v-model="form.remark" placeholder="请输入备注信息"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showAddModal = false">取消</button>
          <button class="btn-confirm" @click="handleAdd">确定</button>
        </div>
      </div>
    </div>

    <div v-if="showDetailModal && selectedDevice" class="modal-overlay" @click.self="showDetailModal = false">
      <div class="modal-content">
        <div class="modal-header">
          <h3>设备详情</h3>
          <button class="close-btn" @click="showDetailModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="detail-row">
            <span class="detail-label">设备编号</span>
            <span class="detail-value">{{ selectedDevice.deviceCode }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">设备名称</span>
            <span class="detail-value">{{ selectedDevice.name }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">设备类型</span>
            <span class="detail-value">{{ deviceTypes.find(t => t.value === selectedDevice.type)?.label || selectedDevice.type }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">设备状态</span>
            <span :class="['detail-value', selectedDevice.status]">{{ selectedDevice.status === 'online' ? '在线' : '离线' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">创建时间</span>
            <span class="detail-value">{{ selectedDevice.createTime || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">最后更新</span>
            <span class="detail-value">{{ selectedDevice.lastUpdate || '-' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">备注</span>
            <span class="detail-value">{{ selectedDevice.remark || '-' }}</span>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showDetailModal = false">关闭</button>
        </div>
      </div>
    </div>
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.add-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  border: none;
  border-radius: 8px;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(170, 59, 255, 0.4);
}

.status-btn {
  padding: 4px 12px;
  border: none;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.status-btn.online {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
}

.status-btn.online:hover {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.status-btn.offline {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.status-btn.offline:hover {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
}

.device-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn.detail {
  background: rgba(170, 59, 255, 0.2);
  color: #aa3bff;
}

.action-btn.detail:hover {
  background: rgba(170, 59, 255, 0.3);
}

.action-btn.delete {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.action-btn.delete:hover {
  background: rgba(255, 107, 107, 0.3);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: rgba(30, 30, 50, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h3 {
  margin: 0;
  color: white;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  font-size: 24px;
  cursor: pointer;
  line-height: 1;
}

.close-btn:hover {
  color: white;
}

.modal-body {
  padding: 20px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #aa3bff;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.btn-cancel,
.btn-confirm {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
}

.btn-cancel:hover {
  background: rgba(255, 255, 255, 0.2);
}

.btn-confirm {
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  color: white;
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(170, 59, 255, 0.4);
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
}

.detail-value {
  color: white;
  font-size: 14px;
  font-weight: 500;
}

.detail-value.online {
  color: #48bb78;
}

.detail-value.offline {
  color: #ff6b6b;
}
</style>
