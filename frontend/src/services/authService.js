import apiClient from './apiClient'

export const login = (payload) => apiClient.post('/api/v1/auth/login', payload)
export const register = (payload) => apiClient.post('/api/v1/auth/register', payload)
export const getProfile = () => apiClient.get('/api/v1/auth/me')
