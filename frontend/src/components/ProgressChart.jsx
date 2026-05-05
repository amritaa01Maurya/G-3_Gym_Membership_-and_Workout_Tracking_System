import {
  Area,
  AreaChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

export function StrengthChart({ data }) {
  return (
    <div className="h-80 rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <ResponsiveContainer height="100%" width="100%">
        <LineChart data={data}>
          <CartesianGrid stroke="#e2e8f0" strokeDasharray="4 4" />
          <XAxis dataKey="month" stroke="#64748b" />
          <YAxis stroke="#64748b" />
          <Tooltip />
          <Legend />
          <Line dataKey="squat" name="Squat kg" stroke="#059669" strokeWidth={3} type="monotone" />
          <Line dataKey="bench" name="Bench kg" stroke="#2563eb" strokeWidth={3} type="monotone" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}

export function WeightChart({ data }) {
  return (
    <div className="h-80 rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <ResponsiveContainer height="100%" width="100%">
        <AreaChart data={data}>
          <defs>
            <linearGradient id="weightFill" x1="0" x2="0" y1="0" y2="1">
              <stop offset="5%" stopColor="#14b8a6" stopOpacity={0.35} />
              <stop offset="95%" stopColor="#14b8a6" stopOpacity={0.02} />
            </linearGradient>
          </defs>
          <CartesianGrid stroke="#e2e8f0" strokeDasharray="4 4" />
          <XAxis dataKey="month" stroke="#64748b" />
          <YAxis stroke="#64748b" />
          <Tooltip />
          <Area dataKey="weight" fill="url(#weightFill)" name="Body weight kg" stroke="#0f766e" strokeWidth={3} type="monotone" />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}
