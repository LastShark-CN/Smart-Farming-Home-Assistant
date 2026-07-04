<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import ParticleBackground from '../components/ParticleBackground.vue'
import ThreeScene from '../components/ThreeScene.vue'

const router = useRouter()
const userStore = useUserStore()
const isLogin = ref(true)
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const username = ref('')
const isLoading = ref(false)

function toggleMode() {
  isLogin.value = !isLogin.value
}

async function handleSubmit() {
  if (!email.value || !password.value) {
    ElMessage.warning('请填写邮箱和密码')
    return
  }

  if (!isLogin.value && (!username.value || !confirmPassword.value)) {
    ElMessage.warning('请填写完整信息')
    return
  }

  if (!isLogin.value && password.value !== confirmPassword.value) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  isLoading.value = true

  try {
    if (isLogin.value) {
      await userStore.login({ email: email.value, password: password.value })
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      await userStore.register({
        username: username.value,
        email: email.value,
        password: password.value,
        confirmPassword: confirmPassword.value
      })
      ElMessage.success('注册成功，请登录')
      isLogin.value = true
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    isLoading.value = false
  }
}

function goHome() {
  router.push('/')
}
</script>

<template>
  <div class="login-container">
    <ParticleBackground />
    <ThreeScene />
    
    <div class="glass-card">
      <div class="logo-section" @click="goHome">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
            <path d="M2 17l10 5 10-5"></path>
            <path d="M2 12l10 5 10-5"></path>
          </svg>
        </div>
        <h1>智慧农业助手</h1>
      </div>

      <div class="tabs">
        <button
          class="tab-btn"
          :class="{ active: isLogin }"
          @click="isLogin = true"
        >
          登录
        </button>
        <button
          class="tab-btn"
          :class="{ active: !isLogin }"
          @click="isLogin = false"
        >
          注册
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="form">
        <div v-if="!isLogin" class="form-group">
          <label>用户名</label>
          <input
            v-model="username"
            type="text"
            placeholder="请输入用户名"
            required
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>邮箱</label>
          <input
            v-model="email"
            type="email"
            placeholder="请输入邮箱地址"
            required
            class="form-input"
          />
        </div>

        <div class="form-group">
          <label>密码</label>
          <input
            v-model="password"
            type="password"
            placeholder="请输入密码"
            required
            class="form-input"
          />
        </div>

        <div v-if="!isLogin" class="form-group">
          <label>确认密码</label>
          <input
            v-model="confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            required
            class="form-input"
          />
        </div>

        <button type="submit" class="submit-btn" :disabled="isLoading">
          <span v-if="isLoading" class="loading-spinner"></span>
          {{ isLoading ? (isLogin ? '登录中...' : '注册中...') : (isLogin ? '登 录' : '注 册') }}
        </button>
      </form>

      <div class="toggle-link" @click="toggleMode">
        {{ isLogin ? '还没有账号？点击注册' : '已有账号？点击登录' }}
      </div>

      <div class="divider">
        <span>或</span>
      </div>

      <div class="social-buttons">
        <button class="social-btn">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.477 2 2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.879V14.89h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-.443 2.89h-2.33v6.989C18.343 21.129 22 16.99 22 12c0-5.523-4.477-10-10-10z"/>
          </svg>
        </button>
        <button class="social-btn">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.477 2 2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.879V14.89h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-.443 2.89h-2.33v6.989C18.343 21.129 22 16.99 22 12c0-5.523-4.477-10-10-10z"/>
          </svg>
        </button>
        <button class="social-btn">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 10;
}

.glass-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  padding: 48px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-section {
  text-align: center;
  margin-bottom: 32px;
  cursor: pointer;
}

.logo-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 20px rgba(170, 59, 255, 0.4);
  transition: transform 0.3s ease;
}

.logo-section:hover .logo-icon {
  transform: scale(1.05);
}

.logo-icon svg {
  width: 36px;
  height: 36px;
}

.logo-section h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 32px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 12px;
  padding: 4px;
}

.tab-btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tab-btn.active {
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  color: white;
  box-shadow: 0 4px 15px rgba(170, 59, 255, 0.4);
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 16px;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.form-input:focus {
  outline: none;
  border-color: #aa3bff;
  box-shadow: 0 0 0 3px rgba(170, 59, 255, 0.3);
  background: rgba(255, 255, 255, 0.15);
}

.submit-btn {
  padding: 16px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  color: white;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 12px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(170, 59, 255, 0.5);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.toggle-link {
  text-align: center;
  margin-top: 20px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.toggle-link:hover {
  color: #aa3bff;
}

.divider {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 24px 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: rgba(255, 255, 255, 0.2);
}

.divider span {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
}

.social-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.social-btn {
  width: 48px;
  height: 48px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: white;
}

.social-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
  border-color: #aa3bff;
}

.social-btn svg {
  width: 24px;
  height: 24px;
}
</style>
