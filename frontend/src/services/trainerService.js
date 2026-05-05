import apiClient from './apiClient'

export const getClients = () => apiClient.get('/api/v1/trainer/clients')
export const updateAvailability = (payload) => apiClient.post('/api/v1/trainer/availability', payload)
export const assignDietPlan = (payload) => apiClient.post('/api/v1/trainer/diet-plan', payload)
