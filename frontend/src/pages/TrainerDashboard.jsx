import { CalendarClock, ClipboardList, Send, Utensils } from 'lucide-react'
import { useState } from 'react'
import AppLayout from '../components/AppLayout'
import SectionHeader from '../components/SectionHeader'
import StatCard from '../components/StatCard'
import { useToast } from '../context/ToastContext'
import { assignDietPlan, updateAvailability } from '../services/trainerService'
import { assignWorkoutPlan } from '../services/workoutService'
import { clients } from '../utils/mockData'

export default function TrainerDashboard() {
  const { notify } = useToast()
  const [plan, setPlan] = useState({
    clientId: clients[0].id,
    diet: 'High protein, 2200 kcal, hydration target 3L',
    workout: 'Push/Pull/Legs with progressive overload',
  })

  const submitPlans = async (event) => {
    event.preventDefault()

    try {
      await Promise.all([
        assignWorkoutPlan({ clientId: plan.clientId, plan: plan.workout }),
        assignDietPlan({ clientId: plan.clientId, plan: plan.diet }),
      ])
    } finally {
      notify({ title: 'Plans assigned', message: 'Workout and diet payloads are wired to trainer APIs.' })
    }
  }

  const saveAvailability = async () => {
    try {
      await updateAvailability({ days: ['Mon', 'Wed', 'Fri'], start: '07:00', end: '12:00' })
    } finally {
      notify({ title: 'Availability updated', message: 'Slots are ready for booking.' })
    }
  }

  return (
    <AppLayout subtitle="Trainer workspace" title="Client Coaching">
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard icon={ClipboardList} label="Active clients" trend="4 need review" value="32" />
        <StatCard icon={CalendarClock} label="Open slots" trend="18 booked this week" value="24" />
        <StatCard icon={Utensils} label="Diet plans" trend="6 updated today" value="19" />
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_420px]">
        <section>
          <SectionHeader eyebrow="Client progress" title="Review clients" />
          <div className="grid gap-3">
            {clients.map((client) => (
              <article className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm" key={client.id}>
                <div className="flex flex-wrap items-center justify-between gap-4">
                  <div>
                    <h3 className="text-lg font-black text-slate-950">{client.name}</h3>
                    <p className="mt-1 text-sm font-semibold text-slate-500">
                      Goal: {client.goal} · Last workout: {client.lastWorkout}
                    </p>
                  </div>
                  <div className="min-w-40">
                    <div className="flex justify-between text-xs font-black text-slate-500">
                      <span>Adherence</span>
                      <span>{client.adherence}%</span>
                    </div>
                    <div className="mt-2 h-2 rounded-full bg-slate-100">
                      <div className="h-full rounded-full bg-emerald-600" style={{ width: `${client.adherence}%` }} />
                    </div>
                  </div>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section>
          <SectionHeader
            action={
              <button
                className="rounded-lg border border-slate-200 px-3 py-2 text-sm font-black text-slate-700 hover:bg-slate-100"
                onClick={saveAvailability}
                type="button"
              >
                Save slots
              </button>
            }
            eyebrow="Plan assignment"
            title="Assign workout & diet"
          />
          <form className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm" onSubmit={submitPlans}>
            <label className="block text-sm font-bold text-slate-700">
              Client
              <select
                className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2.5 outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100"
                onChange={(event) => setPlan((current) => ({ ...current, clientId: Number(event.target.value) }))}
                value={plan.clientId}
              >
                {clients.map((client) => (
                  <option key={client.id} value={client.id}>
                    {client.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="mt-4 block text-sm font-bold text-slate-700">
              Workout plan
              <textarea
                className="mt-2 min-h-28 w-full rounded-lg border border-slate-200 px-3 py-2.5 outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100"
                onChange={(event) => setPlan((current) => ({ ...current, workout: event.target.value }))}
                value={plan.workout}
              />
            </label>
            <label className="mt-4 block text-sm font-bold text-slate-700">
              Diet plan
              <textarea
                className="mt-2 min-h-28 w-full rounded-lg border border-slate-200 px-3 py-2.5 outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100"
                onChange={(event) => setPlan((current) => ({ ...current, diet: event.target.value }))}
                value={plan.diet}
              />
            </label>
            <button
              className="mt-4 inline-flex w-full items-center justify-center gap-2 rounded-lg bg-emerald-600 px-4 py-3 text-sm font-black text-white hover:bg-emerald-700"
              type="submit"
            >
              <Send className="size-4" />
              Assign plans
            </button>
          </form>
        </section>
      </div>
    </AppLayout>
  )
}
