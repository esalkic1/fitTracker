"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Search, Plus, Edit, Trash2, Dumbbell, ArrowLeft } from "lucide-react"
import Link from "next/link"
import { DashboardLayout } from "@/components/dashboard-layout"
import { toast } from "sonner" // Import toast for notifications

export default function AdminExercisesPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [muscleGroupFilter, setMuscleGroupFilter] = useState("all")
  const [difficultyFilter, setDifficultyFilter] = useState("all")

  const [exercises, setExercises] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [deletingId, setDeletingId] = useState<number | null>(null); // State to track which exercise is being deleted

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

  const fetchExercises = async () => {
    setLoading(true); // Set loading true before fetching
    try {
      const response = await fetch("http://localhost:8000/workout-service/api/v1/exercise-details", { headers: authHeaders() });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to fetch exercises");
      }

      const data = await response.json();

      const mapped = data.map((e: any) => ({
        id: e.id,
        name: e.name,
        description: e.description,
        muscleGroup: e.muscleGroup,
        equipment: e.equipment,
        difficulty: e.difficultyLevel,
        // usageCount: Math.floor(Math.random() * 1000), // Fake usage count for now
      }));

      setExercises(mapped);
    } catch (err: any) {
      console.error("Error loading exercises:", err);
      toast.error(`Error loading exercises: ${err.message || "An unknown error occurred."}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchExercises();
  }, []);

  // --- New Function for Deleting an Exercise ---
  const onDeleteExercise = async (id: number) => {
    if (!confirm("Are you sure you want to delete this exercise? This action cannot be undone.")) {
      return; // User cancelled the deletion
    }

    setDeletingId(id); // Set the ID of the exercise currently being deleted

    try {
      const response = await fetch(`http://localhost:8000/workout-service/api/v1/exercise-details/${id}`, {
        method: "DELETE",
        headers: authHeaders(),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
      }

      // If successful, update the state to remove the deleted exercise
      setExercises(prevExercises => prevExercises.filter(exercise => exercise.id !== id));
      toast.success("Exercise deleted successfully!");
    } catch (error: any) {
      console.error("Failed to delete exercise:", error);
      toast.error(`Failed to delete exercise: ${error.message || "An unknown error occurred."}`);
    } finally {
      setDeletingId(null); // Reset deleting state
    }
  };
  // ---------------------------------------------


  const muscleGroups = ["all", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core"]
  const difficulties = ["all", "Beginner", "Intermediate", "Advanced"]

  const filteredExercises = exercises.filter((exercise) => {
    const matchesSearch =
      exercise.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      exercise.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      exercise.equipment.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesMuscleGroup = muscleGroupFilter === "all" || exercise.muscleGroup === muscleGroupFilter
    const matchesDifficulty = difficultyFilter === "all" || exercise.difficulty === difficultyFilter

    return matchesSearch && matchesMuscleGroup && matchesDifficulty
  })

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
      Back: "bg-fitness-success/20 text-fitness-success",
      Legs: "bg-fitness-energy/20 text-fitness-energy",
      Shoulders: "bg-fitness-warning/20 text-fitness-warning",
      Arms: "bg-purple-500/20 text-purple-400",
      Core: "bg-pink-500/20 text-pink-400",
    }
    return colors[muscleGroup] || "bg-slate-600/20 text-slate-400"
  }

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Button variant="ghost" size="sm" asChild className="text-slate-400 hover:text-white">
                <Link href="/admin">
                  <ArrowLeft className="h-4 w-4 mr-1" />
                  Back to Admin
                </Link>
              </Button>
            </div>
            <h1 className="text-4xl font-bold">
              <span className="fitness-text-gradient">Exercise Library</span>
            </h1>
            <p className="text-slate-400">Manage the exercise database and workout components</p>
          </div>
          <Button className="fitness-button-primary" asChild>
            <Link href="/admin/exercises/new">
              <Plus className="h-4 w-4 mr-2" />
              Add Exercise
            </Link>
          </Button>
        </div>

        {/* Filters */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
            <Input
              placeholder="Search exercises..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 bg-slate-800/50 border-slate-700 text-white"
            />
          </div>

          <Select value={muscleGroupFilter} onValueChange={setMuscleGroupFilter}>
            <SelectTrigger className="bg-slate-800/50 border-slate-700 text-white">
              <SelectValue placeholder="Muscle Group" />
            </SelectTrigger>
            <SelectContent>
              {muscleGroups.map((group) => (
                <SelectItem key={group} value={group}>
                  {group === "all" ? "All Muscle Groups" : group}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select value={difficultyFilter} onValueChange={setDifficultyFilter}>
            <SelectTrigger className="bg-slate-800/50 border-slate-700 text-white">
              <SelectValue placeholder="Difficulty" />
            </SelectTrigger>
            <SelectContent>
              {difficulties.map((difficulty) => (
                <SelectItem key={difficulty} value={difficulty}>
                  {difficulty === "all" ? "All Difficulties" : difficulty}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Exercises Table */}
        <Card className="fitness-card">
          <CardHeader>
            <CardTitle className="text-white">Exercises ({filteredExercises.length})</CardTitle>
            <CardDescription className="text-slate-400">Manage all exercises in the database</CardDescription>
          </CardHeader>
          <CardContent>
            {loading ? (
              <div className="text-center text-slate-400 py-8">Loading exercises...</div>
            ) : filteredExercises.length === 0 ? (
                <div className="text-center text-slate-400 py-8">No exercises found.</div>
            ) : (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    <TableRow className="border-slate-700">
                      <TableHead className="text-slate-300">Exercise</TableHead>
                      <TableHead className="text-slate-300">Muscle Group</TableHead>
                      <TableHead className="text-slate-300">Equipment</TableHead>
                      <TableHead className="text-slate-300">Difficulty</TableHead>
                      <TableHead className="text-slate-300">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredExercises.map((exercise) => (
                      <TableRow key={exercise.id} className="border-slate-700 hover:bg-slate-800/30">
                        <TableCell>
                          <div className="flex items-center gap-3">
                            <div className="h-8 w-8 rounded-full bg-gradient-to-r from-fitness-electric to-fitness-success flex items-center justify-center">
                              <Dumbbell className="h-4 w-4 text-fitness-dark" />
                            </div>
                            <div>
                              <p className="font-medium text-white">{exercise.name}</p>
                              <p className="text-xs text-slate-400 max-w-xs truncate">{exercise.description}</p>
                            </div>
                          </div>
                        </TableCell>
                        <TableCell>
                          <Badge className={getMuscleGroupColor(exercise.muscleGroup)}>{exercise.muscleGroup}</Badge>
                        </TableCell>
                        <TableCell>
                          <p className="text-sm text-slate-300">{exercise.equipment}</p>
                        </TableCell>
                        <TableCell>
                          <Badge className={getDifficultyColor(exercise.difficulty)}>{exercise.difficulty}</Badge>
                        </TableCell>
                        <TableCell>
                          <div className="flex gap-2">
                            <Button
                              variant="ghost"
                              size="sm"
                              className="text-fitness-electric hover:bg-fitness-electric/20"
                              asChild
                            >
                              <Link href={`/admin/exercises/${exercise.id}/edit`}>
                                <Edit className="h-4 w-4" />
                              </Link>
                            </Button>
                            <Button
                              variant="ghost"
                              size="sm"
                              className="text-red-400 hover:bg-red-500/20"
                              onClick={() => onDeleteExercise(exercise.id)} // Link the trash button
                              disabled={deletingId === exercise.id} // Disable button while deleting
                            >
                              {deletingId === exercise.id ? (
                                <span className="animate-spin mr-1">○</span> // Simple spinner
                              ) : (
                                <Trash2 className="h-4 w-4" />
                              )}
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}