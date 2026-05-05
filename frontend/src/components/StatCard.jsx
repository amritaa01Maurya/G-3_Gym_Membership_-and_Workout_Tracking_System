export default function StatCard({ icon: Icon, label, value, trend }) {
  return (
    <article className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-slate-500">{label}</p>
          <p className="mt-2 text-3xl font-black text-slate-950">{value}</p>
        </div>
        <span className="grid size-11 place-items-center rounded-lg bg-emerald-50 text-emerald-700">
          <Icon className="size-5" />
        </span>
      </div>
      {trend ? <p className="mt-4 text-sm font-semibold text-emerald-700">{trend}</p> : null}
    </article>
  )
}
