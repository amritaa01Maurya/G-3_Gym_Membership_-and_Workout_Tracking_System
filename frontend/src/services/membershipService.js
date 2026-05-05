import apiClient from './apiClient'

export const getPlans = () => apiClient.get('/api/plans')
export const getPlanById = (planId) => apiClient.get(`/api/plans/${planId}`)
export const purchasePlan = (planId) => apiClient.post('/api/subscriptions/purchase', { planId })
export const getMySubscription = () => apiClient.get('/api/subscriptions/my')
export const getMySubscriptions = () => apiClient.get('/api/subscriptions/my/all')
export const renewSubscription = (subscriptionId, planId) => 
  apiClient.post('/api/subscriptions/renew', { subscriptionId, planId })
export const freezeMembership = (subscriptionId, freezeStartDate, freezeEndDate) =>
  apiClient.post('/api/subscriptions/freeze', { subscriptionId, freezeStartDate, freezeEndDate })
export const unfreezeMembership = (subscriptionId) =>
  apiClient.post(`/api/subscriptions/${subscriptionId}/unfreeze`)

