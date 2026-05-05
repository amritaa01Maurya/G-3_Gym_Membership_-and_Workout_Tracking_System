import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from './ProtectedRoute'
import AdminDashboard from '../pages/AdminDashboard'
import BookingPage from '../pages/BookingPage'
import LoginPage from '../pages/LoginPage'
import MemberDashboard from '../pages/MemberDashboard'
import ProgressPage from '../pages/ProgressPage'
import TrainerDashboard from '../pages/TrainerDashboard'
import WorkoutLoggerPage from '../pages/WorkoutLoggerPage'

export default function AppRoutes() {
  return (
    <Routes>
      <Route element={<LoginPage />} path="/login" />
      <Route element={<ProtectedRoute roles={['member']} />}>
        <Route element={<MemberDashboard />} path="/member" />
        <Route element={<WorkoutLoggerPage />} path="/workouts" />
        <Route element={<ProgressPage />} path="/progress" />
        <Route element={<BookingPage />} path="/booking" />
      </Route>
      <Route element={<ProtectedRoute roles={['admin']} />}>
        <Route element={<AdminDashboard />} path="/admin" />
      </Route>
      <Route element={<ProtectedRoute roles={['trainer']} />}>
        <Route element={<TrainerDashboard />} path="/trainer" />
      </Route>
      <Route element={<Navigate replace to="/member" />} path="*" />
    </Routes>
  )
}
