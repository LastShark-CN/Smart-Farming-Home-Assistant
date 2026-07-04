<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import ParticleBackground from '../components/ParticleBackground.vue'
import ThreeScene from '../components/ThreeScene.vue'

const router = useRouter()
const userStore = useUserStore()
const isVisible = ref(false)

onMounted(() => {
  setTimeout(() => {
    isVisible.value = true
  }, 100)
})

function goToLogin() {
  router.push('/login')
}

function goToDashboard() {
  router.push('/dashboard')
}

function handleLogout() {
  userStore.logout()
  ElMessage.success('退出成功')
}
</script>

<template>
  <div class="home-container">
    <ParticleBackground />
    <ThreeScene />

    <nav class="navbar">
      <div class="nav-content">
        <div class="logo" @click="goToLogin">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
            <path d="M2 17l10 5 10-5"></path>
            <path d="M2 12l10 5 10-5"></path>
          </svg>
          <span>智慧农业</span>
        </div>
        <div class="nav-links">
          <template v-if="userStore.isLoggedIn">
            <button class="user-btn" @click="goToDashboard">
              <span class="user-icon">👤</span>
              <span>{{ userStore.username }}</span>
            </button>
            <button class="logout-btn" @click="handleLogout">退出</button>
          </template>
          <button v-else class="login-btn" @click="goToLogin">登录</button>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <section class="hero" :class="{ visible: isVisible }">
        <div class="hero-content">
          <h1 class="hero-title">
            智慧农业
            <span class="gradient-text">智能助手</span>
          </h1>
          <p class="hero-subtitle">
            让农业生产更高效、更智能、更可持续
          </p>
          <div class="hero-buttons">
            <template v-if="userStore.isLoggedIn">
              <button class="btn-primary" @click="goToDashboard">进入控制台</button>
            </template>
            <template v-else>
              <button class="btn-primary" @click="goToLogin">开始使用</button>
            </template>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.home-container {
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
  max-width: 1200px;
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
  transition: transform 0.3s ease;
}

.logo:hover {
  transform: scale(1.02);
}

.logo svg {
  width: 32px;
  height: 32px;
  color: #aa3bff;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-links a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 16px;
  transition: color 0.3s ease;
}

.nav-links a:hover {
  color: #aa3bff;
}

.login-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(170, 59, 255, 0.4);
}

.user-btn {
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

.user-btn:hover {
  background: rgba(170, 59, 255, 0.2);
  border-color: #aa3bff;
}

.user-icon {
  font-size: 16px;
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
  padding-top: 80px;
}

.hero {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s ease-out;
}

.hero.visible {
  opacity: 1;
  transform: translateY(0);
}

.hero-content {
  max-width: 800px;
  padding: 0 24px;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  color: white;
  margin-bottom: 20px;
  line-height: 1.2;
}

.gradient-text {
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 40px;
  line-height: 1.6;
}

.hero-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 80px;
}

.btn-primary {
  padding: 16px 40px;
  background: linear-gradient(135deg, #aa3bff, #00d4ff);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(170, 59, 255, 0.5);
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 40px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .nav-links {
    gap: 12px;
  }

  .login-btn {
    padding: 8px 16px;
    font-size: 14px;
  }
}
</style>
