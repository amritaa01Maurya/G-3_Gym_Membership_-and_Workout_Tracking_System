import apiClient from './apiClient'

export const login = (payload) => apiClient.post('/api/auth/login', payload)
export const register = (payload) => apiClient.post('/api/auth/signup', payload)
export const getProfile = () => apiClient.get('/api/users/me')
