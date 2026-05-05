import apiClient from './apiClient'

export const getDashboardStats = () => apiClient.get('/api/admin/dashboard/statistics')

export const getRevenueData = (period = 'TODAY', startDate = null, endDate = null) => {
  const params = { period }
  if (startDate && endDate) {
    params.startDate = startDate
    params.endDate = endDate
  }
  return apiClient.get('/api/admin/dashboard/revenue', { params })
}

export const getSubscriptionMetrics = () => apiClient.get('/api/admin/dashboard/subscriptions')

export const getAttendanceMetrics = (date = null) => {
  const params = date ? { date } : {}
  return apiClient.get('/api/admin/dashboard/attendance', { params })
}
