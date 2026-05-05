import { Link, NavLink, useNavigate } from 'react-router-dom'
import {
  Activity,
  BarChart3,
  CalendarCheck,
  Dumbbell,
  LayoutDashboard,
  LogOut,
  Menu,
  QrCode,
  ShieldCheck,
  UserRoundCog,
  X,
} from 'lucide-react'
import { useState } from 'react'
import { useAuth } from '../context/AuthContext'

const linksByRole = {
  member: [
    { label: 'Dashboard', to: '/member', icon: LayoutDashboard },
    { label: 'Workout Logger', to: '/workouts', icon: Dumbbell },
    { label: 'Progress', to: '/progress', icon: BarChart3 },
    { label: 'Booking', to: '/booking', icon: CalendarCheck },
  ],
  admin: [{ label: 'Admin', to: '/admin', icon: ShieldCheck }],
  trainer: [{ label: 'Trainer', to: '/trainer', icon: UserRoundCog }],
}

export default function AppLayout({ children, title, subtitle }) {
  const { logout, user } = useAuth()
  const [open, setOpen] = useState(false)
  const navigate = useNavigate()
  const navLinks = linksByRole[user?.role] || linksByRole.member

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <aside
        className={`fixed inset-y-0 left-0 z-40 w-72 border-r border-slate-200 bg-white transition lg:translate-x-0 ${
          open ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-full flex-col">
          <div className="flex items-center justify-between border-b border-slate-200 px-5 py-4">
            <Link className="flex items-center gap-3" to={`/${user?.role || 'member'}`}>
              <span className="grid size-11 place-items-center rounded-lg bg-emerald-600 text-white">
                <Activity className="size-6" />
              </span>
              <span>
                <span className="block text-base font-black">PulseFit</span>
                <span className="block text-xs font-medium text-slate-500">Gym operating system</span>
              </span>
            </Link>
            <button
              aria-label="Close navigation"
              className="rounded-md p-2 text-slate-500 hover:bg-slate-100 lg:hidden"
              onClick={() => setOpen(false)}
              type="button"
            >
              <X className="size-5" />
            </button>
          </div>

          <nav className="flex-1 space-y-1 px-3 py-4">
            {navLinks.map(({ icon: Icon, label, to }) => (
              <NavLink
                className={({ isActive }) =>
                  `flex items-center gap-3 rounded-lg px-3 py-3 text-sm font-semibold transition ${
                    isActive ? 'bg-emerald-50 text-emerald-700' : 'text-slate-600 hover:bg-slate-100 hover:text-slate-950'
                  }`
                }
                key={to}
                onClick={() => setOpen(false)}
                to={to}
              >
                <Icon className="size-5" />
                {label}
              </NavLink>
            ))}
          </nav>

          <div className="border-t border-slate-200 p-4">
            <div className="mb-4 rounded-lg bg-slate-100 p-3">
              <p className="text-sm font-bold text-slate-950">{user?.name}</p>
              <p className="text-xs capitalize text-slate-500">{user?.role}</p>
            </div>
            <button
              className="flex w-full items-center justify-center gap-2 rounded-lg border border-slate-200 bg-white px-3 py-2.5 text-sm font-bold text-slate-700 transition hover:bg-slate-100"
              onClick={handleLogout}
              type="button"
            >
              <LogOut className="size-4" />
              Logout
            </button>
          </div>
        </div>
      </aside>

      {open ? <div className="fixed inset-0 z-30 bg-slate-950/30 lg:hidden" onClick={() => setOpen(false)} /> : null}

      <main className="lg:pl-72">
        <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/90 px-4 py-4 backdrop-blur sm:px-6">
          <div className="flex items-center justify-between gap-4">
            <div className="min-w-0">
              <p className="text-sm font-semibold text-emerald-700">{subtitle}</p>
              <h1 className="truncate text-2xl font-black tracking-normal text-slate-950 sm:text-3xl">{title}</h1>
            </div>
            <div className="flex items-center gap-2">
              <button
                aria-label="Open QR check-in"
                className="hidden rounded-lg border border-slate-200 bg-white p-3 text-slate-600 transition hover:bg-slate-100 sm:inline-flex"
                type="button"
              >
                <QrCode className="size-5" />
              </button>
              <button
                aria-label="Open navigation"
                className="rounded-lg border border-slate-200 bg-white p-3 text-slate-600 transition hover:bg-slate-100 lg:hidden"
                onClick={() => setOpen(true)}
                type="button"
              >
                <Menu className="size-5" />
              </button>
            </div>
          </div>
        </header>
        <div className="px-4 py-6 sm:px-6 lg:px-8">{children}</div>
      </main>
    </div>
  )
}
