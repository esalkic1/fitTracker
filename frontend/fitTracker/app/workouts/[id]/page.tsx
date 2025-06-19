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
import { Clock, Dumbbell, Target, ArrowLeft, RotateCcw, Trash2, Calendar, StickyNote } from "lucide-react"
import Link from "next/link"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useRouter, useParams } from "next/navigation"
import { toast } from "sonner" // Import toast for notifications

export default function WorkoutDetailsPage() {
  const router = useRouter()
  const params = useParams()
  // Ensure workoutId is a string before passing to API
  const workoutId = typeof params.id === 'string' ? params.id : '';

  const [workout, setWorkout] = useState<any>(null) // Use 'any' or define a proper interface for workout
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null) // Explicitly type error state
  const [isDeleting, setIsDeleting] = useState(false) // New state for delete button loading

  const authHeaders = () => {
    // In a real application, ensure 'user' is safely retrieved and handled,
    // e.g., check for existence and parse securely.
    const user = JSON.parse(localStorage.getItem("user") || "{}"); // Provide a default empty object
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle || "", // Provide default empty string if not found
      "X-Role": user.role || "",     // Provide default empty string if not found
    };
  };

  useEffect(() => {
    if (!workoutId) {
      setError("Workout ID is missing.");
      setLoading(false);
      return;
    }

    const fetchWorkout = async () => {
      try {
        setLoading(true)
        const response = await fetch(
          `http://localhost:8000/workout-service/api/v1/workout/${workoutId}`,
          { headers: authHeaders() }
        )
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
        const data = await response.json()
        setWorkout(data)
      } catch (error: any) { // Catch as 'any' to access .message safely
        setError(error.message || "An unknown error occurred while fetching workout.");
        toast.error(`Failed to load workout: ${error.message || "Please try again."}`);
      } finally {
        setLoading(false)
      }
    }

    fetchWorkout()
  }, [workoutId]) // Depend on workoutId to refetch if it changes

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case "Beginner":
        return "bg-fitness-success/20 text-fitness-success"
      case "Intermediate":
        return "bg-fitness-warning/20 text-fitness-warning"
      case "Advanced":
        return "bg-fitness-energy/20 text-fitness-energy"
      default:
        return "bg-slate-600/20 text-slate-400"
    }
  }

  const getMuscleGroupColor = (muscleGroup: string) => {
    const colors: { [key: string]: string } = { // Explicitly type colors object
      Chest: "bg-fitness-electric/20 text-fitness-electric",
      Shoulders: "bg-fitness-warning/20 text-fitness-warning",
      Triceps: "bg-fitness-energy/20 text-fitness-energy",
      Back: "bg-fitness-success/20 text-fitness-success",
      Legs: "bg-purple-500/20 text-purple-400",
      Core: "bg-pink-500/20 text-pink-400",
    }
    return colors[muscleGroup] || "bg-slate-600/20 text-slate-400"
  }

  const repeatWorkout = () => {
    if (workout) {
      router.push(`/workouts/new?fromWorkout=${workout.id}`)
    }
  }

  // --- Updated deleteWorkout function ---
  const deleteWorkout = async () => {
    setIsDeleting(true); // Set deleting state to true for loading indicator

    try {
      const response = await fetch(
        `http://localhost:8000/workout-service/api/v1/workout/${workoutId}`,
        {
          method: "DELETE",
          headers: authHeaders(),
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      toast.success("Workout deleted successfully!");
      router.push("/workouts"); // Redirect to workouts list after successful deletion
    } catch (error: any) {
      console.error("Failed to delete workout:", error);
      toast.error(`Failed to delete workout: ${error.message || "An unknown error occurred."}`);
    } finally {
      setIsDeleting(false); // Reset deleting state
    }
  };
  // ------------------------------------

  const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return {
      date: date.toLocaleDateString("en-US", {
        weekday: "long",
        year: "numeric",
        month: "long",
        day: "numeric",
      }),
      time: date.toLocaleTimeString("en-US", {
        hour: "2-digit",
        minute: "2-digit",
      }),
    }
  }

  if (loading) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center h-64">
          <p className="text-gray-500">Loading workout details...</p>
        </div>
      </DashboardLayout>
    )
  }

  if (error) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center h-64">
          <p className="text-red-500">Error loading workout: {error}</p>
        </div>
      </DashboardLayout>
    )
  }

  if (!workout) {
    return (
      <DashboardLayout>
        <div className="flex justify-center items-center h-64">
          <p className="text-gray-500">No workout found</p>
        </div>
      </DashboardLayout>
    )
  }

  // Calculate workout statistics
  const totalSets = workout.exercises.reduce((sum: number, exercise: any) => sum + exercise.sets, 0)
  const totalReps = workout.exercises.reduce((sum: number, exercise: any) => sum + exercise.reps * exercise.sets, 0)
  const totalVolume = workout.exercises.reduce(
    (sum: number, exercise: any) => sum + Math.max(0, exercise.weight) * exercise.reps * exercise.sets,
    0,
  )

  const { date, time } = formatDate(workout.date)

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Button variant="ghost" size="sm" asChild className="text-slate-400 hover:text-white">
                <Link href="/workouts">
                  <ArrowLeft className="h-4 w-4 mr-1" />
                  Back to Workouts
                </Link>
              </Button>
            </div>
            <h1 className="text-4xl font-bold text-white">{workout.name}</h1>
            <div className="flex items-center gap-4 text-slate-400 mt-2">
              <div className="flex items-center gap-1">
                <Calendar className="h-4 w-4" />
                {date}
              </div>
              <div className="flex items-center gap-1">
                <Clock className="h-4 w-4" />
                {time}
              </div>
            </div>
          </div>
          <div className="flex gap-2">
            <Button className="fitness-button-primary" onClick={repeatWorkout}>
              <RotateCcw className="h-4 w-4 mr-2" />
              Repeat Workout
            </Button>
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button
                  variant="outline"
                  className="border-red-500 text-red-400 hover:bg-red-500 hover:text-white"
                  disabled={isDeleting} // Disable button while deleting
                >
                  {isDeleting ? (
                    <span className="animate-spin mr-2">○</span> // Simple spinner
                  ) : (
                    <Trash2 className="h-4 w-4 mr-2" />
                  )}
                  Delete
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent className="fitness-card border-slate-700">
                <AlertDialogHeader>
                  <AlertDialogTitle className="text-white">Delete Workout</AlertDialogTitle>
                  <AlertDialogDescription className="text-slate-400">
                    Are you sure you want to delete this workout? This action cannot be undone.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel className="border-slate-600 text-slate-300 hover:bg-slate-800">
                    Cancel
                  </AlertDialogCancel>
                  <AlertDialogAction
                    onClick={deleteWorkout}
                    className="bg-red-500 hover:bg-red-600 text-white"
                    disabled={isDeleting} // Disable action button inside dialog as well
                  >
                    {isDeleting ? (
                      <span className="animate-spin mr-2">○</span>
                    ) : (
                      "Delete Workout"
                    )}
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </div>
        </div>

        {/* Workout Summary */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

          <Card className="fitness-card border-fitness-success/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">Total Sets</CardTitle>
              <Target className="h-4 w-4 text-fitness-success" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-success">{totalSets}</div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-warning/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">Total Reps</CardTitle>
              <Dumbbell className="h-4 w-4 text-fitness-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-warning">{totalReps}</div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-energy/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">Total Volume</CardTitle>
              <Target className="h-4 w-4 text-fitness-energy" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-energy">{totalVolume}kg</div>
            </CardContent>
          </Card>
        </div>

        {/* Exercises */}
        <Card className="fitness-card">
          <CardHeader>
            <CardTitle className="text-white">Exercises ({workout.exercises.length})</CardTitle>
            <CardDescription className="text-slate-400">Complete workout breakdown</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {workout.exercises.map((exercise: any) => ( // Ensure exercise is typed for safety
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
                      <p className="text-lg font-bold text-fitness-electric">{exercise.sets}</p>
                    </div>
                    <div>
                      <p className="text-xs text-slate-500">Reps</p>
                      <p className="text-lg font-bold text-fitness-success">{exercise.reps}</p>
                    </div>
                    <div>
                      <p className="text-xs text-slate-500">Weight</p>
                      <p className="text-lg font-bold text-fitness-warning">
                        {exercise.weight > 0 ? `${exercise.weight}kg` : "Bodyweight"}
                      </p>
                    </div>
                    <div>
                      <p className="text-xs text-slate-500">Equipment</p>
                      <p className="text-sm text-slate-300">{exercise.exerciseDetails.equipment}</p>
                    </div>
                  </div>

                  {/* Notes section would go here if available in API response */}
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}