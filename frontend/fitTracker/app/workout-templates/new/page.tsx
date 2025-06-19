"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { Badge } from "@/components/ui/badge"
import { Switch } from "@/components/ui/switch"
import { Plus, Trash2, Save, ArrowLeft } from "lucide-react"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useRouter } from "next/navigation"
import Link from "next/link"

interface ExerciseTemplate {
  id: string
  exerciseDetailsId: string
  name: string
  description: string
}

interface ExerciseDetail {
  id: string
  uuid: string
  name: string
  description: string
  muscleGroup: string
  equipment: string
  difficultyLevel: string
}

export default function NewWorkoutTemplatePage() {
  const router = useRouter()
  const [templateName, setTemplateName] = useState("")
  const [templateDescription, setTemplateDescription] = useState("")
  const [exerciseTemplates, setExerciseTemplates] = useState<ExerciseTemplate[]>([])
  const [exerciseDetails, setExerciseDetails] = useState<ExerciseDetail[]>([])
    const [loading, setLoading] = useState({
      exerciseDetails: true,
      saving: false
    })
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
      const fetchExerciseDetails = async () => {
        try {
          setLoading(prev => ({...prev, exerciseDetails: true}))
          setError(null)
          const response = await fetch(
            "http://localhost:8000/workout-service/api/v1/exercise-details",
            { headers: authHeaders() }
          )
          
          if (!response.ok) {
            throw new Error(`Failed to fetch exercises: ${response.status}`)
          }
          
          const data = await response.json()
          setExerciseDetails(data)
        } catch (error) {
          setError(error.message)
          console.error("Error fetching exercise details:", error)
        } finally {
          setLoading(prev => ({...prev, exerciseDetails: false}))
        }
      }
  
      fetchExerciseDetails()
    }, [])


  const addExerciseTemplate = () => {
    const newExerciseTemplate: ExerciseTemplate = {
      id: Date.now().toString(),
      exerciseDetailsId: "",
      name: "",
      description: ""
    }
    setExerciseTemplates([...exerciseTemplates, newExerciseTemplate])
  }

  const removeExerciseTemplate = (id: string) => {
    setExerciseTemplates(exerciseTemplates.filter((ex) => ex.id !== id))
  }

  const updateExerciseTemplate = (id: string, field: keyof ExerciseTemplate, value: any) => {
    setExerciseTemplates(
      exerciseTemplates.map((ex) => {
        if (ex.id === id) {
          if (field === "exerciseDetailsId") {
            const selectedDetail = exerciseDetails.find((detail) => detail.id === value)
            return {
              ...ex,
              [field]: value,
              name: selectedDetail?.name || "",
              description: selectedDetail?.description || ""
            }
          }
          return { ...ex, [field]: value }
        }
        return ex
      }),
    )
  }

  const saveTemplate = async () => {
  if (!templateName.trim() || exerciseTemplates.length === 0) {
    alert("Please fill in all required fields and add at least one exercise template")
    return
  }

  const user = JSON.parse(localStorage.getItem("user") || "{}")
  const userHandle = user.handle || ""

  const body = {
    workoutTemplate: {
      name: templateName,
      description: templateDescription,
      userHandle: userHandle
    },
    exerciseTemplates: exerciseTemplates.map((et) => ({
      exerciseDetailsId: parseInt(et.exerciseDetailsId)
    }))
  }

  try {
    setLoading((prev) => ({ ...prev, saving: true }))
    setError(null)

    const response = await fetch("http://localhost:8000/workout-service/api/v1/workout-template/with-exercises", {
      method: "POST",
      headers: authHeaders(),
      body: JSON.stringify(body)
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`Failed to save template: ${response.status} - ${errorText}`)
    }

    router.push("/workout-templates")
  } catch (err: any) {
    console.error("Error saving template:", err)
    alert(err.message || "Unexpected error occurred.")
  } finally {
    setLoading((prev) => ({ ...prev, saving: false }))
  }
}

  const getMuscleGroupColor = (muscleGroup: string) => {
    const colors = {
      Chest: "bg-fitness-electric/20 text-fitness-electric",
      Back: "bg-fitness-success/20 text-fitness-success",
      Legs: "bg-fitness-energy/20 text-fitness-energy",
      Shoulders: "bg-fitness-warning/20 text-fitness-warning",
      Triceps: "bg-purple-500/20 text-purple-400",
      Biceps: "bg-pink-500/20 text-pink-400",
    }
    return colors[muscleGroup as keyof typeof colors] || "bg-slate-600/20 text-slate-400"
  }

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
            <h1 className="text-4xl font-bold">
              <span className="fitness-text-gradient">New Workout Template</span>
            </h1>
            <p className="text-slate-400">Create a reusable workout template with exercise templates</p>
          </div>
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => router.back()}>
              Cancel
            </Button>
            <Button onClick={saveTemplate} className="fitness-button-primary">
              <Save className="h-4 w-4 mr-2" />
              Save Template
            </Button>
          </div>
        </div>

        {/* Template Details */}
        <Card className="fitness-card">
          <CardHeader>
            <CardTitle className="text-white">Template Details</CardTitle>
            <CardDescription className="text-slate-400">Basic information about your workout template</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="templateName" className="text-slate-200">
                  Template Name *
                </Label>
                <Input
                  id="templateName"
                  placeholder="e.g., Push Day Power, Full Body HIIT"
                  value={templateName}
                  onChange={(e) => setTemplateName(e.target.value)}
                  className="bg-slate-800/50 border-slate-700 text-white"
                />
              </div>
            </div>

            <div>
              <Label htmlFor="templateDescription" className="text-slate-200">
                Description
              </Label>
              <Textarea
                id="templateDescription"
                placeholder="Describe the purpose and focus of this workout template..."
                value={templateDescription}
                onChange={(e) => setTemplateDescription(e.target.value)}
                className="bg-slate-800/50 border-slate-700 text-white"
                rows={3}
              />
            </div>
          </CardContent>
        </Card>

        {/* Exercise Templates */}
        <Card className="fitness-card">
          <CardHeader>
            <div className="flex justify-between items-center">
              <div>
                <CardTitle className="text-white">Exercise Templates</CardTitle>
                <CardDescription className="text-slate-400">
                  Add exercise templates to define which exercises are included in this workout
                </CardDescription>
              </div>
              <Button onClick={addExerciseTemplate} className="fitness-button-primary">
                <Plus className="h-4 w-4 mr-2" />
                Add Exercise Template
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            {exerciseTemplates.length === 0 ? (
              <div className="text-center py-8 text-slate-500">
                No exercise templates added yet. Click "Add Exercise Template" to get started.
              </div>
            ) : (
              <div className="space-y-4">
                {exerciseTemplates.map((exerciseTemplates, index) => (
                  <div key={exerciseTemplates.id} className="p-4 border rounded-lg space-y-4">
                    <div className="flex justify-between items-center">
                      <h4 className="font-medium">Exercise Template {index + 1}</h4>
                      <Button variant="ghost" size="sm" onClick={() => removeExerciseTemplate(exerciseTemplates.id)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <Label>Exercise Template</Label>
                        <Select
                          value={exerciseTemplates.exerciseDetailsId}
                          onValueChange={(value) => updateExerciseTemplate(exerciseTemplates.id, "exerciseDetailsId", value)}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Select an exercise" />
                          </SelectTrigger>
                          <SelectContent>
                            {exerciseDetails.map((detail) => (
                              <SelectItem key={detail.id} value={detail.id}>
                                {detail.name} ({detail.muscleGroup})
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
