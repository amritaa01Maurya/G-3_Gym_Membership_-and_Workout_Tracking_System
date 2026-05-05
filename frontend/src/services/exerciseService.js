import apiClient from './apiClient'

export const getExercises = (page = 0, size = 10) =>
  apiClient.get('/api/exercises', { params: { page, size } })

export const getExerciseById = (id) => apiClient.get(`/api/exercises/${id}`)

export const createExercise = (payload) => apiClient.post('/api/exercises', payload)

export const updateExercise = (id, payload) => apiClient.put(`/api/exercises/${id}`, payload)

export const deleteExercise = (id) => apiClient.delete(`/api/exercises/${id}`)

export const searchExercises = (query) =>
  apiClient.get('/api/exercises/search', { params: { query } })
