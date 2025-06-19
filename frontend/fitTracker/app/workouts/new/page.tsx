"use client"

import { useState, useEffect } from "react"
import { useSearchParams, useRouter } from "next/navigation"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Plus, Trash2, Save } from "lucide-react"
import { DashboardLayout } from "@/components/dashboard-layout"

interface Exercise {
  id: string
  exerciseDetailsId: string
  name: string
  muscleGroup: string
  sets: number
  reps: number
  weight: number
  notes: string
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

export default function NewWorkoutPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const fromTemplateId = searchParams.get("fromTemplate")
  const fromWorkout = searchParams.get("fromWorkout");

  const [workoutName, setWorkoutName] = useState("")
  const [exercises, setExercises] = useState<Exercise[]>([])
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
  const fetchInitialData = async () => {
    try {
      setLoading(prev => ({ ...prev, exerciseDetails: true }));
      setError(null);

      // Fetch exercise details
      const detailsRes = await fetch("http://localhost:8000/workout-service/api/v1/exercise-details", {
        headers: authHeaders(),
      });
      const detailsData = await detailsRes.json();
      setExerciseDetails(detailsData);

      // If fromTemplateId is present, fetch template
      if (fromTemplateId) {
        const templateRes = await fetch(
          `http://localhost:8000/workout-service/api/v1/workout-template/${fromTemplateId}`,
          { headers: authHeaders() }
        );
        const templateData = await templateRes.json();
        setWorkoutName(templateData.name || "");

        const prefilledExercises: Exercise[] = templateData.exerciseTemplates.map((template: any) => ({
          id: Date.now().toString() + Math.random(),
          exerciseDetailsId: template.exerciseDetails.id.toString(),
          name: template.exerciseDetails.name,
          muscleGroup: template.exerciseDetails.muscleGroup,
          sets: 3,
          reps: 10,
          weight: 10,
          notes: ""
        }));
        setExercises(prefilledExercises);
      }

      // If fromWorkout is present, fetch workout
      else if (fromWorkout) {
        const workoutRes = await fetch(
          `http://localhost:8000/workout-service/api/v1/workout/${fromWorkout}`,
          { headers: authHeaders() }
        );
        const workoutData = await workoutRes.json();
        setWorkoutName(workoutData.name || "");

        const prefilledExercises: Exercise[] = workoutData.exercises.map((ex: any) => ({
          id: Date.now().toString() + Math.random(),
          exerciseDetailsId: ex.exerciseDetails.id.toString(),
          name: ex.exerciseDetails.name,
          muscleGroup: ex.exerciseDetails.muscleGroup,
          sets: ex.sets,
          reps: ex.reps,
          weight: ex.weight,
          notes: ""
        }));
        setExercises(prefilledExercises);
      }
    } catch (err: any) {
      console.error("Error fetching data:", err);
      setError(err.message);
    } finally {
      setLoading(prev => ({ ...prev, exerciseDetails: false }));
    }
  };

