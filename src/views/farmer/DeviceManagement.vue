<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import ParticleBackground from '../../components/ParticleBackground.vue'
import ThreeScene from '../../components/ThreeScene.vue'
import { getDevicePage, getDeviceTypeList, createDevice, updateDevice, deleteDevice } from '../../api/device'
import { turnOnDevice, turnOffDevice, toggleDevice } from '../../api/deviceControl'

const router = useRouter()
const userStore = useUserStore()

const devices = ref([])
const deviceTypes = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)
const showDialog = ref(false)
const editMode = ref(false)
const currentId = ref(null)

const form = ref({
  name: '',
  code: '',
  status: 0,
  location: '',
  deviceTypeId: '',
  description: ''
})

const onlineCount = computed(() => devices.value.filter(d => d.status === 1).length)
const offlineCount = computed(() => devices.value.filter(d => d.status === 0).length)

onMounted(async () => {
  await loadDeviceTypes()
  await loadDevices()
})

async function loadDeviceTypes() {
  try {
    const response = await getDeviceTypeList()
    deviceTypes.value = response.data || []
  } catch (error) {
    deviceTypes.value = []
  }
}

async function loadDevices() {
  loading.value = true
  try {
    const response = await getDevicePage({
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword.value || undefined
    })
    devices.value = response.data.list || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载设备列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadDevices()
}

function openAddDialog() {
  editMode.value = false
  currentId.value = null
  form.value = { name: '', code: '', status: 0, location: '', deviceTypeId: '', description: '' }
  showDialog.value = true
}

function openEditDialog(device) {
  editMode.value = true
  currentId.value = device.id
  form.value = {
    name: device.name,
    code: device.code,
    status: device.status,
    location: device.location || '',
    deviceTypeId: device.deviceTypeId || '',
    description: device.description || ''
  }
  showDialog.value = true
}

async function handleSubmit() {
  try {
    if (editMode.value) {
      await updateDevice(currentId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createDevice(form.value)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    loadDevices()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除该设备吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteDevice(id)
    ElMessage.success('删除成功')
    loadDevices()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

async function handleTurnOn(deviceId) {
  try {
    await turnOnDevice(deviceId)
    ElMessage.success('设备已开启')
    loadDevices()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

async function handleTurnOff(deviceId) {
  try {
    await turnOffDevice(deviceId)
    ElMessage.success('设备已关闭')
    loadDevices()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

async function handleToggle(deviceId) {
  try {
    await toggleDevice(deviceId)
    ElMessage.success('设备状态已切换')
    loadDevices()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

function getTypeName(typeId) {
  const type = deviceTypes.value.find(t => t.id === typeId)
  return type ? type.name : '未知'
}

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
          <span class="stat-num total">{{ total }}</span>
          <span class="stat-label">设备总数</span>
        </div>
      </div>

      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索设备名称/编号"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="openAddDialog">添加设备</el-button>
      </div>

      <section class="devices-section">
        <div v-if="loading" class="loading-state">加载中...</div>
        <div v-else class="devices-grid">
          <div v-for="device in devices" :key="device.id" class="device-card">
            <div class="device-header">
              <div class="device-info">
                <span class="device-id">{{ device.code }}</span>
                <h3 class="device-name">{{ device.name }}</h3>
                <span class="device-type">{{ getTypeName(device.deviceTypeId) }}</span>
              </div>
              <span :class="['status-indicator', device.status === 1 ? 'online' : 'offline']">
                {{ device.status === 1 ? '在线' : '离线' }}
              </span>
            </div>
            <div class="device-body">
              <div class="device-location">📍 {{ device.location || '未设置位置' }}</div>
            </div>
            <div class="device-footer">
              <div class="control-buttons">
                <el-button
                  size="small"
                  type="success"
                  :disabled="device.status === 1"
                  @click="handleTurnOn(device.id)"
                >开启</el-button>
                <el-button
                  size="small"
                  type="warning"
                  :disabled="device.status === 0"
                  @click="handleTurnOff(device.id)"
                >关闭</el-button>
                <el-button
                  size="small"
                  type="info"
                  @click="handleToggle(device.id)"
                >切换</el-button>
              </div>
              <div class="action-buttons">
                <el-button size="small" type="primary" @click="openEditDialog(device)">编辑</el-button>
                <el-button size="small" type="danger" @click="handleDelete(device.id)">删除</el-button>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadDevices"
            @size-change="loadDevices"
          />
        </div>
      </section>
    </main>

    <el-dialog
      v-model="showDialog"
      :title="editMode ? '编辑设备' : '添加设备'"
      width="600px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="设备名称">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备编号">
          <el-input v-model="form.code" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="form.deviceTypeId" placeholder="请选择设备类型" style="width: 100%">
            <el-option
              v-for="type in deviceTypes"
              :key="type.id"
              :label="type.name"
              :value="type.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="安装位置">
          <el-input v-model="form.location" placeholder="请输入安装位置" />
        </el-form-item>
        <el-form-item label="设备描述">
          <el-input type="textarea" v-model="form.description" placeholder="请输入设备描述" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
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
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  margin-bottom: 24px;
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
  margin-bottom: 24px;
}

.stat-box {
  flex: 1;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
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

.stat-num.online { color: #48bb78; }
.stat-num.offline { color: #ff6b6b; }
.stat-num.total { color: #aa3bff; }

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  width: 260px;
}

.devices-section {
  margin-top: 20px;
}

.loading-state {
  text-align: center;
  padding: 40px;
  color: rgba(255, 255, 255, 0.6);
}

.devices-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.device-card {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
}

.device-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
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

.device-type {
  font-size: 12px;
  color: rgba(170, 59, 255, 0.8);
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

.device-body {
  margin-bottom: 12px;
}

.device-location {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.device-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  flex-wrap: wrap;
  gap: 8px;
}

.control-buttons,
.action-buttons {
  display: flex;
  gap: 6px;
}

.pagination-wrapper {
  padding: 16px 0;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
}

:deep(.el-input__inner) {
  color: white;
}

:deep(.el-dialog) {
  background: rgba(20, 20, 40, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

:deep(.el-dialog__title) {
  color: white;
}

:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.8);
}

@media (max-width: 768px) {
  .stats-row {
    flex-wrap: wrap;
  }

  .stat-box {
    min-width: 100px;
  }

  .devices-grid {
    grid-template-columns: 1fr;
  }
}
</style>
