<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage, ElInput, ElButton, ElMessageBox } from 'element-plus'
import { ChatRound, Delete, ArrowLeft } from '@element-plus/icons-vue'
import { sendMessage, getChatHistory, clearChatHistory } from '../api/chat'
import vueLive2d from 'vue-live2d'

const router = useRouter()
const userStore = useUserStore()

const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const chatContainer = ref(null)

const currentExpression = ref('')

const expressionMap = {
  happy: 'click',
  sad: 'hover',
  surprised: 'shake',
  blink: 'blink',
  shy: 'touch',
  normal: 'idle',
  talking: 'click',
  thinking: 'hover'
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const handleSend = async () => {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return
  
  const userMsg = {
    id: Date.now(),
    role: 'user',
    content: message,
    timestamp: new Date().toLocaleString()
  }
  
  messages.value.push(userMsg)
  inputMessage.value = ''
  scrollToBottom()
  
  isLoading.value = true
  currentExpression.value = 'thinking'
  
  try {
    const response = await sendMessage({ content: message })
    
    if (response.data) {
      const botMsg = {
        id: Date.now() + 1,
        role: 'bot',
        content: response.data.content,
        expression: response.data.expression || 'happy',
        timestamp: new Date().toLocaleString()
      }
      
      messages.value.push(botMsg)
      
      if (botMsg.expression) {
        setTimeout(() => {
          triggerExpression(botMsg.expression)
        }, 500)
      }
    }
  } catch (error) {
    ElMessage.error('发送失败，请重试')
    
    const fallbackMsg = {
      id: Date.now() + 1,
      role: 'bot',
      content: '抱歉，我现在无法响应您的请求，请稍后再试。',
      expression: 'sad',
      timestamp: new Date().toLocaleString()
    }
    messages.value.push(fallbackMsg)
    setTimeout(() => triggerExpression('sad'), 500)
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

const triggerExpression = (expressionName) => {
  currentExpression.value = expressionName
  
  const action = expressionMap[expressionName]
  if (!action) return
  
  const canvas = document.querySelector('.vue-live2d-main')
  if (!canvas) return
  
  switch (action) {
    case 'click':
      canvas.click()
      break
    case 'hover':
      canvas.dispatchEvent(new MouseEvent('mouseover'))
      setTimeout(() => canvas.dispatchEvent(new MouseEvent('mouseout')), 1000)
      break
    case 'shake':
      window.scrollTo({ top: window.scrollY + 10, behavior: 'smooth' })
      setTimeout(() => window.scrollTo({ top: window.scrollY - 10, behavior: 'smooth' }), 100)
      break
    case 'touch':
      const touchEvent = new TouchEvent('touchstart', {
        touches: [{ clientX: canvas.offsetLeft + canvas.width / 2, clientY: canvas.offsetTop + canvas.height / 2 }]
      })
      canvas.dispatchEvent(touchEvent)
      break
    case 'idle':
      canvas.dispatchEvent(new MouseEvent('mouseout'))
      break
  }
}

const handleKeydown = (e) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    handleSend()
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空聊天记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await clearChatHistory()
    messages.value = []
    ElMessage.success('聊天记录已清空')
  } catch (error) {
    console.log('用户取消清空')
  }
}

const goBack = () => {
  router.push('/dashboard')
}

onMounted(() => {
  getChatHistory({ page: 1, size: 50 })
    .then(response => {
      if (response.data) {
        messages.value = response.data.list || response.data
      }
    })
    .catch(() => {
      messages.value = [
        {
          id: 1,
          role: 'bot',
          content: '你好！我是您的智慧农业助手，很高兴为您服务。请问有什么可以帮助您的吗？',
          expression: 'happy',
          timestamp: new Date().toLocaleString()
        }
      ]
    })
    .finally(() => {
      scrollToBottom()
    })
})

onUnmounted(() => {
})
</script>

<template>
  <div class="ai-chat-container">
    <nav class="navbar">
      <div class="nav-content">
        <div class="header-left">
          <button class="back-btn" @click="goBack">
            <ArrowLeft />
          </button>
          <div class="logo" @click="router.push('/')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
              <path d="M2 17l10 5 10-5"></path>
              <path d="M2 12l10 5 10-5"></path>
            </svg>
            <span>智慧农业助手</span>
          </div>
        </div>
        <div class="header-right">
          <ElButton 
            type="danger" 
            circle 
            size="small"
            @click="handleClear"
            title="清空聊天记录"
          >
            <Delete />
          </ElButton>
        </div>
      </div>
    </nav>
    
    <main class="chat-messages" ref="chatContainer">
      <div 
        v-for="msg in messages" 
        :key="msg.id" 
        class="message-item"
        :class="msg.role"
      >
        <div v-if="msg.role === 'bot'" class="bot-avatar">
          <vue-live2d 
            :model="['Potion-Maker/Pio', 'school-2017-costume-yellow']" 
            :size="50"
            direction="right"
            tip-position="top"
            api-path="https://cdn.jsdelivr.net/npm/live2d-static-api@latest/indexes"
          />
        </div>
        
        <div class="message-content">
          <div class="message-bubble">
            <p>{{ msg.content }}</p>
          </div>
          <span class="message-time">{{ msg.timestamp }}</span>
        </div>
        
        <div v-if="msg.role === 'user'" class="user-avatar">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
        </div>
      </div>
      
      <div v-if="isLoading" class="loading-indicator">
        <div class="loading-dots">
          <span></span>
          <span></span>
          <span></span>
        </div>
        <span>正在思考中...</span>
      </div>
    </main>
    
    <div class="chat-input-area">
      <div class="live2d-display">
        <vue-live2d 
          :model="['Potion-Maker/Pio', 'school-2017-costume-yellow']" 
          :size="120"
          direction="right"
          tip-position="top"
          api-path="https://cdn.jsdelivr.net/npm/live2d-static-api@latest/indexes"
        />
      </div>
      <div class="input-wrapper">
        <ElInput
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="输入消息..."
          :disabled="isLoading"
          @keydown="handleKeydown"
          class="message-input"
        />
        <ElButton 
          type="primary" 
          :loading="isLoading"
          :disabled="!inputMessage.trim() || isLoading"
          @click="handleSend"
          class="send-btn"
        >
          <ChatRound />
        </ElButton>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ai-chat-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.2);
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

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 80px 24px 180px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.chat-messages::-webkit-scrollbar-thumb {
  background: rgba(139, 92, 246, 0.5);
  border-radius: 3px;
}

