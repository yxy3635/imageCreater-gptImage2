<script setup lang="ts">
defineProps<{ current: number; pages: number }>()
const emit = defineEmits<{ change: [page: number] }>()
</script>

<template>
  <div class="flex items-center justify-end gap-2">
    <button class="btn-secondary" :disabled="current <= 1" @click="emit('change', current - 1)">上一页</button>
    <span class="text-sm text-slate-600">{{ current }} / {{ Math.max(pages, 1) }}</span>
    <button class="btn-secondary" :disabled="current >= pages" @click="emit('change', current + 1)">下一页</button>
  </div>
</template>
