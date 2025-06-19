"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { useParams, useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ArrowLeft, Save, Loader2 } from "lucide-react"
import { toast } from "sonner"

const equipmentOptions = [
  "Bodyweight",
  "Dumbbells",
  "Barbell",
  "Resistance Bands",
  "Kettlebell",
  "Cable Machine",
  "Pull-up Bar",
  "Bench",
  "Medicine Ball",
  "Foam Roller",
]

const difficultyLevels = ["Beginner", "Intermediate", "Advanced", "Expert"]

const availableMuscleGroups = [
  "Chest",
  "Back",
  "Shoulders",
  "Biceps",
  "Triceps",
  "Forearms",
  "Core",
  "Quadriceps",
  "Hamstrings",
  "Glutes",
  "Calves",
  "Full Body",
]

export default function EditExercisePage() {
  const params = useParams()
  const router = useRouter()
  const exerciseId = params.id as string

  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [exercise, setExercise] = useState<any>(null)
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    equipment: "",
    difficulty: "",
    muscleGroup: "",
  })
  const [errors, setErrors] = useState<Record<string, string>>({})

  const authHeaders = () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}")
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle || "",
      "X-Role": user.role || "",
    }
  }

  useEffect(() => {
    const fetchExercise = async () => {
      try {
        const response = await fetch(
          `http://localhost:8000/workout-service/api/v1/exercise-details/${exerciseId}`,
          { headers: authHeaders() }
        )

        if (!response.ok) {
          throw new Error("Failed to fetch exercise")
        }

        const data = await response.json()
        setExercise(data)
        setFormData({
          name: data.name,
          description: data.description,
          equipment: data.equipment,
          difficulty: data.difficultyLevel,
          muscleGroup: data.muscleGroup,
        })
      } catch (error) {
        console.error("Error fetching exercise:", error)
        toast.error("Failed to load exercise data")
      } finally {
        setLoading(false)
      }
    }

    fetchExercise()
  }, [exerciseId])

  const validateForm = () => {
    const newErrors: Record<string, string> = {}

    if (!formData.name.trim()) newErrors.name = "Exercise name is required"
    if (!formData.description.trim()) newErrors.description = "Description is required"
    if (!formData.equipment) newErrors.equipment = "Equipment is required"
    if (!formData.difficulty) newErrors.difficulty = "Difficulty is required"
    if (!formData.muscleGroup) newErrors.muscleGroup = "Muscle group is required"

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) {
      toast.error("Please fix the errors in the form")
      return
    }

    setSaving(true)

    try {
      const response = await fetch(
        `http://localhost:8000/workout-service/api/v1/exercise-details/${exerciseId}`,
        {
          method: "PUT",
          headers: authHeaders(),
          body: JSON.stringify({
            name: formData.name,
            description: formData.description,
            equipment: formData.equipment,
            difficultyLevel: formData.difficulty,
            muscleGroup: formData.muscleGroup,
          }),
        }
      )

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || "Failed to update exercise")
      }

      toast.success("Exercise updated successfully!")
      router.push("/admin/exercises")
    } catch (error: any) {
      console.error("Error updating exercise:", error)
      toast.error(error.message || "Failed to update exercise")
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto">
          <div className="flex items-center gap-4 mb-8">
            <div className="h-10 w-10 bg-gray-200 rounded animate-pulse" />
            <div className="h-8 w-48 bg-gray-200 rounded animate-pulse" />
          </div>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="space-y-6">
              {[...Array(5)].map((_, i) => (
                <div key={i} className="space-y-2">
                  <div className="h-4 w-24 bg-gray-200 rounded animate-pulse" />
                  <div className="h-10 w-full bg-gray-200 rounded animate-pulse" />
                </div>
              ))}
            </div>
            <div className="h-96 bg-gray-200 rounded animate-pulse" />
          </div>
        </div>
      </div>
    )
  }

  if (!exercise) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Exercise Not Found</h1>
          <p className="text-gray-600 mb-8">The exercise you're looking for doesn't exist.</p>
          <Button onClick={() => router.push("/admin/exercises")}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Exercises
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="flex items-center gap-4 mb-8">
          <Button variant="ghost" size="sm" onClick={() => router.push("/admin/exercises")}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back
          </Button>
          <div>
            <h1 className="text-3xl font-bold text-white-900">Edit Exercise</h1>
            <p className="text-gray-600">Update exercise information and details</p>
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Form Section */}
            <div className="space-y-6">
              {/* Exercise Name */}
              <div className="space-y-2">
                <Label htmlFor="name">Exercise Name *</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData((prev) => ({ ...prev, name: e.target.value }))}
                  placeholder="Enter exercise name"
                  className={errors.name ? "border-red-500" : ""}
                />
                {errors.name && <p className="text-sm text-red-500">{errors.name}</p>}
              </div>

              {/* Description */}
              <div className="space-y-2">
                <Label htmlFor="description">Description *</Label>
                <Textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                  placeholder="Describe the exercise"
                  rows={3}
                  className={errors.description ? "border-red-500" : ""}
                />
                {errors.description && <p className="text-sm text-red-500">{errors.description}</p>}
              </div>

              {/* Equipment */}
              <div className="space-y-2">
                <Label htmlFor="equipment">Equipment *</Label>
                <Select
                  value={formData.equipment}
                  onValueChange={(value) => setFormData((prev) => ({ ...prev, equipment: value }))}
                >
                  <SelectTrigger className={errors.equipment ? "border-red-500" : ""}>
                    <SelectValue placeholder="Select equipment" />
                  </SelectTrigger>
                  <SelectContent>
                    {equipmentOptions.map((equipment) => (
                      <SelectItem key={equipment} value={equipment}>
                        {equipment}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {errors.equipment && <p className="text-sm text-red-500">{errors.equipment}</p>}
              </div>

              {/* Difficulty */}
              <div className="space-y-2">
                <Label htmlFor="difficulty">Difficulty *</Label>
                <Select
                  value={formData.difficulty}
                  onValueChange={(value) => setFormData((prev) => ({ ...prev, difficulty: value }))}
                >
                  <SelectTrigger className={errors.difficulty ? "border-red-500" : ""}>
                    <SelectValue placeholder="Select difficulty" />
                  </SelectTrigger>
                  <SelectContent>
                    {difficultyLevels.map((level) => (
                      <SelectItem key={level} value={level}>
                        {level}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {errors.difficulty && <p className="text-sm text-red-500">{errors.difficulty}</p>}
              </div>

              {/* Muscle Group */}
              <div className="space-y-2">
                <Label>Muscle Group *</Label>
                <Select
                  value={formData.muscleGroup}
                  onValueChange={(value) => setFormData((prev) => ({ ...prev, muscleGroup: value }))}
                >
                  <SelectTrigger className={errors.muscleGroup ? "border-red-500" : ""}>
                    <SelectValue placeholder="Select muscle group" />
                  </SelectTrigger>
                  <SelectContent>
                    {availableMuscleGroups.map((muscleGroup) => (
                      <SelectItem key={muscleGroup} value={muscleGroup}>
                        {muscleGroup}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                {errors.muscleGroup && <p className="text-sm text-red-500">{errors.muscleGroup}</p>}
              </div>

              {/* Action Buttons */}
              <div className="flex gap-3 pt-4">
                <Button type="submit" disabled={saving} className="bg-blue-600 hover:bg-blue-700">
                  {saving ? (
                    <>
                      <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                      Updating...
                    </>
                  ) : (
                    <>
                      <Save className="h-4 w-4 mr-2" />
                      Update Exercise
                    </>
                  )}
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => router.push("/admin/exercises")}
                  disabled={saving}
                >
                  Cancel
                </Button>
              </div>
            </div>

            {/* Preview Section */}
            <div className="space-y-6">
              <Card>
                <CardHeader>
                  <CardTitle>Exercise Preview</CardTitle>
                  <CardDescription>Preview of the updated exercise</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <h3 className="font-semibold text-lg">{formData.name || "Exercise Name"}</h3>
                    <div className="flex gap-2 mt-2">
                      <Badge variant="outline">{formData.difficulty || "Difficulty"}</Badge>
                    </div>
                  </div>

                  <div>
                    <p className="text-sm text-gray-600">
                      {formData.description || "Exercise description will appear here..."}
                    </p>
                  </div>

                  <div>
                    <p className="text-sm font-medium">Equipment:</p>
                    <p className="text-sm text-gray-600">{formData.equipment || "Not specified"}</p>
                  </div>

                  <div>
                    <p className="text-sm font-medium">Muscle Group:</p>
                    <div className="flex flex-wrap gap-1 mt-1">
                      {formData.muscleGroup ? (
                        <Badge variant="secondary" className="text-xs">
                          {formData.muscleGroup}
                        </Badge>
                      ) : (
                        <p className="text-sm text-gray-400">No muscle group selected</p>
                      )}
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </form>
      </div>
    </div>
  )
}