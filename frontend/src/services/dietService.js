import apiClient from './apiClient'

export const getDietPlans = () => apiClient.get('/api/diet-plans')

export const getDietPlanById = (id) => apiClient.get(`/api/diet-plans/${id}`)

export const createDietPlan = (payload) => apiClient.post('/api/diet-plans', payload)

export const updateDietPlan = (id, payload) => apiClient.put(`/api/diet-plans/${id}`, payload)

export const deleteDietPlan = (id) => apiClient.delete(`/api/diet-plans/${id}`)

export const assignDietPlan = (userId, dietPlanId) =>
  apiClient.post('/api/diet-plans/assign', { userId, dietPlanId })

export const addDietItem = (payload) => apiClient.post('/api/diet-items', payload)

export const getDietItems = (dietPlanId) => apiClient.get(`/api/diet-plans/${dietPlanId}/items`)
