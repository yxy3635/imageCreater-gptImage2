<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import * as THREE from 'three'

const host = ref<HTMLDivElement>()
let renderer: THREE.WebGLRenderer | null = null
let scene: THREE.Scene | null = null
let camera: THREE.PerspectiveCamera | null = null
let group: THREE.Group | null = null
let frame = 0

onMounted(() => {
  if (!host.value) return
  scene = new THREE.Scene()
  camera = new THREE.PerspectiveCamera(50, 1, 0.1, 100)
  camera.position.z = 8
  renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true })
  renderer.setSize(220, 220)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  host.value.appendChild(renderer.domElement)
  group = new THREE.Group()
  const geometry = new THREE.BufferGeometry()
  const count = 360
  const positions = new Float32Array(count * 3)
  for (let i = 0; i < count; i++) {
    const angle = (i / count) * Math.PI * 2
    const radius = 2.1 + Math.sin(i * 0.31) * 0.35
    positions.set([Math.cos(angle) * radius, Math.sin(angle) * radius, Math.sin(angle * 3) * 0.7], i * 3)
  }
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  group.add(new THREE.Points(geometry, new THREE.PointsMaterial({ color: '#4f46e5', size: 0.08, transparent: true, opacity: 0.8 })))
  scene.add(group)
  const tick = () => {
    frame = requestAnimationFrame(tick)
    if (group && renderer && scene && camera) {
      group.rotation.x += 0.012
      group.rotation.y += 0.018
      renderer.render(scene, camera)
    }
  }
  tick()
})

onUnmounted(() => {
  cancelAnimationFrame(frame)
  renderer?.dispose()
  host.value?.replaceChildren()
})
</script>

<template>
  <div ref="host" class="grid place-items-center" />
</template>
