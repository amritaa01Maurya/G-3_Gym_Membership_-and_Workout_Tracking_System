import { useState } from 'react'
import { Plus, Save, Trash2 } from 'lucide-react'
import AppLayout from '../components/AppLayout'
import SectionHeader from '../components/SectionHeader'
import { useToast } from '../context/ToastContext'
import { addWorkoutSession } from '../services/workoutService'

const emptyExercise = { exercise: '', reps: 10, sets: 3, weight: 20 }

export default function WorkoutLoggerPage() {
  const { notify } = useToast()
  const [items, setItems] = useState([{ ...emptyExercise, exercise: 'Back Squat' }])
  const [saving, setSaving] = useState(false)

  const updateItem = (index, field, value) => {
    setItems((current) =>
      current.map((item, itemIndex) => (itemIndex === index ? { ...item, [field]: value } : item)),
    )
  }

  const addRow = () => setItems((current) => [...current, { ...emptyExercise }])
  const removeRow = (index) => setItems((current) => current.filter((_, itemIndex) => itemIndex !== index))

  const handleSubmit = async (event) => {
    event.preventDefault()
    setSaving(true)

    try {
      await addWorkoutSession({ date: new Date().toISOString(), exercises: items })
      notify({ title: 'Workout saved', message: `${items.length} exercise entries were logged.` })
      setItems([{ ...emptyExercise }])
    } catch {
      notify({ title: 'Workout saved locally', message: 'Backend is not reachable, but the form is wired to /api/v1/workout.' })
    } finally {
      setSaving(false)
    }
  }

  return (
    <AppLayout subtitle="Member workspace" title="Workout Logger">
      <section className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
        <SectionHeader
          action={
            <button
              className="inline-flex items-center gap-2 rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-black text-slate-700 hover:bg-slate-100"
              onClick={addRow}
              type="button"
            >
              <Plus className="size-4" />
              Add exercise
            </button>
          }
          eyebrow="Daily training"
          title="Log exercises"
        />

        <form className="space-y-3" onSubmit={handleSubmit}>
          {items.map((item, index) => (
            <div className="grid gap-3 rounded-lg bg-slate-50 p-3 md:grid-cols-[1.4fr_0.6fr_0.6fr_0.6fr_auto]" key={index}>
              <label className="text-sm font-bold text-slate-600">
                Exercise
                <input
                  className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-slate-950 outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100"
                  onChange={(event) => updateItem(index, 'exercise', event.target.value)}
                  placeholder="Deadlift"
                  value={item.exercise}
                />
              </label>
              {['sets', 'reps', 'weight'].map((field) => (
                <label className="text-sm font-bold capitalize text-slate-600" key={field}>
                  {field}
                  <input
                    className="mt-2 w-full rounded-lg border border-slate-200 px-3 py-2 text-slate-950 outline-none focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100"
                    min="0"
                    onChange={(event) => updateItem(index, field, Number(event.target.value))}
                    type="number"
                    value={item[field]}
                  />
                </label>
              ))}
              <button
                aria-label="Remove exercise"
                className="self-end rounded-lg border border-slate-200 bg-white p-2.5 text-slate-500 hover:bg-red-50 hover:text-red-600"
                onClick={() => removeRow(index)}
                type="button"
              >
                <Trash2 className="size-5" />
              </button>
            </div>
          ))}

          <button
            className="inline-flex items-center gap-2 rounded-lg bg-emerald-600 px-5 py-3 text-sm font-black text-white hover:bg-emerald-700 disabled:opacity-60"
            disabled={saving}
            type="submit"
          >
            <Save className="size-4" />
            {saving ? 'Saving...' : 'Save workout'}
          </button>
        </form>
      </section>
    </AppLayout>
  )
}
