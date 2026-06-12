<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AppLayout from '@/components/AppLayout.vue'
import ImagePreviewModal from '@/components/ImagePreviewModal.vue'
import Pagination from '@/components/Pagination.vue'
import { imageApi } from '@/api/imageApi'
import type { ImageRecord } from '@/types'

const records = ref<ImageRecord[]>([])
const current = ref(1)
const pages = ref(1)
const preview = ref('')

async function load(page = 1) {
  const { data } = await imageApi.history(page, 10)
  records.value = data.data.records
  current.value = data.data.current
  pages.value = data.data.pages
}
onMounted(() => load())
</script>

<template>
  <AppLayout>
    <div class="flex flex-wrap items-end justify-between gap-4">
      <div>
        <p class="text-sm font-bold uppercase tracking-[0.22em] text-sky-600">生成历史</p>
        <h1 class="mt-2 text-4xl font-black tracking-tight">图像资产库</h1>
        <p class="mt-2 text-sm text-slate-500">查看提示词、状态、生成时间和本地保存图像。</p>
      </div>
      <RouterLink class="rounded-full bg-sky-500 px-5 py-3 text-sm font-black text-white shadow-[0_18px_50px_rgba(14,165,233,0.24)] transition hover:-translate-y-0.5 hover:bg-sky-600" to="/create">继续创作</RouterLink>
    </div>

    <section class="soft-card mt-6 overflow-hidden">
      <div class="hidden grid-cols-[1fr_120px_180px_120px] gap-4 border-b border-slate-100 bg-slate-50/80 px-5 py-4 text-xs font-bold uppercase tracking-[0.14em] text-slate-500 md:grid">
        <span>提示词</span><span>状态</span><span>创建时间</span><span>预览</span>
      </div>
      <div class="divide-y divide-slate-100">
        <div v-for="record in records" :key="record.id" class="interactive-row grid gap-4 px-5 py-4 text-sm md:grid-cols-[1fr_120px_180px_120px] md:items-center">
          <p class="leading-6 text-slate-700">{{ record.prompt }}</p>
          <span class="w-fit rounded-full border border-sky-100 bg-sky-50 px-3 py-1 text-xs font-bold text-sky-700">{{ record.status }}</span>
          <span class="text-slate-500">{{ record.createdAt }}</span>
          <button v-if="record.generatedImageUrl" class="group overflow-hidden rounded-xl border border-slate-200 bg-white p-1 shadow-sm" @click="preview = record.generatedImageUrl || ''">
            <img :src="record.generatedImageUrl" class="h-16 w-24 rounded-lg object-cover transition duration-300 group-hover:scale-105" alt="生成缩略图" />
          </button>
        </div>
        <div v-if="records.length === 0" class="p-8 text-sm text-slate-500">暂无生成记录</div>
      </div>
      <div class="border-t border-slate-100 bg-white/70 p-4"><Pagination :current="current" :pages="pages" @change="load" /></div>
    </section>
    <ImagePreviewModal :src="preview" @close="preview = ''" />
  </AppLayout>
</template>
