import apiClient from './apiClient'

export const getClasses = () => apiClient.get('/api/v1/booking/classes')
export const bookClass = (classId) => apiClient.post('/api/v1/booking/classes', { classId })
export const getTrainerSlots = () => apiClient.get('/api/v1/booking/trainer-slots')
export const bookTrainerSlot = (slotId) => apiClient.post('/api/v1/booking/trainer-slots', { slotId })
