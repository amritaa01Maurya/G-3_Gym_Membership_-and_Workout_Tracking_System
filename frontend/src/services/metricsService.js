import apiClient from './apiClient'

export const getMyMetrics = (startDate = null, endDate = null) => {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return apiClient.get('/api/metrics/my', { params })
}

export const addBodyMetrics = (payload) => apiClient.post('/api/metrics/body', payload)

export const getBodyMetrics = (startDate = null, endDate = null) => {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return apiClient.get('/api/metrics/body', { params })
}

export const getProgressAnalytics = (startDate = null, endDate = null) => {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return apiClient.get('/api/analytics/progress', { params })
}

export const getClientMetrics = (clientId, startDate = null, endDate = null) => {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return apiClient.get(`/api/metrics/client/${clientId}`, { params })
}
