import apiClient from './apiClient'

export const getAdminStats = () => apiClient.get('/api/v1/admin/stats')
export const getUsers = () => apiClient.get('/api/v1/admin/users')
export const updateUser = (id, payload) => apiClient.patch(`/api/v1/admin/users/${id}`, payload)
