import { BadgeIndianRupee, RefreshCcw, UserCheck, Users } from 'lucide-react'
import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import AppLayout from '../components/AppLayout'
import SectionHeader from '../components/SectionHeader'
import Skeleton from '../components/Skeleton'
import StatCard from '../components/StatCard'
import { members, revenueData } from '../utils/mockData'

export default function AdminDashboard() {
  return (
    <AppLayout subtitle="Admin workspace" title="Operations Dashboard">
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <StatCard icon={Users} label="Total members" trend="+18 this month" value="1,248" />
        <StatCard icon={UserCheck} label="Active memberships" trend="91% retention" value="1,096" />
        <StatCard icon={BadgeIndianRupee} label="Revenue" trend="+14.2% MoM" value="₹7.2L" />
        <StatCard icon={RefreshCcw} label="Renewals due" trend="32 due this week" value="86" />
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_420px]">
        <section>
          <SectionHeader eyebrow="Revenue" title="Monthly performance" />
          <div className="h-80 rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
            <ResponsiveContainer height="100%" width="100%">
              <BarChart data={revenueData}>
                <CartesianGrid stroke="#e2e8f0" strokeDasharray="4 4" />
                <XAxis dataKey="month" stroke="#64748b" />
                <YAxis stroke="#64748b" />
                <Tooltip formatter={(value) => `₹${value.toLocaleString('en-IN')}`} />
                <Bar dataKey="revenue" fill="#059669" name="Revenue" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </section>

        <section>
          <SectionHeader eyebrow="System health" title="Loading states" />
          <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
            <Skeleton className="h-5 w-1/2" />
            <Skeleton className="mt-4 h-24 w-full" />
            <Skeleton className="mt-3 h-10 w-2/3" />
          </div>
        </section>
      </div>

      <section className="mt-6">
        <SectionHeader eyebrow="Users" title="Manage members and renewals" />
        <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
          <div className="overflow-x-auto">
            <table className="w-full min-w-[640px] text-left text-sm">
              <thead className="bg-slate-100 text-slate-600">
                <tr>
                  <th className="px-5 py-3 font-black">Name</th>
                  <th className="px-5 py-3 font-black">Role</th>
                  <th className="px-5 py-3 font-black">Plan</th>
                  <th className="px-5 py-3 font-black">Status</th>
                  <th className="px-5 py-3 font-black">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                {members.map((member) => (
                  <tr key={member.id}>
                    <td className="px-5 py-4 font-bold text-slate-950">{member.name}</td>
                    <td className="px-5 py-4 text-slate-600">{member.role}</td>
                    <td className="px-5 py-4 text-slate-600">{member.plan}</td>
                    <td className="px-5 py-4">
                      <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-black text-emerald-700">
                        {member.status}
                      </span>
                    </td>
                    <td className="px-5 py-4">
                      <button className="rounded-lg border border-slate-200 px-3 py-2 font-black text-slate-700 hover:bg-slate-100" type="button">
                        Manage
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </AppLayout>
  )
}
