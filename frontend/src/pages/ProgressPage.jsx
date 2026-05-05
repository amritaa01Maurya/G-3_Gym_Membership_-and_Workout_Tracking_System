import { Award, Scale, TrendingUp } from 'lucide-react'
import AppLayout from '../components/AppLayout'
import { StrengthChart, WeightChart } from '../components/ProgressChart'
import SectionHeader from '../components/SectionHeader'
import StatCard from '../components/StatCard'
import { progressData } from '../utils/mockData'

export default function ProgressPage() {
  return (
    <AppLayout subtitle="Member workspace" title="Progress Tracking">
      <div className="grid gap-4 md:grid-cols-3">
        <StatCard icon={Scale} label="Weight change" trend="Down 7 kg" value="84 → 77" />
        <StatCard icon={TrendingUp} label="Squat progress" trend="+29 kg since January" value="104 kg" />
        <StatCard icon={Award} label="Latest badge" trend="Unlocked this week" value="Power Builder" />
      </div>

      <div className="mt-6 grid gap-6 xl:grid-cols-2">
        <section>
          <SectionHeader eyebrow="Body metrics" title="Weight trend" />
          <WeightChart data={progressData} />
        </section>
        <section>
          <SectionHeader eyebrow="Strength metrics" title="Lift progression" />
          <StrengthChart data={progressData} />
        </section>
      </div>
    </AppLayout>
  )
}
