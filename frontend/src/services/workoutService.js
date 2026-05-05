import apiClient from './apiClient'

export const getWorkoutHistory = () => apiClient.get('/api/v1/workout/history')
export const addWorkout = (payload) => apiClient.post('/api/v1/workout', payload)
export const assignWorkoutPlan = (payload) => apiClient.post('/api/v1/workout/assign', payload)
export const getClientProgress = (clientId) => apiClient.get(`/api/v1/workout/client/${clientId}/progress`)
