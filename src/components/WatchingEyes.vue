<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const canvasRef = ref(null)
let ctx = null
let animationId = null
let eyes = []
let spawnTimer = 0

const MAX_EYES = 10
const EYE_SIZE_RANGE = { min: 8, max: 20 }
const EYE_SPACING_RANGE = { min: 1.8, max: 2.8 }
const LIFETIME_RANGE = { min: 3000, max: 8000 }
const SPAWN_INTERVAL_RANGE = { min: 1500, max: 5000 }

function rand(min, max) {
  return Math.random() * (max - min) + min
}

function randInt(min, max) {
  return Math.floor(rand(min, max + 1))
}

function createEye(canvas) {
  const size = rand(EYE_SIZE_RANGE.min, EYE_SIZE_RANGE.max)
  const spacing = size * rand(EYE_SPACING_RANGE.min, EYE_SPACING_RANGE.max)
  const lifetime = rand(LIFETIME_RANGE.min, LIFETIME_RANGE.max)
  const margin = 60
  return {
    x: rand(margin, canvas.width - margin),
    y: rand(margin, canvas.height - margin),
    size,
    spacing,
    opacity: 0,
    maxOpacity: rand(0.3, 0.7),
    phase: 'fadeIn',
    phaseTimer: 0,
    lifetime,
    blinkTimer: rand(2000, 5000),
    isBlinking: false,
    blinkPhase: 0,
    pupilOffsetX: 0,
    pupilOffsetY: 0,
    pupilTargetX: 0,
    pupilTargetY: 0,
    lookTimer: rand(1500, 4000),
    lookDuration: rand(600, 1500),
    isLooking: false
  }
}

function drawEye(eye) {
  if (!ctx) return

  const { x, y, size, spacing, opacity, pupilOffsetX, pupilOffsetY, isBlinking, blinkPhase } = eye
  if (opacity <= 0) return

  const eyeW = size
  const eyeH = isBlinking ? size * (1 - blinkPhase) * 0.5 : size * 0.6
  if (eyeH < 1) return

  ctx.save()
  ctx.globalAlpha = opacity

  const eyeY = y

  ctx.beginPath()
  ctx.ellipse(x - spacing / 2, eyeY, eyeW * 0.5, Math.max(eyeH * 0.5, 1), 0, 0, Math.PI * 2)
  ctx.fillStyle = '#0a0a1a'
  ctx.fill()

  ctx.beginPath()
  ctx.ellipse(x + spacing / 2, eyeY, eyeW * 0.5, Math.max(eyeH * 0.5, 1), 0, 0, Math.PI * 2)
  ctx.fillStyle = '#0a0a1a'
  ctx.fill()

  if (!isBlinking || blinkPhase < 0.8) {
    const pupilR = eyeW * 0.22
    const maxOffsetX = eyeW * 0.15
    const maxOffsetY = eyeW * 0.1

    ctx.beginPath()
    ctx.arc(x - spacing / 2 + pupilOffsetX * maxOffsetX, eyeY + pupilOffsetY * maxOffsetY, pupilR, 0, Math.PI * 2)
    ctx.fillStyle = '#ffffff'
    ctx.fill()

    ctx.beginPath()
    ctx.arc(x + spacing / 2 + pupilOffsetX * maxOffsetX, eyeY + pupilOffsetY * maxOffsetY, pupilR, 0, Math.PI * 2)
    ctx.fillStyle = '#ffffff'
    ctx.fill()
  }

  ctx.restore()
}

function updateEyes(dt) {
  const canvas = canvasRef.value
  if (!canvas) return

  for (let i = eyes.length - 1; i >= 0; i--) {
    const eye = eyes[i]
    eye.phaseTimer += dt

    const fadeDuration = 600

    if (eye.phase === 'fadeIn') {
      eye.opacity = Math.min(eye.maxOpacity, (eye.phaseTimer / fadeDuration) * eye.maxOpacity)
      if (eye.phaseTimer >= fadeDuration) {
        eye.phase = 'active'
        eye.phaseTimer = 0
      }
    } else if (eye.phase === 'active') {
      eye.blinkTimer -= dt
      if (eye.blinkTimer <= 0 && !eye.isBlinking) {
        eye.isBlinking = true
        eye.blinkPhase = 0
        eye.blinkTimer = rand(2000, 5000)
      }

      if (eye.isBlinking) {
        eye.blinkPhase += dt / 120
        if (eye.blinkPhase >= 1) {
          eye.isBlinking = false
          eye.blinkPhase = 0
        }
      }

      eye.lookTimer -= dt
      if (eye.lookTimer <= 0 && !eye.isLooking) {
        eye.isLooking = true
        eye.pupilTargetX = rand(-1, 1)
        eye.pupilTargetY = rand(-1, 1)
        eye.lookTimer = rand(1500, 4000)
        eye.lookDuration = rand(600, 1500)
      }

      if (eye.isLooking) {
        const progress = 1 - (eye.lookTimer / eye.lookDuration)
        if (progress >= 1) {
          eye.isLooking = false
          eye.pupilOffsetX = 0
          eye.pupilOffsetY = 0
        } else {
          eye.pupilOffsetX = eye.pupilTargetX * Math.sin(progress * Math.PI * 0.5)
          eye.pupilOffsetY = eye.pupilTargetY * Math.sin(progress * Math.PI * 0.5)
        }
      }

      if (eye.phaseTimer >= eye.lifetime) {
        eye.phase = 'fadeOut'
        eye.phaseTimer = 0
      }
    } else if (eye.phase === 'fadeOut') {
      eye.opacity = Math.max(0, eye.maxOpacity * (1 - eye.phaseTimer / fadeDuration))
      if (eye.phaseTimer >= fadeDuration) {
        eyes.splice(i, 1)
        continue
      }
    }

    drawEye(eye)
  }
}

function animate(timestamp) {
  if (!ctx || !canvasRef.value) return

  if (!animate.lastTime) animate.lastTime = timestamp
  const dt = Math.min(timestamp - animate.lastTime, 50)
  animate.lastTime = timestamp

  ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)

  updateEyes(dt)

  spawnTimer -= dt
  if (spawnTimer <= 0 && eyes.length < MAX_EYES) {
    eyes.push(createEye(canvasRef.value))
    spawnTimer = rand(SPAWN_INTERVAL_RANGE.min, SPAWN_INTERVAL_RANGE.max)
  }

  animationId = requestAnimationFrame(animate)
}

animate.lastTime = 0

function init() {
  const canvas = canvasRef.value
  if (!canvas) return
  ctx = canvas.getContext('2d')
  resize()
  spawnTimer = rand(500, 2000)
  animationId = requestAnimationFrame(animate)
}

function resize() {
  const canvas = canvasRef.value
  if (!canvas) return
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
}

onMounted(() => {
  init()
  window.addEventListener('resize', resize)
})

onUnmounted(() => {
  if (animationId) cancelAnimationFrame(animationId)
  window.removeEventListener('resize', resize)
})
</script>

<template>
  <canvas ref="canvasRef" class="watching-eyes"></canvas>
</template>

<style scoped>
.watching-eyes {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1;
  pointer-events: none;
}
</style>
