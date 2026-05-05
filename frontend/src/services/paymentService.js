import apiClient from './apiClient'

export const processPayment = (subscriptionId, amount, description = '') =>
  apiClient.post('/api/payments', { subscriptionId, amount, description })

export const getMyPayments = () => apiClient.get('/api/payments/my/all')

export const getSuccessfulPayments = () => apiClient.get('/api/payments/my/successful')

export const getPaymentById = (id) => apiClient.get(`/api/payments/${id}`)

export const refundPayment = (id) => apiClient.post(`/api/payments/${id}/refund`)

export const getAllPayments = () => apiClient.get('/api/payments')
