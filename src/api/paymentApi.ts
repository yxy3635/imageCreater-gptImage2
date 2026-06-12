import http from './http'
import type { ApiResponse, PageResult, PaymentRecord } from '@/types'

export const paymentApi = {
  recharge(amount: number, type: string) {
    return http.post<ApiResponse<Record<string, unknown>>>('/payment/recharge', { amount, type })
  },
  history(page = 1, size = 10) {
    return http.get<ApiResponse<PageResult<PaymentRecord>>>('/payment/history', { params: { page, size } })
  }
}
