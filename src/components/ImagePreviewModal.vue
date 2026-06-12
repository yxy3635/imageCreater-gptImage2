<script setup lang="ts">
defineProps<{ src?: string }>()
const emit = defineEmits<{ close: [] }>()

function downloadImage(src: string) {
  const link = document.createElement('a')
  link.href = src
  link.download = `imageCreater-${Date.now()}.png`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
</script>

<template>
  <div v-if="src" class="fixed inset-0 z-50 grid place-items-center bg-slate-950/78 p-4 backdrop-blur-sm" @click.self="emit('close')">
    <div class="flex max-h-[94vh] w-full max-w-6xl flex-col overflow-hidden rounded-[28px] border border-white/20 bg-white p-3 shadow-[0_30px_110px_rgba(2,6,23,0.45)]">
      <div class="min-h-0 flex-1 overflow-auto rounded-3xl bg-slate-950/5">
        <img :src="src" alt="预览图像" class="mx-auto max-h-[78vh] w-full object-contain" />
      </div>
      <div class="mt-3 flex flex-wrap items-center justify-between gap-3 px-1">
        <p class="text-sm font-black text-slate-800">图像预览</p>
        <div class="flex gap-2">
          <button class="rounded-full border border-slate-200 bg-white px-4 py-2 text-sm font-black text-slate-700 transition hover:border-sky-200 hover:bg-sky-50" @click="emit('close')">关闭</button>
          <button class="rounded-full bg-slate-950 px-4 py-2 text-sm font-black text-white transition hover:bg-sky-600" @click="downloadImage(src)">下载图片</button>
        </div>
      </div>
    </div>
  </div>
</template>
