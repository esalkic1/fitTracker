"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import { Clock, Dumbbell, Target, ArrowLeft, Play, Edit, Trash2, User, Calendar } from "lucide-react"
import Link from "next/link"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useRouter, useParams } from "next/navigation"

interface ExerciseTemplate {
  id: number
  uuid: string
  exerciseDetails: {
    id: number
    uuid: string
    name: string
    description: string
    muscleGroup: string
    equipment: string
    difficultyLevel: string
  }
}

interface WorkoutTemplate {
  id: number
  uuid: string
  name: string
  description: string
  exerciseTemplates: ExerciseTemplate[]
  createdAt: string
  updatedAt: string
  usageCount: number
  isPublic: boolean
  tags: string[]
}

export default function WorkoutTemplateDetailsPage() {
  const router = useRouter()
  const params = useParams()
  const templateId = params.id

  const [template, setTemplate] = useState<WorkoutTemplate | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const authHeaders = () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}")
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle || "",
      "X-Role": user.role || "",
    }
  }

  useEffect(() => {
    const fetchTemplate = async () => {
      try {
        setLoading(true)
        setError(null)
        const response = await fetch(
          `http://localhost:8000/workout-service/api/v1/workout-template/${templateId}`,
          { headers: authHeaders() }
        )
        
        if (!response.ok) {
          throw new Error(`Failed to fetch template: ${response.status}`)
        }
        
        const data = await response.json()
        setTemplate(data)
      } catch (error) {
        setError(error.message)
        console.error("Error fetching template:", error)
      } finally {
        setLoading(false)
      }
    }

    fetchTemplate()
  }, [templateId])

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case "Beginner":
        return "bg-fitness-success/20 text-fitness-success"
      case "Intermediate":
        return "bg-fitness-warning/20 text-fitness-warning"
      case "Advanced":
      case "Hard":
        return "bg-fitness-energy/20 text-fitness-energy"
      default:
        return "bg-slate-600/20 text-slate-400"
    }
  }

  const getMuscleGroupColor = (muscleGroup: string) => {
    const colors = {
      Chest: "bg-fitness-electric/20 text-fitness-electric",
      Shoulders: "bg-fitness-warning/20 text-fitness-warning",
      Triceps: "bg-fitness-energy/20 text-fitness-energy",
      Back: "bg-fitness-success/20 text-fitness-success",
      Legs: "bg-purple-500/20 text-purple-400",
      Core: "bg-pink-500/20 text-pink-400",
    }
    return colors[muscleGroup as keyof typeof colors] || "bg-slate-600/20 text-slate-400"
  }

  const startWorkout = () => {
    if (template) {
      router.push(`/workouts/new?fromTemplate=${template.id}`)
    }
  }

  const deleteTemplate = async () => {
    try {
      const response = await fetch(
        `http://localhost:8000/workout-service/api/v1/workout-template/${templateId}`,
        {
          method: "DELETE",
          headers: authHeaders()
        }
      )

      if (!response.ok) {
        throw new Error(`Failed to delete template: ${response.status}`)
      }

      router.push("/workout-templates")
    } catch (error) {
      console.error("Error deleting template:", error)
      alert(`Failed to delete template: ${error.message}`)
    }
  }

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString("en-US", {
      year: "numeric",
      month: "long",
      day: "numeric"
    })
  }

  if (loading) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center h-64">
          <p className="text-gray-500">Loading template details...</p>
        </div>
      </DashboardLayout>
    )
  }

  if (error) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center h-64">
          <p className="text-red-500">Error: {error}</p>
        </div>
      </DashboardLayout>
    )
  }

  if (!template) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center h-64">
          <p className="text-gray-500">Template not found</p>
        </div>
      </DashboardLayout>
    )
  }

  // Calculate total sets (assuming 3 sets per exercise as default)
  const totalSets = template.exerciseTemplates.length * 3
  // Estimate duration (assuming 2 minutes per set)
  const estimatedDuration = `${Math.round(totalSets * 2)}-${Math.round(totalSets * 2.5)} min`

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Button variant="ghost" size="sm" asChild className="text-slate-400 hover:text-white">
                <Link href="/workout-templates">
                  <ArrowLeft className="h-4 w-4 mr-1" />
                  Back to Templates
                </Link>
              </Button>
            </div>
            <h1 className="text-4xl font-bold text-white">{template.name}</h1>
            <p className="text-slate-400 mt-2">{template.description}</p>
          </div>
          <div className="flex gap-2">
            <Button className="fitness-button-primary" onClick={startWorkout}>
              <Play className="h-4 w-4 mr-2" />
              Start Workout
            </Button>
            <Button
              variant="outline"
              className="border-fitness-warning text-fitness-warning hover:bg-fitness-warning hover:text-fitness-dark"
              asChild
            >
              <Link href={`/workout-templates/${template.id}/edit`}>
                <Edit className="h-4 w-4 mr-2" />
                Edit
              </Link>
            </Button>
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="outline" className="border-red-500 text-red-400 hover:bg-red-500 hover:text-white">
                  <Trash2 className="h-4 w-4 mr-2" />
                  Delete
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent className="fitness-card border-slate-700">
                <AlertDialogHeader>
                  <AlertDialogTitle className="text-white">Delete Template</AlertDialogTitle>
                  <AlertDialogDescription className="text-slate-400">
                    Are you sure you want to delete "{template.name}"? This action cannot be undone.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel className="border-slate-600 text-slate-300 hover:bg-slate-800">
                    Cancel
                  </AlertDialogCancel>
                  <AlertDialogAction onClick={deleteTemplate} className="bg-red-500 hover:bg-red-600 text-white">
                    Delete Template
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </div>
        </div>

        {/* Template Details */}
        <div className="grid grid-cols-1 lg:grid-cols-1 gap-6">

          <Card className="fitness-card lg:col-span-2">
            <CardHeader>
              <CardTitle className="text-white">Exercises ({template.exerciseTemplates.length})</CardTitle>
              <CardDescription className="text-slate-400">Complete exercise breakdown</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {template.exerciseTemplates.map((exercise) => (
                  <div key={exercise.id} className="p-4 bg-slate-800/30 border border-slate-700/50 rounded-lg">
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <h4 className="font-medium text-white">{exercise.exerciseDetails.name}</h4>
                        <p className="text-sm text-slate-400">{exercise.exerciseDetails.description}</p>
                      </div>
                      <div className="flex gap-2">
                        <Badge className={getMuscleGroupColor(exercise.exerciseDetails.muscleGroup)}>
                          {exercise.exerciseDetails.muscleGroup}
                        </Badge>
                        <Badge className={getDifficultyColor(exercise.exerciseDetails.difficultyLevel)}>
                          {exercise.exerciseDetails.difficultyLevel}
                        </Badge>
                      </div>
                    </div>

                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-3">
                      <div>
                        <p className="text-xs text-slate-500">Sets</p>
                        <p className="text-sm font-medium text-white">3</p>
                      </div>
                      <div>
                        <p className="text-xs text-slate-500">Reps</p>
                        <p className="text-sm font-medium text-white">8-12</p>
                      </div>
                      <div>
                        <p className="text-xs text-slate-500">Rest</p>
                        <p className="text-sm font-medium text-white">2-3 min</p>
                      </div>
                      <div>
                        <p className="text-xs text-slate-500">Equipment</p>
                        <p className="text-sm font-medium text-white">{exercise.exerciseDetails.equipment}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  )
}