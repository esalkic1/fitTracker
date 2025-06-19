export interface Goal {
  id: string
  handle: string
  type: "WORKOUT" | "NUTRITION"
  frequency: "DAILY" | "WEEKLY" | "MONTHLY"
  target: number
  current: number
  title: string
  description: string
  createdAt: Date
  updatedAt: Date
  isCompleted: boolean
  completedAt?: Date
}

export interface CreateGoalData {
  type: "WORKOUT" | "NUTRITION"
  frequency: "DAILY" | "WEEKLY" | "MONTHLY"
  target: number
}

export interface UpdateGoalData {
  type?: "WORKOUT" | "NUTRITION"
  frequency?: "DAILY" | "WEEKLY" | "MONTHLY"
  target?: number
  current?: number
}

// Mock data store
const goals: Goal[] = [
  {
    id: "1",
    handle: "workout-weekly-5",
    type: "WORKOUT",
    frequency: "WEEKLY",
    target: 5,
    current: 3,
    title: "Weekly Workout Goal",
    description: "Complete 5 workouts per week",
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date("2024-01-15"),
    isCompleted: false,
  },
  {
    id: "2",
    handle: "nutrition-daily-2000",
    type: "NUTRITION",
    frequency: "DAILY",
    target: 2000,
    current: 1800,
    title: "Daily Calorie Goal",
    description: "Achieve 2000 calories per day",
    createdAt: new Date("2024-01-01"),
    updatedAt: new Date("2024-01-15"),
    isCompleted: false,
  },
]

function generateHandle(type: string, frequency: string, target: number): string {
  return `${type.toLowerCase()}-${frequency.toLowerCase()}-${target}`
}

function generateTitle(type: string, frequency: string): string {
  const typeLabel = type === "WORKOUT" ? "Workout" : "Nutrition"
  const freqLabel = frequency.charAt(0) + frequency.slice(1).toLowerCase()
  return `${freqLabel} ${typeLabel} Goal`
}

function generateDescription(type: string, frequency: string, target: number): string {
  const action = type === "WORKOUT" ? "Complete" : "Achieve"
  const unit = type === "WORKOUT" ? "workouts" : frequency === "DAILY" ? "calories" : "meals"
  return `${action} ${target} ${unit} per ${frequency.toLowerCase().slice(0, -2)}`
}

export const goalsService = {
  async getAllGoals(): Promise<Goal[]> {
    // Simulate API delay
    await new Promise((resolve) => setTimeout(resolve, 100))
    return [...goals]
  },

  async getGoalByHandle(handle: string): Promise<Goal | null> {
    await new Promise((resolve) => setTimeout(resolve, 100))
    return goals.find((goal) => goal.handle === handle) || null
  },

  async createGoal(data: CreateGoalData): Promise<Goal> {
    await new Promise((resolve) => setTimeout(resolve, 200))

    const id = (goals.length + 1).toString()
    const handle = generateHandle(data.type, data.frequency, data.target)
    const title = generateTitle(data.type, data.frequency)
    const description = generateDescription(data.type, data.frequency, data.target)

    const newGoal: Goal = {
      id,
      handle,
      type: data.type,
      frequency: data.frequency,
      target: data.target,
      current: 0,
      title,
      description,
      createdAt: new Date(),
      updatedAt: new Date(),
      isCompleted: false,
    }

    goals.push(newGoal)
    return newGoal
  },

  async updateGoal(handle: string, data: UpdateGoalData): Promise<Goal | null> {
    await new Promise((resolve) => setTimeout(resolve, 200))

    const goalIndex = goals.findIndex((goal) => goal.handle === handle)
    if (goalIndex === -1) return null

    const updatedGoal = {
      ...goals[goalIndex],
      ...data,
      updatedAt: new Date(),
    }

    // Regenerate handle if type, frequency, or target changed
    if (data.type || data.frequency || data.target) {
      updatedGoal.handle = generateHandle(updatedGoal.type, updatedGoal.frequency, updatedGoal.target)
      updatedGoal.title = generateTitle(updatedGoal.type, updatedGoal.frequency)
      updatedGoal.description = generateDescription(updatedGoal.type, updatedGoal.frequency, updatedGoal.target)
    }

    goals[goalIndex] = updatedGoal
    return updatedGoal
  },

  async deleteGoal(handle: string): Promise<boolean> {
    await new Promise((resolve) => setTimeout(resolve, 200))

    const goalIndex = goals.findIndex((goal) => goal.handle === handle)
    if (goalIndex === -1) return false

    goals.splice(goalIndex, 1)
    return true
  },

  async updateProgress(handle: string, progress: number): Promise<Goal | null> {
    await new Promise((resolve) => setTimeout(resolve, 100))

    const goalIndex = goals.findIndex((goal) => goal.handle === handle)
    if (goalIndex === -1) return null

    const updatedGoal = {
      ...goals[goalIndex],
      current: Math.max(0, progress),
      updatedAt: new Date(),
    }

    // Check if goal is completed
    if (updatedGoal.current >= updatedGoal.target && !updatedGoal.isCompleted) {
      updatedGoal.isCompleted = true
      updatedGoal.completedAt = new Date()
    } else if (updatedGoal.current < updatedGoal.target && updatedGoal.isCompleted) {
      updatedGoal.isCompleted = false
      updatedGoal.completedAt = undefined
    }

    goals[goalIndex] = updatedGoal
    return updatedGoal
  },

  async markComplete(handle: string): Promise<Goal | null> {
    await new Promise((resolve) => setTimeout(resolve, 100))

    const goalIndex = goals.findIndex((goal) => goal.handle === handle)
    if (goalIndex === -1) return null

    const updatedGoal = {
      ...goals[goalIndex],
      isCompleted: true,
      completedAt: new Date(),
      current: goals[goalIndex].target,
      updatedAt: new Date(),
    }

    goals[goalIndex] = updatedGoal
    return updatedGoal
  },

  getGoalStats(): {
    total: number
    completed: number
    inProgress: number
    completionRate: number
  } {
    const total = goals.length
    const completed = goals.filter((goal) => goal.isCompleted).length
    const inProgress = total - completed
    const completionRate = total > 0 ? Math.round((completed / total) * 100) : 0

    return {
      total,
      completed,
      inProgress,
      completionRate,
    }
  },
}
