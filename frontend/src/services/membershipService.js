import apiClient from './apiClient'

export const getPlans = () => apiClient.get('/api/v1/membership/plans')
export const buyPlan = (planId) => apiClient.post('/api/v1/membership/buy', { planId })
export const getRenewals = () => apiClient.get('/api/v1/membership/renewals')
