<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import ImagePreviewModal from '@/components/ImagePreviewModal.vue'
import { imageApi } from '@/api/imageApi'
import { useAuthStore } from '@/store/authStore'
import type { ImageGenerationConfig, ImageRecord } from '@/types'

const auth = useAuthStore()
const router = useRouter()
const prompt = ref('')
const loading = ref(false)
const error = ref('')
const result = ref<ImageRecord | null>(null)
const preview = ref('')
const recent = ref<ImageRecord[]>([])
const configs = ref<ImageGenerationConfig[]>([])
const selectedQuality = ref('1k')

const promptPresets = [
  '未来主义建筑，清晨薄雾，广角镜头，超写实',
  '高级香水广告，黑色大理石台面，电影级布光',
  '赛博朋克街区，雨夜霓虹，人物背影，细节丰富'
]

async function loadRecent() {
  const { data } = await imageApi.history(1, 4)
  recent.value = data.data.records
}

async function loadConfigs() {
  const { data } = await imageApi.configs()
  configs.value = data.data
  if (configs.value.length && !configs.value.some((item) => item.code === selectedQuality.value)) {
    selectedQuality.value = configs.value[0]?.code || '1k'
  }
}

async function generate() {
  error.value = ''
  if (!prompt.value.trim()) {
    error.value = '请输入提示词'
    return
  }
  loading.value = true
  try {
    const { data } = await imageApi.generate(prompt.value, selectedQuality.value)
    result.value = data.data
  } catch (err) {
    error.value = err instanceof Error ? err.message : '生成失败'
  } finally {
    await Promise.allSettled([auth.refreshUser(), loadRecent()])
    loading.value = false
  }
}