  fetchInitialData();
}, [fromTemplateId, fromWorkout]);


  const addExercise = () => {
    const newExercise: Exercise = {
      id: Date.now().toString(),
      exerciseDetailsId: "",
      name: "",
      muscleGroup: "",
      sets: 3,
      reps: 10,
      weight: 10,
      notes: "",
    }
    setExercises([...exercises, newExercise])
  }

  const removeExercise = (id: string) => {
    setExercises(exercises.filter((ex) => ex.id !== id))
  }

  const updateExercise = (id: string, field: keyof Exercise, value: any) => {
  setExercises(
    exercises.map((ex) => {
      if (ex.id === id) {
        if (field === "exerciseDetailsId") {
          // Parse value to integer for comparison with detail.id
          const selectedDetail = exerciseDetails.find((detail) => detail.id === parseInt(value, 10));
          return {
            ...ex,
            [field]: value, // Keep value as string for exerciseDetailsId
            name: selectedDetail?.name || "",
            muscleGroup: selectedDetail?.muscleGroup || "",
          };
        }
        return { ...ex, [field]: value };
      }
      return ex;
    }),
  );
};

  const saveWorkout = async () => {
  if (!workoutName.trim() || exercises.length === 0) {
    alert("Please add a workout name and at least one exercise")
    return
  }

  try {
    setLoading(prev => ({...prev, saving: true}))
    
    // Get current user
    const user = JSON.parse(localStorage.getItem("user") || "{}")
    
    // Prepare the workout data for API
    const requestBody = {
      workout: {
        name: workoutName,
        date: new Date().toISOString(), // Current date/time
        userHandle: user.handle
      },
      exercises: exercises.map(exercise => ({
        exerciseDetailsId: parseInt(exercise.exerciseDetailsId),
        sets: exercise.sets,
        reps: exercise.reps,
        weight: exercise.weight,
        //notes: exercise.notes || undefined // Send undefined if empty
      }))
    }

    // Make API call to save workout
    const response = await fetch(
      "http://localhost:8000/workout-service/api/v1/workout/with-exercises",
      {
        method: "POST",
        headers: authHeaders(),
        body: JSON.stringify(requestBody)
      }
    )

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.message || `Failed to save workout: ${response.status}`)
    }

    // Redirect to workouts page on success
    router.push("/workouts")
  } catch (error) {
    console.error("Error saving workout:", error)
    alert(`Failed to save workout: ${error.message}`)
  } finally {
    setLoading(prev => ({...prev, saving: false}))
  }
}

  if (loading.exerciseDetails) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center h-64">
          <p className="text-gray-500">Loading exercise details...</p>
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

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold">New Workout</h1>
            <p className="text-gray-600">Create a new workout session</p>
          </div>
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => router.back()}>
              Cancel
            </Button>
            <Button onClick={saveWorkout} disabled={loading.saving}>
              {loading.saving ? (
                "Saving..."
              ) : (
                <>
                  <Save className="h-4 w-4 mr-2" />
                  Save Workout
                </>
              )}
            </Button>
          </div>
        </div>

        {/* Workout Details */}
        <Card>
          <CardHeader>
            <CardTitle>Workout Details</CardTitle>
            <CardDescription>Basic information about your workout</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label htmlFor="workoutName">Workout Name</Label>
              <Input
                id="workoutName"
                placeholder="e.g., Push Day, Leg Day, Full Body"
                value={workoutName}
                onChange={(e) => setWorkoutName(e.target.value)}
              />
            </div>
          </CardContent>
        </Card>

        {/* Exercises */}
        <Card>
          <CardHeader>
            <div className="flex justify-between items-center">
              <div>
                <CardTitle>Exercises</CardTitle>
                <CardDescription>Add exercises to your workout</CardDescription>
              </div>
              <Button onClick={addExercise}>
                <Plus className="h-4 w-4 mr-2" />
                Add Exercise
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            {exercises.length === 0 ? (
              <div className="text-center py-8 text-gray-500">
                No exercises added yet. Click "Add Exercise" to get started.
              </div>
            ) : (
              <div className="space-y-4">
                {exercises.map((exercise, index) => (
                  <div key={exercise.id} className="p-4 border rounded-lg space-y-4">
                    <div className="flex justify-between items-center">
                      <h4 className="font-medium">Exercise {index + 1}</h4>
                      <Button variant="ghost" size="sm" onClick={() => removeExercise(exercise.id)}>
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <Label>Exercise</Label>
                        <Select
                          value={exercise.exerciseDetailsId}
                          onValueChange={(value) => updateExercise(exercise.id, "exerciseDetailsId", value)}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Select an exercise" />
                          </SelectTrigger>
                          <SelectContent>
                            {exerciseDetails.map((detail) => (
                              <SelectItem key={detail.id} value={detail.id.toString()}>
                                {detail.name} ({detail.muscleGroup})
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>

                      <div>
                        <Label>Muscle Group</Label>
                        <Input value={exercise.muscleGroup} disabled />
                      </div>
                    </div>

                    <div className="grid grid-cols-3 gap-4">
                      <div>
                        <Label>Sets</Label>
                        <Input
                          type="number"
                          value={exercise.sets}
                          onChange={(e) => updateExercise(exercise.id, "sets", Number.parseInt(e.target.value) || 0)}
                          min="1"
                        />
                      </div>

                      <div>
                        <Label>Reps</Label>
                        <Input
                          type="number"
                          value={exercise.reps}
                          onChange={(e) => updateExercise(exercise.id, "reps", Number.parseInt(e.target.value) || 0)}
                          min="1"
                        />
                      </div>

                      <div>
                        <Label>Weight (kg)</Label>
                        <Input
                          type="number"
                          value={exercise.weight}
                          onChange={(e) =>
                            updateExercise(exercise.id, "weight", Number.parseFloat(e.target.value) || 0)
                          }
                          min="0"
                          step="0.5"
                        />
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