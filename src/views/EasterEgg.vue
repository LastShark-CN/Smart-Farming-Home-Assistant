<template>
  <div class="easter-egg" ref="container">
    <canvas ref="canvas" class="glitch-canvas"></canvas>

    <div class="ghost-layer" :class="{ active: ghostActive }">
      <img src="/pictures/01.png" class="abaron-image" />
    </div>

    <img src="/pictures/01.png" class="abaron-image main-image" />

    <div class="text-container">
      <span
        v-for="(char, i) in textChars"
        :key="i"
        class="glitch-char"
        :data-text="char"
        :style="{ transform: `translate(${charOffsets[i].x}px, ${charOffsets[i].y}px)` }"
      >{{ char }}</span>
    </div>

    <div class="scanlines"></div>
    <div class="noise"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive } from 'vue'

const container = ref(null)
const canvas = ref(null)
const ghostActive = ref(false)

const text = '被冠以亚波伦之名的终焉在此。'
const textChars = text.split('')
const charOffsets = reactive(textChars.map(() => ({ x: 0, y: 0 })))
let textAnimFrame = null

function animateText() {
  const t = Date.now() / 1000
  for (let i = 0; i < textChars.length; i++) {
    const phase = i * 0.7
    charOffsets[i].x = Math.sin(t * 1.7 + phase) * 60
    charOffsets[i].y = Math.cos(t * 2.3 + phase * 1.3) * 60
  }
  textAnimFrame = requestAnimationFrame(animateText)
}

let ghostInterval = null
let glitchInterval = null

onMounted(() => {
  const cvs = canvas.value
  const ctx = cvs.getContext('2d')

  function resize() {
    cvs.width = window.innerWidth
    cvs.height = window.innerHeight
  }
  resize()
  window.addEventListener('resize', resize)

  const redLines = []
  for (let i = 0; i < 30; i++) {
    redLines.push({
      x: Math.random() * cvs.width,
      y: Math.random() * cvs.height,
      length: Math.random() * 200 + 50,
      angle: Math.random() * Math.PI * 2,
      speed: Math.random() * 4 + 2,
      opacity: Math.random(),
      flickerSpeed: Math.random() * 0.3 + 0.05,
      thickness: Math.random() * 3 + 1,
      visible: true,
      timer: 0,
      duration: Math.random() * 60 + 20
    })
  }

  function drawGlitch() {
    ctx.fillStyle = 'rgba(0, 0, 0, 0.15)'
    ctx.fillRect(0, 0, cvs.width, cvs.height)

    const time = Date.now() / 1000

    if (Math.random() < 0.08) {
      const blockH = Math.random() * 80 + 10
      const blockY = Math.random() * cvs.height
      const shift = (Math.random() - 0.5) * 40
      ctx.save()
      ctx.globalAlpha = 0.6
      ctx.fillStyle = `rgba(${Math.random() > 0.5 ? '255,0,0' : '0,255,255'}, 0.1)`
      ctx.fillRect(shift, blockY, cvs.width, blockH)
      ctx.restore()
    }

    redLines.forEach(line => {
      line.timer++
      if (line.timer > line.duration) {
        line.visible = !line.visible
        line.timer = 0
        line.duration = Math.random() * 80 + 15
        line.x = Math.random() * cvs.width
        line.y = Math.random() * cvs.height
        line.angle = Math.random() * Math.PI * 2
      }

      if (!line.visible) return

      const flicker = Math.sin(time * line.flickerSpeed * 60) * 0.5 + 0.5
      if (flicker < 0.3) return

      line.x += Math.cos(line.angle) * line.speed
      line.y += Math.sin(line.angle) * line.speed

      if (line.x < -200 || line.x > cvs.width + 200 || line.y < -200 || line.y > cvs.height + 200) {
        line.x = Math.random() * cvs.width
        line.y = Math.random() * cvs.height
        line.angle = Math.random() * Math.PI * 2
      }

      ctx.save()
      ctx.globalAlpha = flicker * line.opacity
      ctx.strokeStyle = `rgb(${200 + Math.random() * 55}, ${Math.random() * 30}, ${Math.random() * 30})`
      ctx.lineWidth = line.thickness
      ctx.shadowColor = '#ff0000'
      ctx.shadowBlur = 8
      ctx.beginPath()
      ctx.moveTo(line.x, line.y)
      ctx.lineTo(
        line.x + Math.cos(line.angle) * line.length,
        line.y + Math.sin(line.angle) * line.length
      )
      ctx.stroke()
      ctx.restore()
    })

    if (Math.random() < 0.03) {
      ctx.save()
      ctx.globalAlpha = 0.15
      ctx.fillStyle = '#ff0000'
      for (let i = 0; i < 5; i++) {
        const sx = Math.random() * cvs.width
        const sy = Math.random() * cvs.height
        const sw = Math.random() * 300 + 50
        const sh = Math.random() * 3 + 1
        ctx.fillRect(sx, sy, sw, sh)
      }
      ctx.restore()
    }

    if (Math.random() < 0.02) {
      ctx.save()
      ctx.globalAlpha = 0.08
      ctx.filter = `hue-rotate(${Math.random() * 360}deg)`
      ctx.drawImage(cvs, (Math.random() - 0.5) * 20, (Math.random() - 0.5) * 20)
      ctx.restore()
    }

    glitchInterval = requestAnimationFrame(drawGlitch)
  }

  drawGlitch()

  ghostInterval = setInterval(() => {
    ghostActive.value = true
    setTimeout(() => { ghostActive.value = false }, 150 + Math.random() * 200)
  }, 3000 + Math.random() * 4000)

  animateText()
})

