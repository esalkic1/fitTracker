"use client"

import type React from "react"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { ArrowLeft } from "lucide-react"
import { toast } from "sonner"

interface ExerciseFormData {
  name: string
  description: string
  muscleGroup: string
  equipment: string
  difficulty: string // Note: This will be mapped to difficultyLevel in the API body
}

export default function NewExercisePage() {
  const router = useRouter()
  const [isLoading, setIsLoading] = useState(false)

  const [formData, setFormData] = useState<ExerciseFormData>({
    name: "",
    description: "",
    muscleGroup: "",
    equipment: "",
    difficulty: ""
  })

  const authHeaders = () => {
  const user = JSON.parse(localStorage.getItem("user")!)
  return {
    "Content-Type": "application/json",
    "X-Handle": user.handle,
    "X-Role": user.role,
  }
}

  const [errors, setErrors] = useState<Partial<ExerciseFormData>>({})

  const equipmentOptions = [
    "None (Bodyweight)",
    "Dumbbells",
    "Barbell",
    "Resistance Bands",
    "Kettlebell",
    "Cable Machine",
    "Pull-up Bar",
    "Medicine Ball",
    "Stability Ball",
    "Other",
  ]

  const difficultyLevels = ["Beginner", "Intermediate", "Advanced", "Expert"]

  const commonMuscleGroups = [
    "Chest",
    "Back",
    "Shoulders",
    "Arms",
    "Biceps",
    "Triceps",
    "Core",
    "Abs",
    "Legs",
    "Quadriceps",
    "Hamstrings",
    "Glutes",
    "Calves",
    "Forearms",
    "Traps",
    "Lats",
  ]

  const validateForm = (): boolean => {
    const newErrors: Partial<ExerciseFormData> = {}

    if (!formData.name.trim()) {
      newErrors.name = "Exercise name is required"
    }

    if (!formData.description.trim()) {
      newErrors.description = "Description is required"
    }

    if (!formData.muscleGroup) {
      newErrors.muscleGroup = "Muscle group selection is required"
    }

    if (!formData.equipment) {
      newErrors.equipment = "Equipment selection is required"
    }

    if (!formData.difficulty) {
      newErrors.difficulty = "Difficulty level is required"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) {
      toast.error("Please fix the form errors before submitting")
      return
    }

    setIsLoading(true)

    // Define your API endpoint
    const apiUrl = "http://localhost:8000/workout-service/api/v1/exercise-details";

    // Construct the request body, mapping 'difficulty' to 'difficultyLevel'
    const requestBody = {
      name: formData.name,
      description: formData.description,
      muscleGroup: formData.muscleGroup,
      equipment: formData.equipment,
      difficultyLevel: formData.difficulty, // Key changed here as per API expectation
    };

    try {
      const response = await fetch(apiUrl, {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        // Attempt to read the error message from the response body
        const errorData = await response.json();
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      console.log("Exercise created successfully:", result);
      toast.success("Exercise created successfully!");
      router.push("/admin/exercises"); // Redirect on success
    } catch (error) {
      console.error("Failed to create exercise:", error);
      toast.error(`Failed to create exercise: ${(error as Error).message || "An unknown error occurred"}`);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <div className="container mx-auto py-6 max-w-4xl">
      <div className="flex items-center gap-4 mb-6">
        <Button variant="ghost" size="sm" onClick={() => router.back()} className="flex items-center gap-2">
          <ArrowLeft className="h-4 w-4" />
          Back
        </Button>
        <div>
          <h1 className="text-2xl font-bold">Create New Exercise</h1>
          <p className="text-muted-foreground">Add a new exercise to the database</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Exercise Details</CardTitle>
              <CardDescription>Fill in the information for the new exercise</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="name">Exercise Name *</Label>
                    <Input
                      id="name"
                      value={formData.name}
                      onChange={(e) => setFormData((prev) => ({ ...prev, name: e.target.value }))}
                      placeholder="e.g., Push-ups"
                      className={errors.name ? "border-red-500" : ""}
                    />
                    {errors.name && <p className="text-sm text-red-500">{errors.name}</p>}
                  </div>
                </div>

                <div className="space-y-2">
                  <Label htmlFor="description">Description *</Label>
                  <Textarea
                    id="description"
                    value={formData.description}
                    onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                    placeholder="Brief description of the exercise..."
                    rows={3}
                    className={errors.description ? "border-red-500" : ""}
                  />
                  {errors.description && <p className="text-sm text-red-500">{errors.description}</p>}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
                </div>

                <div className="space-y-2">
                  <Label htmlFor="muscleGroup">Muscle Group *</Label>
                  <Select
                    value={formData.muscleGroup}
                    onValueChange={(value) => setFormData((prev) => ({ ...prev, muscleGroup: value }))}
                  >
                    <SelectTrigger className={errors.muscleGroup ? "border-red-500" : ""}>
                      <SelectValue placeholder="Select muscle group" />
                    </SelectTrigger>
                    <SelectContent>
                      {commonMuscleGroups.map((group) => (
                        <SelectItem key={group} value={group}>
                          {group}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {errors.muscleGroup && <p className="text-sm text-red-500">{errors.muscleGroup}</p>}
                </div>

                <div className="flex gap-3 pt-4">
                  <Button type="submit" disabled={isLoading} className="bg-green-600 hover:bg-green-700">
                    {isLoading ? "Creating..." : "Create Exercise"}
                  </Button>
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => router.push("/admin/exercises")}
                    disabled={isLoading}
                  >
                    Cancel
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-lg">Exercise Preview</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div>
                <Label className="text-xs text-muted-foreground">NAME</Label>
                <p className="font-medium">{formData.name || "Exercise Name"}</p>
              </div>

              <div>
                <Label className="text-xs text-muted-foreground">DIFFICULTY</Label>
                <p className="text-sm">{formData.difficulty || "Not selected"}</p>
              </div>

              <div>
                <Label className="text-xs text-muted-foreground">EQUIPMENT</Label>
                <p className="text-sm">{formData.equipment || "Not selected"}</p>
              </div>

              <div>
                <Label className="text-xs text-muted-foreground">MUSCLE GROUP</Label>
                <div className="flex flex-wrap gap-1 mt-1">
                  {formData.muscleGroup ? (
                    <Badge variant="outline" className="text-xs">
                      {formData.muscleGroup}
                    </Badge>
                  ) : (
                    <p className="text-sm text-muted-foreground">None selected</p>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}