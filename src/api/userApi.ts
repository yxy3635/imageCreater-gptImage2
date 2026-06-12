import http from './http'
import type { ApiResponse, UserInfo } from '@/types'

export const userApi = {
  balance() {
    return http.get<ApiResponse<number>>('/user/balance')
  },
  updateProfile(email: string) {
    return http.put<ApiResponse<UserInfo>>('/user/profile', { email })
  },
  changePassword(oldPassword: string, newPassword: string) {
    return http.post<ApiResponse<void>>('/user/password', { oldPassword, newPassword })
  }
}
