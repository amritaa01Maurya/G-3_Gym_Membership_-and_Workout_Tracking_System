export default function SectionHeader({ action, eyebrow, title }) {
  return (
    <div className="mb-4 flex flex-wrap items-end justify-between gap-3">
      <div>
        {eyebrow ? <p className="text-sm font-bold text-emerald-700">{eyebrow}</p> : null}
        <h2 className="text-xl font-black tracking-normal text-slate-950">{title}</h2>
      </div>
      {action}
    </div>
  )
}
