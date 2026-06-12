<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import * as THREE from 'three'

const container = ref<HTMLDivElement>()

let renderer: THREE.WebGLRenderer | null = null
let scene: THREE.Scene | null = null
let camera: THREE.PerspectiveCamera | null = null
let particles: THREE.Points | null = null
let frameId = 0

const count = 60000;
let positions: Float32Array;
let scales: Float32Array;

const vertexShader = `
  uniform float uTime;
  attribute float aScale;
  varying vec3 vColor;
  
  void main() {
    vec3 pos = position;
    
    // Organic wave distortion
    float noise = sin(pos.x * 0.4 + uTime * 0.4) * cos(pos.z * 0.4 + uTime * 0.3) * 1.5;
    noise += sin(pos.x * 0.8 - uTime * 0.6) * 0.5;
    noise += cos(pos.z * 1.2 + uTime * 0.8) * 0.3;
    
    pos.y += noise;
    
    // Dynamic color mixing based on wave height
    float mixRatio = (noise + 2.0) / 4.0;
    vec3 color1 = vec3(0.0, 0.64, 1.0);   // #00A3FF
    vec3 color2 = vec3(0.4, 0.2, 0.9);    // Deep Indigo/Purple
    vec3 color3 = vec3(0.0, 0.9, 1.0);    // Cyan
    
    if(mixRatio < 0.5) {
      vColor = mix(color1, color2, mixRatio * 2.0);
    } else {
      vColor = mix(color2, color3, (mixRatio - 0.5) * 2.0);
    }
    
    vec4 mvPosition = modelViewMatrix * vec4(pos, 1.0);
    
    // Size attenuation
    gl_PointSize = aScale * (20.0 / -mvPosition.z);
    gl_Position = projectionMatrix * mvPosition;
  }
`

const fragmentShader = `
  varying vec3 vColor;
  void main() {
    vec2 xy = gl_PointCoord.xy - vec2(0.5);
    float ll = length(xy);
    if(ll > 0.5) discard;
    
    // Soft particle edge
    float alpha = pow((0.5 - ll) * 2.0, 1.5);
    gl_FragColor = vec4(vColor, alpha * 0.7);
  }
`

function init() {
  if (!container.value) return

  scene = new THREE.Scene()
  // Light fog to blend into the white background
  scene.fog = new THREE.FogExp2(0xffffff, 0.04)

  camera = new THREE.PerspectiveCamera(45, container.value.clientWidth / container.value.clientHeight, 1, 100)
  camera.position.set(0, 4, 15)

  renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true })
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  renderer.setSize(container.value.clientWidth, container.value.clientHeight)
  container.value.appendChild(renderer.domElement)

  const geometry = new THREE.BufferGeometry()
  positions = new Float32Array(count * 3)
  scales = new Float32Array(count)

  let i = 0
  const width = 200;
  const depth = 300;
  for (let ix = 0; ix < width; ix++) {
    for (let iy = 0; iy < depth; iy++) {
      // Create a grid with slight random offset for organic feel
      const x = (ix - width / 2) * 0.2 + (Math.random() - 0.5) * 0.1;
      const z = (iy - depth / 2) * 0.2 + (Math.random() - 0.5) * 0.1;
      
      positions[i * 3] = x;
      positions[i * 3 + 1] = 0;
      positions[i * 3 + 2] = z;
      
      scales[i] = Math.random() * 2.0 + 0.5;
      i++;
    }
  }

  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('aScale', new THREE.BufferAttribute(scales, 1))

  const material = new THREE.ShaderMaterial({
    uniforms: {
      uTime: { value: 0 }
    },
    vertexShader,
    fragmentShader,
    transparent: true,
    depthWrite: false,
    blending: THREE.AdditiveBlending
  })

  particles = new THREE.Points(geometry, material)
  particles.rotation.x = -0.15
  scene.add(particles)

  let mouseX = 0;
  let mouseY = 0;

  const onMouseMove = (event: MouseEvent) => {
    mouseX = (event.clientX / window.innerWidth) * 2 - 1;
    mouseY = -(event.clientY / window.innerHeight) * 2 + 1;
  }
  window.addEventListener('mousemove', onMouseMove)

  const clock = new THREE.Clock()

  const animate = () => {
    frameId = requestAnimationFrame(animate)
    
    // Smooth camera movement based on mouse
    const targetX = mouseX * 4;
    const targetY = 4 + mouseY * 2;
    
    if (camera) {
      camera.position.x += (targetX - camera.position.x) * 0.02;
      camera.position.y += (targetY - camera.position.y) * 0.02;
      camera.lookAt(0, 0, 0);
    }

    if (particles && particles.material instanceof THREE.ShaderMaterial && particles.material.uniforms.uTime) {
      particles.material.uniforms.uTime.value = clock.getElapsedTime() * 0.8;
    }

    if (renderer && scene && camera) {
      renderer.render(scene, camera)
    }
  }

  animate()

  const onResize = () => {
    if (!container.value || !camera || !renderer) return
    camera.aspect = container.value.clientWidth / container.value.clientHeight
    camera.updateProjectionMatrix()
    renderer.setSize(container.value.clientWidth, container.value.clientHeight)
  }

  window.addEventListener('resize', onResize)

  // Attach cleanup to container
  ;(container.value as any)._cleanup = () => {
    window.removeEventListener('mousemove', onMouseMove)
    window.removeEventListener('resize', onResize)
    geometry.dispose()
    material.dispose()
    renderer?.dispose()
  }
}

onMounted(init)

onUnmounted(() => {
  cancelAnimationFrame(frameId)
  if (container.value && (container.value as any)._cleanup) {
    (container.value as any)._cleanup()
  }
})
</script>

<template>
  <div ref="container" class="absolute inset-0 pointer-events-none" />
</template>
