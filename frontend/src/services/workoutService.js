import apiClient from './apiClient'

export const getMyWorkouts = (startDate, endDate) => {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return apiClient.get('/api/workouts/my', { params })
}

export const getWorkoutSessions = (startDate, endDate) => {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return apiClient.get('/api/workouts/sessions', { params })
}

export const addWorkoutSession = (payload) => apiClient.post('/api/workouts/sessions', payload)

export const logExercise = (payload) => apiClient.post('/api/workouts/exercise-logs', payload)

export const getMyExerciseLogs = (startDate, endDate) => {
  const params = {}
  if (startDate) params.startDate = startDate
  if (endDate) params.endDate = endDate
  return apiClient.get('/api/workouts/exercise-logs/my', { params })
}

export const getWorkoutPlans = () => apiClient.get('/api/workout-plans')

export const assignWorkoutPlan = (userId, workoutPlanId) =>
  apiClient.post('/api/workout-plans/assign', { userId, workoutPlanId })

export const getClientProgress = (clientId) => 
  apiClient.get(`/api/analytics/clients/${clientId}/progress`)

