"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { Badge } from "@/components/ui/badge"
import { Plus, Trash2, Save, ArrowLeft } from "lucide-react"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useRouter, useParams } from "next/navigation"
import Link from "next/link"
import { toast } from "sonner"

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

interface WorkoutTemplateResponse {
  id: number
  uuid: string
  name: string
  description: string
  exerciseTemplates: Array<{
    id: number
    uuid: string
    exerciseDetails: ExerciseDetail
  }>
}

export default function EditWorkoutTemplatePage() {
  const router = useRouter()
  const params = useParams()
  const templateId = params.id as string

  const [templateName, setTemplateName] = useState("")
  const [templateDescription, setTemplateDescription] = useState("")
  const [exerciseTemplates, setExerciseTemplates] = useState<ExerciseTemplate[]>([])
  const [exerciseDetails, setExerciseDetails] = useState<ExerciseDetail[]>([])
  const [loading, setLoading] = useState({
    exerciseDetails: true,
    template: true,
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
    const fetchData = async () => {
      try {
        setLoading(prev => ({...prev, exerciseDetails: true, template: true}))
        setError(null)
        
        // Fetch exercise details
        const detailsResponse = await fetch(
          "http://localhost:8000/workout-service/api/v1/exercise-details",
          { headers: authHeaders() }
        )
        
        if (!detailsResponse.ok) {
          throw new Error(`Failed to fetch exercises: ${detailsResponse.status}`)
        }
        
        const detailsData = await detailsResponse.json()
        setExerciseDetails(detailsData)
        
        // Fetch template data if we're editing
        if (templateId) {
          const templateResponse = await fetch(
            `http://localhost:8000/workout-service/api/v1/workout-template/${templateId}`,
            { headers: authHeaders() }
          )
          
          if (!templateResponse.ok) {
            throw new Error(`Failed to fetch template: ${templateResponse.status}`)
          }
          
          const templateData: WorkoutTemplateResponse = await templateResponse.json()
          setTemplateName(templateData.name)
          setTemplateDescription(templateData.description)
          
          // Map exercise templates from API to our local format
          const mappedTemplates = templateData.exerciseTemplates.map(et => ({
            id: et.id.toString(),
            exerciseDetailsId: et.exerciseDetails.id.toString(),
            name: et.exerciseDetails.name,
            description: et.exerciseDetails.description
          }))
          
          setExerciseTemplates(mappedTemplates)
        }
      } catch (error: any) {
        setError(error.message)
        console.error("Error fetching data:", error)
      } finally {
        setLoading(prev => ({...prev, exerciseDetails: false, template: false}))
      }
    }

    fetchData()
  }, [templateId])

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

  try {
    setLoading((prev) => ({ ...prev, saving: true }))
    setError(null)

    // First, update the workout template
    const workoutTemplateResponse = await fetch(
      `http://localhost:8000/workout-service/api/v1/workout-template/${templateId}`,
      {
        method: "PUT",
        headers: authHeaders(),
        body: JSON.stringify({
          name: templateName,
          description: templateDescription
        })
      }
    )

    if (!workoutTemplateResponse.ok) {
      const errorText = await workoutTemplateResponse.text()
      throw new Error(`Failed to update workout template: ${workoutTemplateResponse.status} - ${errorText}`)
    }

    // Then update each exercise template
    const exerciseTemplateUpdates = exerciseTemplates.map(async (et) => {
      const response = await fetch(
        `http://localhost:8000/workout-service/api/v1/exercise-template/${et.id}`,
        {
          method: "PUT",
          headers: authHeaders(),
          body: JSON.stringify({
            exerciseDetailsId: parseInt(et.exerciseDetailsId),
            workoutTemplateId: parseInt(templateId)
          })
        }
      )

      if (!response.ok) {
        const errorText = await response.text()
        throw new Error(`Failed to update exercise template ${et.id}: ${response.status} - ${errorText}`)
      }
      return response
    })

    // Wait for all exercise template updates to complete
    await Promise.all(exerciseTemplateUpdates)

    toast.success("Workout template updated successfully!")
    router.push("/workout-templates")
  } catch (err: any) {
    console.error("Error saving template:", err)
    toast.error(err.message || "Unexpected error occurred while saving template")
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

  if (loading.template || loading.exerciseDetails) {
    return (
      <DashboardLayout>
        <div className="space-y-6">
          <div className="flex justify-between items-center">
            <div>
              <div className="h-8 w-48 bg-slate-700 rounded animate-pulse mb-2" />
              <div className="h-6 w-64 bg-slate-700 rounded animate-pulse" />
            </div>
            <div className="flex gap-2">
              <div className="h-10 w-24 bg-slate-700 rounded animate-pulse" />
              <div className="h-10 w-24 bg-slate-700 rounded animate-pulse" />
            </div>
          </div>
          
          <div className="space-y-4">
            <div className="h-96 bg-slate-800 rounded animate-pulse" />
            <div className="h-96 bg-slate-800 rounded animate-pulse" />
          </div>
        </div>
      </DashboardLayout>
    )
  }

  if (error) {
    return (
      <DashboardLayout>
        <div className="space-y-6">
          <div className="text-center py-8">
            <h2 className="text-xl font-bold text-red-500 mb-2">Error Loading Template</h2>
            <p className="text-slate-400 mb-4">{error}</p>
            <Button onClick={() => router.push("/workout-templates")}>
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to Templates
            </Button>
          </div>
        </div>
      </DashboardLayout>
    )
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
              <span className="fitness-text-gradient">
                {templateId ? "Edit Workout Template" : "Create Workout Template"}
              </span>
            </h1>
            <p className="text-slate-400">Create a reusable workout template with exercise templates</p>
          </div>
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => router.back()}>
              Cancel
            </Button>
            <Button onClick={saveTemplate} className="fitness-button-primary" disabled={loading.saving}>
              {loading.saving ? (
                <>
                  <span className="animate-spin mr-2">○</span>
                  Saving...
                </>
              ) : (
                <>
                  <Save className="h-4 w-4 mr-2" />
                  Save Template
                </>
              )}
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
                {exerciseTemplates.map((exerciseTemplate, index) => (
                  <div key={exerciseTemplate.id} className="p-4 border border-slate-700 rounded-lg space-y-4">
                    <div className="flex justify-between items-center">
                      <h4 className="font-medium text-white">Exercise Template {index + 1}</h4>
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        onClick={() => removeExerciseTemplate(exerciseTemplate.id)}
                        className="text-red-400 hover:bg-red-500/20"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <Label className="text-slate-200">Exercise Template *</Label>
                        <Select
                          value={exerciseTemplate.exerciseDetailsId}
                          onValueChange={(value) => updateExerciseTemplate(exerciseTemplate.id, "exerciseDetailsId", value)}
                        >
                          <SelectTrigger className="bg-slate-800/50 border-slate-700 text-white">
                            <SelectValue placeholder="Select an exercise" />
                          </SelectTrigger>
                          <SelectContent className="bg-slate-800 border-slate-700">
                            {exerciseDetails.map((detail) => (
                              <SelectItem 
                                key={detail.id} 
                                value={detail.id.toString()}
                                className="hover:bg-slate-700"
                              >
                                <div className="flex items-center gap-2">
                                  <span>{detail.name}</span>
                                  <Badge className={getMuscleGroupColor(detail.muscleGroup)}>
                                    {detail.muscleGroup}
                                  </Badge>
                                </div>
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                    </div>

                    {exerciseTemplate.exerciseDetailsId && (
                      <div className="p-3 bg-slate-800/50 rounded-lg">
                        <div className="flex flex-wrap gap-2 mb-2">
                          <Badge className={getMuscleGroupColor(
                            exerciseDetails.find(d => d.id.toString() === exerciseTemplate.exerciseDetailsId)?.muscleGroup || ""
                          )}>
                            {exerciseDetails.find(d => d.id.toString() === exerciseTemplate.exerciseDetailsId)?.muscleGroup}
                          </Badge>
                          <Badge variant="secondary">
                            {exerciseDetails.find(d => d.id.toString() === exerciseTemplate.exerciseDetailsId)?.equipment}
                          </Badge>
                          <Badge variant="secondary">
                            {exerciseDetails.find(d => d.id.toString() === exerciseTemplate.exerciseDetailsId)?.difficultyLevel}
                          </Badge>
                        </div>
                        <p className="text-sm text-slate-300">
                          {exerciseDetails.find(d => d.id.toString() === exerciseTemplate.exerciseDetailsId)?.description}
                        </p>
                      </div>
                    )}
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