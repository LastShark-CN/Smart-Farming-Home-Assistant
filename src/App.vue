<script setup>
import { RouterView, useRouter } from 'vue-router'
import { onMounted, onUnmounted } from 'vue'
import WatchingEyes from './components/WatchingEyes.vue'

const router = useRouter()
const konamiCode = [
  'ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown',
  'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight',
  'KeyB', 'KeyA'
]
let konamiIndex = 0

function handleKeydown(e) {
  if (e.code === konamiCode[konamiIndex]) {
    konamiIndex++
    if (konamiIndex === konamiCode.length) {
      konamiIndex = 0
      router.push('/easter-egg')
    }
  } else {
    konamiIndex = 0
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <WatchingEyes />
  <RouterView />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  color: white;
}

#app {
  width: 100%;
  min-height: 100vh;
}
</style>
