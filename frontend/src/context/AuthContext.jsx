/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { login as loginRequest, register as registerRequest } from '../services/authService'
import { clearSession, getStoredUser, getToken, setSession } from '../utils/storage'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => getStoredUser())
  const [token, setToken] = useState(() => getToken())
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    const handleLogout = () => {
      setUser(null)
      setToken(null)
    }

    window.addEventListener('auth:logout', handleLogout)
    return () => window.removeEventListener('auth:logout', handleLogout)
  }, [])

  const login = async ({ email, password, role }) => {
    setLoading(true)
    setError(null)

    try {
      const response = await loginRequest({ email, password, role })
      
      // Backend returns LoginResponse directly (not wrapped in ApiResponse)
      // Format: { token, type, userId, email, name, role }
      const nextToken = response.data?.token
      const nextUser = {
        id: response.data?.userId,
        email: response.data?.email,
        name: response.data?.name,
        role: response.data?.role?.toLowerCase() || 'member'
      }

      if (!nextToken || !nextUser.email) {
        throw new Error('Invalid login response')
      }

      setSession({ token: nextToken, user: nextUser })
      setToken(nextToken)
      setUser(nextUser)
      return nextUser
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message || 'Login failed'
      setError(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const register = async (payload) => {
    setLoading(true)
    setError(null)

    try {
      // Backend returns UserDTO directly
      const response = await registerRequest(payload)
      return response.data
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message || 'Registration failed'
      setError(errorMessage)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    clearSession()
    setUser(null)
    setToken(null)
    setError(null)
  }

  const value = useMemo(
    () => ({
      isAuthenticated: Boolean(token && user),
      loading,
      error,
      login,
      logout,
      register,
      token,
      user,
    }),
    [loading, error, token, user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider')
  }

  return context
}
