import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { Activity, ArrowRight, ShieldCheck, UserRoundCog } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'

const roleOptions = [
  { label: 'Member', value: 'member', icon: Activity },
  { label: 'Admin', value: 'admin', icon: ShieldCheck },
  { label: 'Trainer', value: 'trainer', icon: UserRoundCog },
]

export default function LoginPage() {
  const { isAuthenticated, loading, login, register, user } = useAuth()
  const { notify } = useToast()
  const navigate = useNavigate()
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState({
    email: 'member@gym.com',
    name: 'Nisha Patel',
    password: 'password',
    role: 'member',
  })

  if (isAuthenticated) {
    return <Navigate replace to={`/${user?.role || 'member'}`} />
  }

  const updateField = (event) => {
    setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()

    if (mode === 'register') {
      await register(form)
      notify({ title: 'Registration submitted', message: 'The backend endpoint will create the real account.' })
      setMode('login')
      return
    }

    const nextUser = await login(form)
    notify({ title: `Welcome, ${nextUser.name}`, message: 'Demo fallback is enabled until the backend responds.' })
    navigate(`/${nextUser.role}`)
  }

  return (
    <main className="min-h-screen bg-slate-950 text-white">
      <div className="grid min-h-screen lg:grid-cols-[1fr_520px]">
        <section className="relative flex items-end overflow-hidden p-6 sm:p-10">
          <img
            alt="Gym training floor"
            className="absolute inset-0 h-full w-full object-cover opacity-45"
            src="https://images.unsplash.com/photo-1534438327276-14e5300c3a48?auto=format&fit=crop&w=1600&q=80"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-slate-950 via-slate-950/70 to-slate-950/20" />
          <div className="relative max-w-3xl pb-8">
            <span className="mb-5 inline-flex items-center gap-2 rounded-full bg-white/10 px-4 py-2 text-sm font-bold backdrop-blur">
              <Activity className="size-4 text-emerald-300" />
              Gym Membership & Workout Tracking
            </span>
            <h1 className="max-w-2xl text-5xl font-black tracking-normal sm:text-6xl">
              Run memberships, workouts, trainers, and progress from one clean cockpit.
            </h1>
            <p className="mt-5 max-w-xl text-lg text-slate-200">
              JWT-ready authentication, role dashboards, bookings, check-ins, plans, and visual progress tracking.
            </p>
          </div>
        </section>

        <section className="flex items-center bg-white p-6 text-slate-950 sm:p-10">
          <div className="w-full">
            <div className="mb-8">
              <p className="text-sm font-bold uppercase text-emerald-700">PulseFit</p>
              <h2 className="mt-2 text-3xl font-black tracking-normal">{mode === 'login' ? 'Sign in' : 'Create account'}</h2>
            </div>

            <div className="mb-6 grid grid-cols-3 gap-2 rounded-lg bg-slate-100 p-1">
              {roleOptions.map(({ icon: Icon, label, value }) => (
                <button
                  className={`flex items-center justify-center gap-2 rounded-md px-3 py-2 text-sm font-bold transition ${
                    form.role === value ? 'bg-white text-emerald-700 shadow-sm' : 'text-slate-500 hover:text-slate-950'
                  }`}
                  key={value}
                  onClick={() => setForm((current) => ({ ...current, role: value, email: `${value}@gym.com` }))}
                  type="button"
                >
                  <Icon className="size-4" />
                  {label}
                </button>
              ))}
            </div>

            <form className="space-y-4" onSubmit={handleSubmit}>
              {mode === 'register' ? (
                <label className="block text-sm font-bold text-slate-700">
                  Name
                  <input
                    className="mt-2 w-full rounded-lg border border-slate-200 px-4 py-3 text-slate-950 outline-none transition focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100"
                    name="name"
                    onChange={updateField}
                    value={form.name}
                  />
                </label>
              ) : null}
              <label className="block text-sm font-bold text-slate-700">
                Email
                <input
                  className="mt-2 w-full rounded-lg border border-slate-200 px-4 py-3 text-slate-950 outline-none transition focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100"
                  name="email"
                  onChange={updateField}
                  type="email"
                  value={form.email}
                />
              </label>
              <label className="block text-sm font-bold text-slate-700">
                Password
                <input
                  className="mt-2 w-full rounded-lg border border-slate-200 px-4 py-3 text-slate-950 outline-none transition focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100"
                  name="password"
                  onChange={updateField}
                  type="password"
                  value={form.password}
                />
              </label>
              <button
                className="flex w-full items-center justify-center gap-2 rounded-lg bg-emerald-600 px-4 py-3 font-black text-white transition hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-60"
                disabled={loading}
                type="submit"
              >
                {loading ? 'Working...' : mode === 'login' ? 'Login' : 'Register'}
                <ArrowRight className="size-5" />
              </button>
            </form>

            <button
              className="mt-6 text-sm font-bold text-emerald-700 hover:text-emerald-800"
              onClick={() => setMode((current) => (current === 'login' ? 'register' : 'login'))}
              type="button"
            >
              {mode === 'login' ? 'Need a member account? Register' : 'Already registered? Login'}
            </button>
          </div>
        </section>
      </div>
    </main>
  )
}
