import { CalendarPlus, Clock, UsersRound } from 'lucide-react'
import AppLayout from '../components/AppLayout'
import SectionHeader from '../components/SectionHeader'
import { useToast } from '../context/ToastContext'
import { bookClass, bookTrainerSlot } from '../services/bookingService'
import { classes, trainerSlots } from '../utils/mockData'

export default function BookingPage() {
  const { notify } = useToast()

  const submitBooking = async (type, id, label) => {
    try {
      if (type === 'class') await bookClass(id)
      if (type === 'trainer') await bookTrainerSlot(id)
    } finally {
      notify({ title: 'Booking confirmed', message: label })
    }
  }

  return (
    <AppLayout subtitle="Member workspace" title="Bookings">
      <div className="grid gap-6 xl:grid-cols-2">
        <section>
          <SectionHeader eyebrow="Group classes" title="Book Zumba or Yoga" />
          <div className="space-y-3">
            {classes.map((classItem) => (
              <article className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm" key={classItem.id}>
                <div className="flex flex-wrap items-center justify-between gap-4">
                  <div>
                    <h3 className="text-lg font-black text-slate-950">{classItem.name}</h3>
                    <p className="mt-2 flex items-center gap-2 text-sm font-semibold text-slate-500">
                      <Clock className="size-4" />
                      {classItem.time}
                    </p>
                    <p className="mt-1 flex items-center gap-2 text-sm font-semibold text-slate-500">
                      <UsersRound className="size-4" />
                      {classItem.seats} seats left
                    </p>
                  </div>
                  <button
                    className="inline-flex items-center gap-2 rounded-lg bg-emerald-600 px-4 py-2.5 text-sm font-black text-white hover:bg-emerald-700"
                    onClick={() => submitBooking('class', classItem.id, classItem.name)}
                    type="button"
                  >
                    <CalendarPlus className="size-4" />
                    Book
                  </button>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section>
          <SectionHeader eyebrow="Personal coaching" title="Book trainer slots" />
          <div className="space-y-3">
            {trainerSlots.map((slot) => (
              <article className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm" key={slot.id}>
                <div className="flex flex-wrap items-center justify-between gap-4">
                  <div>
                    <h3 className="text-lg font-black text-slate-950">{slot.trainer}</h3>
                    <p className="mt-2 text-sm font-semibold text-slate-500">{slot.specialty}</p>
                    <p className="mt-1 text-sm font-semibold text-slate-500">{slot.time}</p>
                  </div>
                  <button
                    className="inline-flex items-center gap-2 rounded-lg bg-slate-950 px-4 py-2.5 text-sm font-black text-white hover:bg-emerald-700"
                    onClick={() => submitBooking('trainer', slot.id, `${slot.trainer} at ${slot.time}`)}
                    type="button"
                  >
                    <CalendarPlus className="size-4" />
                    Book
                  </button>
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </AppLayout>
  )
}