.message-item {
  display: flex;
  gap: 12px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.user {
  flex-direction: row-reverse;
}

.bot-avatar {
  flex-shrink: 0;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(139, 92, 246, 0.5);
  background: transparent;
}

.bot-avatar :deep(.vue-live2d-main) {
  background: transparent !important;
  border: none !important;
}

.user-avatar {
  flex-shrink: 0;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(59, 130, 246, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 70%;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
}

.message-item.user .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.bot .message-bubble {
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.9);
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
}

.message-item.user .message-time {
  text-align: right;
}

.loading-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #8b5cf6;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.chat-input-area {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: flex-end;
  gap: 16px;
  padding: 16px 24px;
  background: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.live2d-display {
  flex-shrink: 0;
  width: 120px;
  height: 120px;
  background: transparent;
}

.live2d-display :deep(.vue-live2d-main) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

.live2d-display :deep(.vue-live2d-toolbar) {
  display: none !important;
}

.input-wrapper {
  flex: 1;
  display: flex;
  gap: 12px;
}

.message-input {
  flex: 1;
}

.message-input :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 12px;
}

.message-input :deep(.el-textarea__inner::placeholder) {
  color: rgba(255, 255, 255, 0.4);
}

.send-btn {
  align-self: flex-end;
  border-radius: 12px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.send-btn:hover {
  opacity: 0.9;
}

.send-btn:disabled {
  opacity: 0.5;
}

@media (max-width: 768px) {
  .live2d-display {
    width: 80px;
    height: 80px;
  }
  
  .chat-messages {
    padding: 80px 16px 150px;
  }
  
  .message-content {
    max-width: 80%;
  }
  
  .nav-content {
    gap: 12px;
  }
  
  .logo span {
    display: none;
  }
}
</style>