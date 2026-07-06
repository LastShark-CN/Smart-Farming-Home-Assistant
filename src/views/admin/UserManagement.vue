<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import ParticleBackground from '../../components/ParticleBackground.vue'
import ThreeScene from '../../components/ThreeScene.vue'
import { getAdminUsers, createAdminUser, updateAdminUser, deleteAdminUser, updateAdminUserStatus } from '../../api/admin'

const router = useRouter()
const userStore = useUserStore()

const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const loading = ref(false)
const showDialog = ref(false)
const editMode = ref(false)
const currentId = ref(null)

const form = ref({
  username: '',
  email: '',
  password: '',
  phone: ''
})

const statusOptions = [
  { label: '启用', value: 'active' },
  { label: '禁用', value: 'inactive' }
]

onMounted(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const response = await getAdminUsers({
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword.value || undefined
    })
    users.value = response.data.list || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadUsers()
}

function openAddDialog() {
  editMode.value = false
  currentId.value = null
  form.value = { username: '', email: '', password: '', phone: '' }
  showDialog.value = true
}

function openEditDialog(user) {
  editMode.value = true
  currentId.value = user.id
  form.value = {
    username: user.username,
    email: user.email,
    phone: user.phone || '',
    password: ''
  }
  showDialog.value = true
}

async function handleSubmit() {
  try {
    if (editMode.value) {
      const data = { username: form.value.username, email: form.value.email, phone: form.value.phone }
      if (form.value.password) data.password = form.value.password
      await updateAdminUser(currentId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createAdminUser(form.value)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAdminUser(id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

async function handleStatusChange(id, status) {
  try {
    await updateAdminUserStatus(id, status)
    ElMessage.success('状态更新成功')
    loadUsers()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
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
        <h1>用户管理</h1>
        <p>管理系统中的所有用户</p>
      </header>

      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="搜索用户名/邮箱"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button type="success" @click="openAddDialog">添加用户</el-button>
      </div>

      <div class="table-container">
        <el-table :data="users" v-loading="loading" stripe border class="data-table">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="role" label="角色" width="120">
            <template #default="{ row }">
              <el-tag :type="row.role === 'super_admin' ? 'danger' : 'success'" size="small">
                {{ row.role === 'super_admin' ? '超级管理员' : row.role === 'admin' ? '管理员' : '农场主' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-switch
                :model-value="row.status === 'active'"
                :disabled="row.id === userStore.userInfo?.id"
                @change="(val) => handleStatusChange(row.id, val ? 'active' : 'inactive')"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
              <el-button
                size="small"
                type="danger"
                :disabled="row.id === userStore.userInfo?.id"
                @click="handleDelete(row.id)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="loadUsers"
            @size-change="loadUsers"
          />
        </div>
      </div>
    </main>

    <el-dialog
      v-model="showDialog"
      :title="editMode ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            :placeholder="editMode ? '留空则不修改' : '请输入密码'"
          />
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

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
}

.search-input {
  width: 260px;
}

.table-container {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
}

.data-table {
  width: 100%;
}

.pagination-wrapper {
  padding: 16px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table__header th) {
  color: rgba(255, 255, 255, 0.8);
  background: rgba(0, 0, 0, 0.2);
}

:deep(.el-table__body td) {
  color: rgba(255, 255, 255, 0.9);
}

:deep(.el-table) {
  background: transparent;
}

:deep(.el-table__body tr:hover) {
  background: rgba(170, 59, 255, 0.1);
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
</style>
