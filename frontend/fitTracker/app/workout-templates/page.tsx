"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dumbbell, Target, ArrowLeft, Play, Search, Plus, Edit } from "lucide-react"
import Link from "next/link"
import { DashboardLayout } from "@/components/dashboard-layout"
import { useRouter } from "next/navigation"
import { useEffect, useState } from "react"
import { toast } from "sonner" // Import toast for notifications

// --- Updated Interfaces based on the new API response structure ---

interface ExerciseDetails {
  id: number;
  uuid: string;
  name: string;
  description: string;
}

interface ExerciseTemplate {
  id: number;
  uuid: string;
  exerciseDetails: ExerciseDetails;
  // If your API provides sets, reps, weight for templates, add them here
  // e.g., sets?: number; reps?: number; weight?: number;
}

interface WorkoutTemplate {
  id: number; // Added 'id' as per the response body
  uuid: string;
  name: string;
  description: string;
  exerciseTemplates: ExerciseTemplate[]; // Changed from 'exercises' to 'exerciseTemplates'
  category: string; // Assuming this is still top-level, or derived
  estimatedDuration: string; // Assuming this is still top-level, or derived
  createdDate: string; // Assuming this is still top-level, or derived
  usageCount: number; // Assuming this is still top-level, or derived
}

// --- End of Updated Interfaces ---

export default function WorkoutTemplatesPage() {
  const router = useRouter()
  const [searchTerm, setSearchTerm] = useState("")
  const [workoutTemplates, setWorkoutTemplates] = useState<WorkoutTemplate[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const authHeaders = () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle || "",
      "X-Role": user.role || "",
    };
  };

  useEffect(() => {
    const fetchWorkoutTemplates = async () => {
      setLoading(true);
      setError(null);

      const user = JSON.parse(localStorage.getItem("user") || "{}");
      const userHandle = user.handle;

      if (!userHandle) {
        setError("User handle not found. Cannot fetch workout templates.");
        setLoading(false);
        toast.error("Please log in to view workout templates.");
        return;
      }

      try {
        const response = await fetch(
          `http://localhost:8000/workout-service/api/v1/workout-template/user/uuid/${userHandle}`,
          { headers: authHeaders() }
        );

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const data: WorkoutTemplate[] = await response.json(); // Directly type the incoming data

        // No need for a separate mapping step if the API response directly matches
        // the WorkoutTemplate interface. If there are slight mismatches or
        // you need to compute fields like 'category', 'estimatedDuration', 'usageCount',
        // or 'createdDate' from sub-elements or default them, you'd do it here.
        // For now, assuming these top-level fields are directly provided by the API.

        setWorkoutTemplates(data); // Set the data directly as it matches the interface
      } catch (err: any) {
        console.error("Error loading workout templates:", err);
        setError(err.message || "An unknown error occurred while fetching templates.");
        toast.error(`Failed to load templates: ${err.message || "Please try again."}`);
      } finally {
        setLoading(false);
      }
    };

    fetchWorkoutTemplates();
  }, [])

  // Filter templates based on searchTerm
  const filteredTemplates = workoutTemplates.filter(
    (template) =>
      template.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      template.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      // Assuming 'category' is a top-level field as in your mock and previous code
      (template.category && template.category.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const startWorkout = (templateId: number) => {
    router.push(`/workouts/new?fromTemplate=${templateId}`)
  }

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
            <h1 className="text-4xl font-bold">
              <span className="fitness-text-gradient">Workout Templates</span>
            </h1>
            <p className="text-slate-400">Manage your custom workout routines and templates</p>
          </div>
          <Button asChild className="fitness-button-primary">
            <Link href="/workout-templates/new">
              <Plus className="h-4 w-4 mr-2" />
              New Template
            </Link>
          </Button>
        </div>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
          <Input
            placeholder="Search templates..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10 bg-slate-800/50 border-slate-700 text-white"
          />
        </div>

        {/* Loading, Error, or Templates Grid */}
        {loading ? (
          <div className="text-center py-12">
            <p className="text-slate-400">Loading workout templates...</p>
          </div>
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-red-500">Error: {error}</p>
            <p className="text-slate-400 mt-2">Please try reloading the page or logging in again.</p>
          </div>
        ) : filteredTemplates.length === 0 ? (
          <div className="text-center py-12">
            <Target className="h-12 w-12 text-slate-600 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-slate-300 mb-2">
              {searchTerm ? "No templates found" : "No templates created yet"}
            </h3>
            <p className="text-slate-400 mb-4">
              {searchTerm ? "Try adjusting your search terms" : "Create your first workout template to get started!"}
            </p>
            {!searchTerm && (
              <Button asChild className="fitness-button-primary">
                <Link href="/workout-templates/new">
                  <Plus className="h-4 w-4 mr-2" />
                  Create Template
                </Link>
              </Button>
            )}
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {filteredTemplates.map((template) => (
              <Card
                key={template.id}
                className="fitness-card border-slate-700/50 hover:border-fitness-electric/30 transition-all duration-300"
              >
                <CardHeader>
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <CardTitle className="text-white text-xl mb-2">{template.name}</CardTitle>
                      <CardDescription className="text-slate-300 mb-3">{template.description}</CardDescription>
                      <div className="flex items-center gap-4 text-sm text-slate-400 mb-3">
                        <div className="flex items-center gap-1">
                          {/* Use template.exerciseTemplates.length */}
                          <Dumbbell className="h-4 w-4" />
                          {template.exerciseTemplates.length} exercises
                        </div>
                      </div>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {/* Exercise Preview */}
                    <div>
                      <p className="text-sm font-medium text-slate-300 mb-2">Exercises:</p>
                      <div className="grid grid-cols-2 gap-1">
                        {/* Map over exerciseTemplates and access exerciseDetails.name */}
                        {template.exerciseTemplates.slice(0, 4).map((exerciseTemplate, index) => (
                          <div key={index} className="text-xs text-slate-400 truncate">
                            • {exerciseTemplate.exerciseDetails.name}
                          </div>
                        ))}
                        {template.exerciseTemplates.length > 4 && (
                          <div className="text-xs text-slate-500">+{template.exerciseTemplates.length - 4} more...</div>
                        )}
                      </div>
                    </div>

                    {/* Actions */}
                    <div className="flex gap-2 pt-2">
                      <Button className="flex-1 fitness-button-primary" onClick={() => startWorkout(template.id)}>
                        <Play className="h-4 w-4 mr-2" />
                        Start Workout
                      </Button>
                      <Button variant="outline" className="border-slate-600 text-slate-300 hover:bg-slate-800" asChild>
                        <Link href={`/workout-templates/${template.id}`}>
                          <Edit className="h-4 w-4 mr-2" />
                          View/Edit
                        </Link>
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </DashboardLayout>
  )
}