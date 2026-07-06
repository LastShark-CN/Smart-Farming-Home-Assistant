<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const canvasRef = ref(null)
let ctx = null
let animationId = null
let particles = []

const config = {
  particleCount: 80,
  particleSize: { min: 2, max: 6 },
  particleSpeed: 1.5,
  linkDistance: 150,
  linkOpacity: 0.4,
  colors: ['#aa3bff', '#00d4ff', '#ff6b6b', '#4ecdc4']
}

class Particle {
  constructor(canvas) {
    this.canvas = canvas
    this.reset()
  }

  reset() {
    this.x = Math.random() * this.canvas.width
    this.y = Math.random() * this.canvas.height
    this.vx = (Math.random() - 0.5) * config.particleSpeed
    this.vy = (Math.random() - 0.5) * config.particleSpeed
    this.size = Math.random() * (config.particleSize.max - config.particleSize.min) + config.particleSize.min
    this.color = config.colors[Math.floor(Math.random() * config.colors.length)]
    this.opacity = Math.random() * 0.5 + 0.1
  }

  update() {
    this.x += this.vx
    this.y += this.vy

    if (this.x < 0 || this.x > this.canvas.width) this.vx *= -1
    if (this.y < 0 || this.y > this.canvas.height) this.vy *= -1
  }

  draw(ctx) {
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.fillStyle = this.color
    ctx.globalAlpha = this.opacity
    ctx.fill()
    ctx.globalAlpha = 1
  }
}

function initCanvas() {
  const canvas = canvasRef.value
  if (!canvas) return

  ctx = canvas.getContext('2d')
  resizeCanvas()

  particles = []
  for (let i = 0; i < config.particleCount; i++) {
    particles.push(new Particle(canvas))
  }

  animate()
}

function resizeCanvas() {
  const canvas = canvasRef.value
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight

  particles.forEach((p) => {
    p.canvas = canvas
    if (p.x > canvas.width) p.x = canvas.width - 10
    if (p.y > canvas.height) p.y = canvas.height - 10
  })
}

function animate() {
  if (!ctx || !canvasRef.value) return

  ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)

  particles.forEach((p) => {
    p.update()
    p.draw(ctx)
  })

  particles.forEach((p1, i) => {
    particles.slice(i + 1).forEach((p2) => {
      const dx = p1.x - p2.x
      const dy = p1.y - p2.y
      const distance = Math.sqrt(dx * dx + dy * dy)

      if (distance < config.linkDistance) {
        const opacity = (1 - distance / config.linkDistance) * config.linkOpacity
        ctx.beginPath()
        ctx.moveTo(p1.x, p1.y)
        ctx.lineTo(p2.x, p2.y)
        ctx.strokeStyle = config.colors[0]
        ctx.globalAlpha = opacity
        ctx.lineWidth = 1
        ctx.stroke()
        ctx.globalAlpha = 1
      }
    })
  })

  animationId = requestAnimationFrame(animate)
}

onMounted(() => {
  initCanvas()
  window.addEventListener('resize', resizeCanvas)
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('resize', resizeCanvas)
})
</script>

<template>
  <canvas ref="canvasRef" class="particle-background"></canvas>
</template>

<style scoped>
.particle-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 0;
  pointer-events: none;
}
</style>
