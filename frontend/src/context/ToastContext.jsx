/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useMemo, useState } from 'react'
import { CheckCircle2, Info, X } from 'lucide-react'

const ToastContext = createContext(null)

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])

  const notify = ({ title, message, type = 'success' }) => {
    const id = crypto.randomUUID()
    setToasts((current) => [...current, { id, title, message, type }])
    setTimeout(() => {
      setToasts((current) => current.filter((toast) => toast.id !== id))
    }, 3800)
  }

  const dismiss = (id) => {
    setToasts((current) => current.filter((toast) => toast.id !== id))
  }

  const value = useMemo(() => ({ notify }), [])

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="fixed right-4 top-4 z-50 grid w-[min(360px,calc(100vw-2rem))] gap-3">
        {toasts.map((toast) => {
          const Icon = toast.type === 'info' ? Info : CheckCircle2

          return (
            <div
              className="rounded-lg border border-slate-200 bg-white p-4 text-left shadow-xl shadow-slate-900/10"
              key={toast.id}
            >
              <div className="flex items-start gap-3">
                <Icon className="mt-0.5 size-5 text-emerald-600" />
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-semibold text-slate-950">{toast.title}</p>
                  {toast.message ? <p className="mt-1 text-sm text-slate-600">{toast.message}</p> : null}
                </div>
                <button
                  aria-label="Dismiss notification"
                  className="rounded-md p-1 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700"
                  onClick={() => dismiss(toast.id)}
                  type="button"
                >
                  <X className="size-4" />
                </button>
              </div>
            </div>
          )
        })}
      </div>
    </ToastContext.Provider>
  )
}

export const useToast = () => {
  const context = useContext(ToastContext)

  if (!context) {
    throw new Error('useToast must be used inside ToastProvider')
  }

  return context
}
