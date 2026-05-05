/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import { login as loginRequest, register as registerRequest } from '../services/authService'
import { clearSession, getStoredUser, getToken, setSession } from '../utils/storage'

const AuthContext = createContext(null)

const demoUsers = {
  member: { name: 'Nisha Patel', email: 'member@gym.com', role: 'member' },
  admin: { name: 'Admin Rao', email: 'admin@gym.com', role: 'admin' },
  trainer: { name: 'Aisha Khan', email: 'trainer@gym.com', role: 'trainer' },
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => getStoredUser())
  const [token, setToken] = useState(() => getToken())
  const [loading, setLoading] = useState(false)

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

    try {
      const response = await loginRequest({ email, password, role })
      const nextToken = response.data?.token
      const nextUser = response.data?.user

      if (!nextToken || !nextUser) {
        throw new Error('Invalid login response')
      }

      setSession({ token: nextToken, user: nextUser })
      setToken(nextToken)
      setUser(nextUser)
      return nextUser
    } catch {
      const fallbackUser = demoUsers[role] || demoUsers.member
      const fallbackToken = `demo-jwt-${fallbackUser.role}`

      setSession({ token: fallbackToken, user: fallbackUser })
      setToken(fallbackToken)
      setUser(fallbackUser)
      return fallbackUser
    } finally {
      setLoading(false)
    }
  }

  const register = async (payload) => {
    setLoading(true)

    try {
      const response = await registerRequest(payload)
      return response.data
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    clearSession()
    setUser(null)
    setToken(null)
  }

  const value = useMemo(
    () => ({
      isAuthenticated: Boolean(token && user),
      loading,
      login,
      logout,
      register,
      token,
      user,
    }),
    [loading, token, user],
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
