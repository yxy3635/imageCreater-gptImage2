import axios from 'axios'
import type { ApiResponse } from '@/types'
import { sessionText } from '@/utils/storage'

const http = axios.create({
  baseURL: '/api',
  timeout: 600000
})

http.interceptors.request.use((config) => {
  const sessionValue = sessionText()
  if (sessionValue) {
    config.headers.Authorization = `Bearer ${sessionValue}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse<unknown>
    if (body && body.code && body.code !== 200) {
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return response
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(message))
  }
)

export default http
