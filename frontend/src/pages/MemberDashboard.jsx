import { BadgeCheck, CalendarDays, Dumbbell, Flame, QrCode, Trophy, WalletCards } from 'lucide-react'
import AppLayout from '../components/AppLayout'
import SectionHeader from '../components/SectionHeader'
import StatCard from '../components/StatCard'
import { useToast } from '../context/ToastContext'
import { buyPlan } from '../services/membershipService'
import { plans } from '../utils/mockData'

export default function MemberDashboard() {
  const { notify } = useToast()

  const handleBuyPlan = async (plan) => {
    try {
      await buyPlan(plan.id)
    } finally {
      notify({ title: `${plan.name} plan selected`, message: 'Membership checkout is ready for backend payment wiring.' })
    }
  }

  return (
    <AppLayout subtitle="Member workspace" title="Dashboard">
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard icon={Flame} label="Current streak" trend="+3 from last week" value="12 days" />
        <StatCard icon={Dumbbell} label="Workouts logged" trend="5 this week" value="48" />
        <StatCard icon={CalendarDays} label="Upcoming bookings" trend="Yoga at 7:30 AM" value="3" />
        <StatCard icon={Trophy} label="Badges earned" trend="New strength badge" value="8" />
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-[1.1fr_0.9fr]">
        <section>
          <SectionHeader eyebrow="Membership" title="Choose a plan" />
          <div className="grid gap-4 md:grid-cols-3">
            {plans.map((plan) => (
              <article className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm" key={plan.id}>
                <div className="flex items-center justify-between">
                  <h3 className="text-lg font-black text-slate-950">{plan.name}</h3>
                  <WalletCards className="size-5 text-emerald-600" />
                </div>
                <p className="mt-3 text-3xl font-black">₹{plan.price}</p>
                <ul className="mt-4 space-y-2">
                  {plan.perks.map((perk) => (
                    <li className="flex items-center gap-2 text-sm font-semibold text-slate-600" key={perk}>
                      <BadgeCheck className="size-4 text-emerald-600" />
                      {perk}
                    </li>
                  ))}
                </ul>
                <button
                  className="mt-5 w-full rounded-lg bg-slate-950 px-4 py-2.5 text-sm font-black text-white transition hover:bg-emerald-700"
                  onClick={() => handleBuyPlan(plan)}
                  type="button"
                >
                  Buy Plan
                </button>
              </article>
            ))}
          </div>
        </section>

        <section>
          <SectionHeader eyebrow="Attendance" title="QR check-in" />
          <div className="rounded-lg border border-slate-200 bg-white p-6 text-center shadow-sm">
            <div className="mx-auto grid size-48 place-items-center rounded-lg border-4 border-slate-950 bg-[repeating-linear-gradient(45deg,#0f172a_0_8px,#fff_8px_16px)]">
              <div className="grid size-20 place-items-center rounded-lg bg-white">
                <QrCode className="size-12 text-slate-950" />
              </div>
            </div>
            <p className="mt-5 text-sm font-bold text-slate-600">Scan at reception to mark today’s attendance.</p>
            <button
              className="mt-5 rounded-lg bg-emerald-600 px-5 py-2.5 text-sm font-black text-white transition hover:bg-emerald-700"
              onClick={() => notify({ title: 'Checked in', message: 'Attendance API will persist this scan.' })}
              type="button"
            >
              Simulate Check-in
            </button>
          </div>
        </section>
      </div>
    </AppLayout>
  )
}
