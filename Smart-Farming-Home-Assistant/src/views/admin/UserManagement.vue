<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import ParticleBackground from '../../components/ParticleBackground.vue'
import ThreeScene from '../../components/ThreeScene.vue'
import request from '../../utils/request'

const router = useRouter()
const userStore = useUserStore()

const users = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')

const loadUsers = async () => {
  try {
 const response = await request.get('/api/admin/users', {
 params: {
 page: currentPage.value,
 size: pageSize.value,
 keyword: keyword.value || undefined
 }
 });
 if (response.data) {
 users.value = response.data.list;
 total.value = response.data.total;
 }
  } catch (error) {
 console.error('加载用户列表失败:', error);
 users.value = [];
  }
}

const handleSearch = () => {
  currentPage.value = 1;
  loadUsers();
}

const handleReset = () => {
  keyword.value = '';
  currentPage.value = 1;
  loadUsers();
}

const handleStatusChange = async (user) => {
  try {
 await ElMessageBox.confirm(`确定要${user.status === 'active' ? '禁用' : '启用'}用户 ${user.username} 吗？`, '提示', {
 confirmButtonText: '确定',
 cancelButtonText: '取消',
 type: 'warning'
 });
 const response = await request.put(`/api/admin/users/${user.id}/status`, {
 status: user.status === 'active' ? 'inactive' : 'active'
 });
 if (response.code === 200) {
 ElMessage.success('状态更新成功');
 loadUsers();
 }
  } catch (error) {
 if (error !== 'cancel') {
 ElMessage.error('操作失败');
 }
  }
}

const handleEdit = (user) => {
  ElMessage.info('编辑功能开发中');
}

const handleDelete = async (user) => {
  try {
 await ElMessageBox.confirm(`确定要删除用户 ${user.username} 吗？`, '提示', {
 confirmButtonText: '确定',
 cancelButtonText: '取消',
 type: 'warning'
 });
 ElMessage.info('删除功能开发中');
  } catch (error) {
 if (error !== 'cancel') {
 ElMessage.error('操作失败');
 }
  }
}

onMounted(() => {
  loadUsers();
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
        <h1>用户管理</h1>
        <p>管理系统中的所有用户</p>
      </header>

      <div class="search-section">
        <input
          v-model="keyword"
          type="text"
          placeholder="搜索用户名或邮箱"
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <button class="search-btn" @click="handleSearch">搜索</button>
        <button class="reset-btn" @click="handleReset">重置</button>
      </div>

      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>
                <span :class="['role-badge', user.role]">
                  {{ user.role === 'super_admin' ? '超级管理员' : '农场主' }}
                </span>
              </td>
              <td>
                <button 
                  :class="['status-btn', user.status]"
                  @click="handleStatusChange(user)"
                >
                  {{ user.status === 'active' ? '活跃' : '禁用' }}
                </button>
              </td>
              <td>{{ user.createTime || '-' }}</td>
              <td>
                <button class="action-btn edit" @click="handleEdit(user)">编辑</button>
                <button class="action-btn delete" @click="handleDelete(user)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="pagination-container">
          <span class="total-count">共 {{ total }} 条记录</span>
          <div class="pagination">
            <button 
              class="page-btn" 
              :disabled="currentPage <= 1"
              @click="currentPage--; loadUsers()"
            >上一页</button>
            <span class="current-page">{{ currentPage }}</span>
            <button 
              class="page-btn" 
              :disabled="currentPage * pageSize >= total"
              @click="currentPage++; loadUsers()"
            >下一页</button>
          </div>
        </div>
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

.table-container {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 16px 20px;
  text-align: left;
}

.data-table th {
  background: rgba(0, 0, 0, 0.2);
  color: rgba(255, 255, 255, 0.8);
  font-weight: 600;
  font-size: 14px;
}

.data-table tbody tr {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  transition: background 0.3s ease;
}

.data-table tbody tr:hover {
  background: rgba(255, 255, 255, 0.05);
}

.role-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.super_admin {
  background: rgba(170, 59, 255, 0.2);
  color: #aa3bff;
}

.role-badge.farmer {
  background: rgba(78, 205, 196, 0.2);
  color: #4ecdc4;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
}

.status-badge.inactive {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-right: 8px;
}

.action-btn.edit {
  background: rgba(170, 59, 255, 0.2);
  color: #aa3bff;
}

.action-btn.edit:hover {
  background: rgba(170, 59, 255, 0.3);
}

.action-btn.delete {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.action-btn.delete:hover {
  background: rgba(255, 107, 107, 0.3);
}

.search-section {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  color: white;
  font-size: 14px;
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.search-input:focus {
  outline: none;
  border-color: #aa3bff;
}

.search-btn,
.reset-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn {
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  color: white;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(170, 59, 255, 0.4);
}

.reset-btn {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.9);
}

.reset-btn:hover {
  background: rgba(255, 255, 255, 0.2);
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

.status-btn.active {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
}

.status-btn.active:hover {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.status-btn.inactive {
  background: rgba(255, 107, 107, 0.2);
  color: #ff6b6b;
}

.status-btn.inactive:hover {
  background: rgba(72, 187, 120, 0.2);
  color: #48bb78;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.total-count {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-btn {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 6px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: rgba(170, 59, 255, 0.3);
  border-color: #aa3bff;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.current-page {
  color: #aa3bff;
  font-size: 16px;
  font-weight: 600;
  min-width: 30px;
  text-align: center;
}
</style>
