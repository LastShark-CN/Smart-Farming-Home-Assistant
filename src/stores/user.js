import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import request from '../utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role || '')
  const username = computed(() => userInfo.value?.username || '')
  const email = computed(() => userInfo.value?.email || '')

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  async function login(loginForm) {
    const { email, password } = loginForm
    const response = await request.post('/login', { email, password })
    
    if (response.data) {
      setToken(response.data.token)
      setUserInfo(response.data.user)
    }
    
    return response
  }

  async function register(registerForm) {
    const { username, email, password, confirmPassword } = registerForm
    const response = await request.post('/register', {
      username,
      email,
      password,
      confirmPassword
    })
    
    return response
  }

  async function getUserInfo() {
    if (!token.value) return null
    
    try {
      const response = await request.get('/user/info')
      if (response.data) {
        setUserInfo(response.data)
      }
      return response.data
    } catch (error) {
      return null
    }
  }

  async function updateProfile(profileData) {
    const response = await request.put('/user/profile', profileData)
    if (response.data) {
      setUserInfo(response.data)
    }
    return response
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    role,
    username,
    email,
    setToken,
    setUserInfo,
    login,
    register,
    getUserInfo,
    updateProfile,
    logout
  }
})
