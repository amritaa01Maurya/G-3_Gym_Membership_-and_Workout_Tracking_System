export const progressData = [
  { month: 'Jan', weight: 84, squat: 75, bench: 52 },
  { month: 'Feb', weight: 82, squat: 82, bench: 57 },
  { month: 'Mar', weight: 80, squat: 90, bench: 62 },
  { month: 'Apr', weight: 78, squat: 96, bench: 68 },
  { month: 'May', weight: 77, squat: 104, bench: 72 },
]

export const revenueData = [
  { month: 'Jan', revenue: 42000 },
  { month: 'Feb', revenue: 51000 },
  { month: 'Mar', revenue: 58000 },
  { month: 'Apr', revenue: 64000 },
  { month: 'May', revenue: 72000 },
]

export const plans = [
  { id: 'basic', name: 'Basic', price: 999, perks: ['Gym floor', 'Locker', 'Progress tracking'] },
  { id: 'plus', name: 'Plus', price: 1799, perks: ['Classes', 'Trainer consult', 'Body scan'] },
  { id: 'elite', name: 'Elite', price: 2999, perks: ['Unlimited trainer', 'Diet plan', 'Recovery suite'] },
]

export const classes = [
  { id: 1, name: 'Zumba Energy', time: 'Today, 6:00 PM', seats: 8 },
  { id: 2, name: 'Yoga Flow', time: 'Tomorrow, 7:30 AM', seats: 12 },
  { id: 3, name: 'HIIT Circuit', time: 'Friday, 5:30 PM', seats: 5 },
]

export const trainerSlots = [
  { id: 1, trainer: 'Aisha Khan', specialty: 'Strength', time: 'Today, 8:00 PM' },
  { id: 2, trainer: 'Rahul Mehta', specialty: 'Fat loss', time: 'Tomorrow, 9:00 AM' },
  { id: 3, trainer: 'Meera Shah', specialty: 'Mobility', time: 'Saturday, 11:00 AM' },
]

export const members = [
  { id: 1, name: 'Nisha Patel', role: 'Member', plan: 'Elite', status: 'Active' },
  { id: 2, name: 'Arjun Rao', role: 'Member', plan: 'Plus', status: 'Renewal due' },
  { id: 3, name: 'Kabir Sethi', role: 'Trainer', plan: '-', status: 'Active' },
]

export const clients = [
  { id: 1, name: 'Nisha Patel', goal: 'Strength', adherence: 92, lastWorkout: 'Lower body' },
  { id: 2, name: 'Arjun Rao', goal: 'Weight loss', adherence: 78, lastWorkout: 'HIIT' },
  { id: 3, name: 'Dev Malhotra', goal: 'Mobility', adherence: 84, lastWorkout: 'Yoga flow' },
]