function downloadImage(src: string) {
  const link = document.createElement('a')
  link.href = src
  link.download = `imageCreater-${Date.now()}.png`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

function logout() {
  auth.logout()
  router.push('/login')
}

onMounted(() => {
  void Promise.all([auth.refreshUser(), loadRecent(), loadConfigs()])
})
</script>

<template>
  <div class="min-h-screen bg-[#f7f9fc] text-slate-950">
    <div class="fixed inset-0 -z-10 bg-[radial-gradient(circle_at_18%_10%,rgba(14,165,233,0.13),transparent_28%),radial-gradient(circle_at_88%_24%,rgba(45,212,191,0.12),transparent_30%)]" />
    <header class="sticky top-0 z-30 border-b border-white/80 bg-white/78 backdrop-blur-2xl">
      <div class="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 md:px-8">
        <RouterLink to="/" class="text-lg font-black">imageCreater</RouterLink>
        <nav class="hidden items-center gap-5 text-sm font-semibold text-slate-600 md:flex">
          <RouterLink class="hover:text-sky-600" to="/create">创作</RouterLink>
          <RouterLink class="hover:text-sky-600" to="/user/dashboard">控制台</RouterLink>
          <RouterLink class="hover:text-sky-600" to="/user/history">历史</RouterLink>
          <RouterLink class="hover:text-sky-600" to="/user/payment">余额</RouterLink>
        </nav>
        <div class="flex items-center gap-3">
          <span class="hidden rounded-full border border-sky-100 bg-sky-50 px-3 py-2 text-sm font-bold text-sky-700 md:inline-flex">
            ￥{{ Number(auth.userInfo?.balance || 0).toFixed(2) }}
          </span>
          <button class="btn-secondary rounded-full" @click="logout">退出</button>
        </div>
      </div>
    </header>

    <main class="page-enter relative mx-auto grid max-w-7xl gap-6 px-4 py-8 md:px-8 lg:grid-cols-[420px_1fr]">
      <section class="soft-card p-5">
        <p class="text-sm font-bold uppercase tracking-[0.22em] text-sky-600">提示词工作台</p>
        <h1 class="mt-3 text-3xl font-black tracking-tight">前台创作空间</h1>
        <p class="mt-3 text-sm leading-7 text-slate-600">输入画面、镜头、材质和氛围，系统会调用后端生成图像并保存到你的历史记录。</p>

        <textarea
          v-model="prompt"
          class="mt-6 min-h-56 w-full resize-y rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm leading-7 text-slate-800 outline-none transition placeholder:text-slate-400 focus:border-sky-300 focus:ring-4 focus:ring-sky-100"
          placeholder="例如：高端跑车广告，黑色摄影棚，湿润地面反射，蓝色边缘光，85mm镜头，超写实"
        />

        <div class="mt-4 space-y-2">
          <button
            v-for="preset in promptPresets"
            :key="preset"
            class="block w-full rounded-2xl border border-slate-200 bg-slate-50/70 px-3 py-2 text-left text-xs leading-5 text-slate-600 transition hover:-translate-y-0.5 hover:border-sky-200 hover:bg-sky-50"
            @click="prompt = preset"
          >
            {{ preset }}
          </button>
        </div>

        <div class="mt-5 rounded-3xl border border-slate-200 bg-white/80 p-4">
          <div class="flex items-center justify-between gap-3">
            <div>
              <p class="text-sm font-black text-slate-800">图像质量</p>
              <p class="mt-1 text-xs font-semibold text-slate-500">选择生成规格，系统按后台定价扣费。</p>
            </div>
          </div>
          <div class="mt-4 grid gap-3 sm:grid-cols-3">
            <button
              v-for="config in configs"
              :key="config.code"
              class="rounded-2xl border px-3 py-3 text-left transition duration-300 hover:-translate-y-0.5"
              :class="selectedQuality === config.code ? 'border-sky-300 bg-sky-50 shadow-[0_16px_45px_rgba(14,165,233,0.16)]' : 'border-slate-200 bg-slate-50/70 hover:border-sky-200 hover:bg-white'"
              @click="selectedQuality = config.code"
            >
              <span class="block text-sm font-black text-slate-950">{{ config.name }}</span>
              <span class="mt-1 block text-xs font-semibold text-slate-500">{{ config.size }} · {{ config.quality }}</span>
              <span class="mt-2 block text-sm font-black text-sky-600">￥{{ Number(config.price).toFixed(2) }}</span>
            </button>
          </div>
          <p v-if="configs.length === 0" class="mt-3 text-sm font-semibold text-red-500">暂无可用生图规格，请联系管理员配置。</p>
        </div>

        <div class="mt-5 flex flex-wrap items-center gap-3">
          <button class="rounded-full bg-sky-500 px-5 py-3 text-sm font-black text-white shadow-[0_18px_50px_rgba(14,165,233,0.26)] transition hover:-translate-y-0.5 hover:bg-sky-600 disabled:cursor-not-allowed disabled:opacity-60" :disabled="loading || configs.length === 0" @click="generate">
            {{ loading ? '生成中' : '开始生成' }}
          </button>
          <RouterLink v-if="error.includes('余额')" class="text-sm font-semibold text-sky-600" to="/user/payment">前往充值</RouterLink>
        </div>
        <p v-if="error" class="mt-3 rounded-xl bg-red-50 px-3 py-2 text-sm font-semibold text-red-600">{{ error }}</p>
      </section>

      <section class="soft-card min-h-[680px] p-4">
        <div class="flex items-center justify-between border-b border-slate-100 px-2 pb-4">
          <div>
            <p class="text-sm font-black text-slate-800">生成画布</p>
            <p class="mt-1 text-xs text-slate-500">结果可点击放大预览</p>
          </div>
          <RouterLink class="btn-secondary rounded-full" to="/user/history">查看历史</RouterLink>
        </div>

        <div class="grid min-h-[520px] place-items-center">
          <div v-if="loading" class="flex flex-col items-center gap-6">
            <div class="loader"></div>
            <p class="text-sm font-black tracking-[0.2em] text-slate-500">正在生成图像</p>
          </div>
          <div v-else-if="result?.generatedImageUrl" class="w-full max-w-4xl rounded-[28px] border border-slate-200 bg-white p-3 shadow-[0_24px_90px_rgba(14,165,233,0.16)]">
            <button class="group block w-full overflow-hidden rounded-3xl bg-slate-50" @click="preview = result?.generatedImageUrl || ''">
              <img :src="result.generatedImageUrl" alt="生成结果" class="mx-auto max-h-[520px] w-full rounded-3xl object-contain transition duration-500 group-hover:scale-[1.01]" />
            </button>
            <div class="mt-4 flex flex-wrap items-center justify-between gap-3 px-1">
              <div>
                <p class="text-sm font-black text-slate-950">生成完成</p>
                <p class="mt-1 text-xs font-semibold text-slate-500">点击图像可放大预览，或直接下载到本地。</p>
              </div>
              <div class="flex gap-2">
                <button class="rounded-full border border-slate-200 bg-white px-4 py-2 text-xs font-black text-slate-700 transition hover:border-sky-200 hover:bg-sky-50" @click="preview = result?.generatedImageUrl || ''">预览</button>
                <button class="rounded-full bg-slate-950 px-4 py-2 text-xs font-black text-white transition hover:bg-sky-600" @click="downloadImage(result.generatedImageUrl)">下载</button>
              </div>
            </div>
          </div>
          <div v-else class="max-w-md text-center">
            <div class="float-soft mx-auto h-56 w-56 rounded-full border border-sky-100 bg-[radial-gradient(circle,rgba(14,165,233,0.22),transparent_60%)] shadow-[0_0_80px_rgba(14,165,233,0.18)]" />
            <p class="mt-6 text-lg font-black">等待第一张生成图像</p>
            <p class="mt-2 text-sm leading-6 text-slate-500">填写提示词并开始生成后，结果会出现在这里。</p>
          </div>
        </div>
      </section>

      <section class="lg:col-span-2">
        <div class="mb-4 flex items-center justify-between">
          <h2 class="text-xl font-black">最近创作</h2>
          <RouterLink class="text-sm font-semibold text-sky-600" to="/user/history">全部记录</RouterLink>
        </div>
        <div class="grid gap-4 md:grid-cols-4">
          <div v-for="item in recent" :key="item.id" class="soft-card group overflow-hidden">
            <img v-if="item.generatedImageUrl" :src="item.generatedImageUrl" class="h-40 w-full object-cover transition duration-500 group-hover:scale-105" alt="历史图像" />
            <div v-else class="grid h-40 place-items-center bg-slate-100 text-sm text-slate-500">暂无图像</div>
            <div class="p-3">
              <p class="line-clamp-2 text-sm leading-6 text-slate-700">{{ item.prompt }}</p>
              <p class="mt-2 text-xs text-slate-500">{{ item.createdAt }}</p>
            </div>
          </div>
          <div v-if="recent.length === 0" class="rounded-2xl border border-dashed border-slate-300 bg-white/70 p-6 text-sm text-slate-500 md:col-span-4">暂无历史记录</div>
        </div>
      </section>
    </main>

    <ImagePreviewModal :src="preview" @close="preview = ''" />
  </div>
</template>
