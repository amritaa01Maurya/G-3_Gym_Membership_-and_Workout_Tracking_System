import apiClient from './apiClient'

export const checkIn = (payload) => apiClient.post('/api/v1/attendance/check-in', payload)
export const getAttendance = () => apiClient.get('/api/v1/attendance')
