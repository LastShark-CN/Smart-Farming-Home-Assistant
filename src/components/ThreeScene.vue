<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import * as THREE from 'three'

const containerRef = ref(null)
let scene, camera, renderer, geometry, material, mesh, animationId

onMounted(() => {
  init()
  animate()
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  if (renderer) {
    renderer.dispose()
  }
})

function init() {
  scene = new THREE.Scene()
  camera = new THREE.PerspectiveCamera(
    75,
    window.innerWidth / window.innerHeight,
    0.1,
    1000
  )
  camera.position.z = 5

  renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(window.devicePixelRatio)
  containerRef.value.appendChild(renderer.domElement)

  const geometries = [
    new THREE.IcosahedronGeometry(1.2, 0),
    new THREE.TorusGeometry(0.8, 0.2, 16, 100),
    new THREE.OctahedronGeometry(0.6, 0),
    new THREE.TetrahedronGeometry(0.5, 0),
    new THREE.DodecahedronGeometry(0.4, 0)
  ]

  const colors = [
    0xaa3bff,
    0x00d4ff,
    0xff6b6b,
    0x4ecdc4,
    0xffe66d
  ]

  geometries.forEach((geo, i) => {
    const mat = new THREE.MeshPhongMaterial({
      color: colors[i],
      transparent: true,
      opacity: 0.6,
      shininess: 100,
      side: THREE.DoubleSide
    })
    const m = new THREE.Mesh(geo, mat)
    m.position.x = (Math.random() - 0.5) * 8
    m.position.y = (Math.random() - 0.5) * 6
    m.position.z = (Math.random() - 0.5) * 4
    m.userData = {
      rotationSpeed: {
        x: (Math.random() - 0.5) * 0.02,
        y: (Math.random() - 0.5) * 0.02,
        z: (Math.random() - 0.5) * 0.02
      },
      orbitSpeed: {
        x: (Math.random() - 0.5) * 0.005,
        y: (Math.random() - 0.5) * 0.005
      },
      basePosition: m.position.clone()
    }
    scene.add(m)
  })

  const light = new THREE.PointLight(0xaa3bff, 1, 100)
  light.position.set(5, 5, 5)
  scene.add(light)

  const ambientLight = new THREE.AmbientLight(0x404040, 0.5)
  scene.add(ambientLight)

  const pointLight2 = new THREE.PointLight(0x00d4ff, 0.8, 100)
  pointLight2.position.set(-5, -3, 3)
  scene.add(pointLight2)

  window.addEventListener('resize', onWindowResize)
}

function onWindowResize() {
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
  renderer.setSize(window.innerWidth, window.innerHeight)
}

function animate() {
  animationId = requestAnimationFrame(animate)

  scene.children.forEach((child) => {
    if (child instanceof THREE.Mesh && child.userData.rotationSpeed) {
      child.rotation.x += child.userData.rotationSpeed.x
      child.rotation.y += child.userData.rotationSpeed.y
      child.rotation.z += child.userData.rotationSpeed.z

      const time = Date.now() * 0.001
      child.position.x =
        child.userData.basePosition.x +
        Math.sin(time * 2 + child.userData.basePosition.x) * 1.5
      child.position.y =
        child.userData.basePosition.y +
        Math.cos(time * 1.5 + child.userData.basePosition.y) * 1.5
    }
  })

  renderer.render(scene, camera)
}
</script>

<template>
  <div ref="containerRef" class="three-scene"></div>
</template>

<style scoped>
.three-scene {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1;
  pointer-events: none;
}
</style>