onUnmounted(() => {
  if (glitchInterval) cancelAnimationFrame(glitchInterval)
  if (ghostInterval) clearInterval(ghostInterval)
  if (textAnimFrame) cancelAnimationFrame(textAnimFrame)
})
</script>

<style scoped>
@font-face {
  font-family: 'K8x12XiangSuTi';
  src: url('/K8x12XiangSuTi/k8x12S-4.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

.easter-egg {
  position: fixed;
  inset: 0;
  background: #000;
  overflow: hidden;
  z-index: 99999;
}

.glitch-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.abaron-image {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(1.1);
  width: 400px;
  height: auto;
  z-index: 3;
  image-rendering: auto;
  filter: contrast(1.2) saturate(1.3);
}

.ghost-layer {
  position: absolute;
  inset: 0;
  z-index: 2;
  opacity: 0;
  transition: opacity 0.05s;
  pointer-events: none;
}

.ghost-layer.active {
  opacity: 0.4;
}

.ghost-layer .abaron-image {
  filter: hue-rotate(180deg) contrast(1.5) brightness(0.8);
  mix-blend-mode: screen;
  transform: translate(-50%, -50%) scale(1.1);
}

.text-container {
  position: absolute;
  bottom: 15%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  pointer-events: none;
  white-space: nowrap;
}

.glitch-char {
  font-family: 'K8x12XiangSuTi', monospace;
  font-size: 28px;
  color: #ffffff;
  text-shadow:
    0 0 5px rgba(255, 0, 0, 0.8),
    0 0 15px rgba(255, 0, 0, 0.5),
    2px 0 #ff0000,
    -2px 0 #00ffff;
  letter-spacing: 4px;
  display: inline-block;
  padding: 8px 4px;
  border: 2px solid #ff0000;
  box-shadow:
    0 0 10px rgba(255, 0, 0, 0.5),
    inset 0 0 10px rgba(255, 0, 0, 0.2);
  position: relative;
}

.glitch-char::before {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  color: #ff0000;
  z-index: -1;
  animation: glitch-shift-1 0.3s infinite linear alternate-reverse;
}

.glitch-char::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 0;
  color: #00ffff;
  z-index: -2;
  animation: glitch-shift-2 0.2s infinite linear alternate-reverse;
}

.scanlines {
  position: absolute;
  inset: 0;
  z-index: 20;
  pointer-events: none;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 2px,
    rgba(0, 0, 0, 0.15) 2px,
    rgba(0, 0, 0, 0.15) 4px
  );
}

.noise {
  position: absolute;
  inset: -50%;
  z-index: 19;
  pointer-events: none;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.06'/%3E%3C/svg%3E");
  background-size: 256px 256px;
  animation: noise-move 0.5s steps(8) infinite;
  opacity: 0.4;
}

@keyframes glitch-shift-1 {
  0% { clip-path: inset(20% 0 60% 0); transform: translate(-3px, 1px); }
  20% { clip-path: inset(50% 0 20% 0); transform: translate(3px, -2px); }
  40% { clip-path: inset(10% 0 70% 0); transform: translate(-2px, 3px); }
  60% { clip-path: inset(60% 0 10% 0); transform: translate(4px, 0px); }
  80% { clip-path: inset(30% 0 40% 0); transform: translate(-1px, -3px); }
  100% { clip-path: inset(70% 0 5% 0); transform: translate(2px, 2px); }
}

@keyframes glitch-shift-2 {
  0% { clip-path: inset(65% 0 10% 0); transform: translate(3px, -1px); }
  25% { clip-path: inset(15% 0 55% 0); transform: translate(-4px, 2px); }
  50% { clip-path: inset(40% 0 30% 0); transform: translate(2px, -3px); }
  75% { clip-path: inset(5% 0 80% 0); transform: translate(-2px, 1px); }
  100% { clip-path: inset(50% 0 25% 0); transform: translate(3px, -2px); }
}

@keyframes noise-move {
  0% { transform: translate(0, 0); }
  12.5% { transform: translate(-5%, -10%); }
  25% { transform: translate(-10%, 5%); }
  37.5% { transform: translate(5%, -5%); }
  50% { transform: translate(-5%, 10%); }
  62.5% { transform: translate(10%, 0); }
  75% { transform: translate(0, 10%); }
  87.5% { transform: translate(-10%, -5%); }
  100% { transform: translate(5%, 5%); }
}
</style>
