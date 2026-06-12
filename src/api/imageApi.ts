import http from './http'
import type { ApiResponse, ImageGenerationConfig, ImageRecord, PageResult } from '@/types'

export const imageApi = {
  generate(prompt: string, qualityCode: string) {
    return http.post<ApiResponse<ImageRecord>>('/image/generate', { prompt, qualityCode })
  },
  configs() {
    return http.get<ApiResponse<ImageGenerationConfig[]>>('/image/configs')
  },
  history(page = 1, size = 10) {
    return http.get<ApiResponse<PageResult<ImageRecord>>>('/image/history', { params: { page, size } })
  }
}
