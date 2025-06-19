"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Plus, Search, Calendar, Clock, Dumbbell } from "lucide-react"
import Link from "next/link"
import { DashboardLayout } from "@/components/dashboard-layout"

export default function WorkoutsPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [workoutTemplates, setWorkoutTemplates] = useState([])
  const [workouts, setWorkouts] = useState([])
  const [loading, setLoading] = useState({
    templates: false,
    workouts: false
  })
  const [error, setError] = useState({
    templates: null,
    workouts: null
  })
  const user = JSON.parse(localStorage.getItem("user")!)

  const authHeaders = () => {
    const user = JSON.parse(localStorage.getItem("user")!)
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle,
      "X-Role": user.role,
    }
  }

  useEffect(() => {
    const fetchWorkoutTemplates = async () => {
      setLoading(prev => ({...prev, templates: true}))
      setError(prev => ({...prev, templates: null}))
      try {
        const response = await fetch(
          `http://localhost:8000/workout-service/api/v1/workout-template/user/uuid/${user.handle}`, 
          { headers: authHeaders() }
        )
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data = await response.json()
        setWorkoutTemplates(data)
      } catch (error) {
        setError(prev => ({...prev, templates: error.message}))
      } finally {
        setLoading(prev => ({...prev, templates: false}))
      }
    }

    const fetchWorkouts = async () => {
      setLoading(prev => ({...prev, workouts: true}))
      setError(prev => ({...prev, workouts: null}))
      try {
        const response = await fetch(
          `http://localhost:8000/workout-service/api/v1/workout/by-user-uuid/${user.handle}`,
          { headers: authHeaders() }
        )
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data = await response.json()
        setWorkouts(data)
      } catch (error) {
        setError(prev => ({...prev, workouts: error.message}))
      } finally {
        setLoading(prev => ({...prev, workouts: false}))
      }
    }

    fetchWorkoutTemplates()
    fetchWorkouts()
  }, [user.handle])

  const filteredWorkouts = workouts.filter((workout) => 
    workout.name.toLowerCase().includes(searchTerm.toLowerCase())
  )

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold">Workouts</h1>
            <p className="text-gray-600">Track your training sessions and progress</p>
          </div>
          <div className="flex gap-2">
            <Button asChild>
              <Link href="/workouts/new">
                <Plus className="h-4 w-4 mr-2" />
                New Workout
              </Link>
            </Button>
            <Button
              variant="outline"
              className="border-fitness-electric text-fitness-electric hover:bg-fitness-electric hover:text-fitness-dark"
              asChild
            >
              <Link href="/workout-templates">Templates</Link>
            </Button>
          </div>
        </div>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            placeholder="Search workouts..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>

        {/* Workout Templates */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Workout Templates</h2>
          {loading.templates && <p className="text-gray-500">Loading templates...</p>}
          {error.templates && <p className="text-red-500">Error loading templates: {error.templates}</p>}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {workoutTemplates.map((template) => (
              <Card key={template.id} className="cursor-pointer hover:shadow-md transition-shadow">
                <CardHeader>
                  <CardTitle className="text-lg">{template.name}</CardTitle>
                  <CardDescription>{template.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="flex items-center justify-between">
                    <Badge variant="secondary">{template.exerciseTemplates?.length || 0} exercises</Badge>
                    <Button size="sm" asChild>
                      <Link href={`/workout-templates/${template.id}`}>Use Template</Link>
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        {/* Recent Workouts */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Recent Workouts</h2>
          {loading.workouts && <p className="text-gray-500">Loading workouts...</p>}
          {error.workouts && <p className="text-red-500">Error loading workouts: {error.workouts}</p>}
          
          {!loading.workouts && !error.workouts && (
            <div className="space-y-4">
              {filteredWorkouts.length > 0 ? (
                filteredWorkouts.map((workout) => (
                  <Card key={workout.id}>
                    <CardHeader>
                      <div className="flex justify-between items-start">
                        <div>
                          <CardTitle className="text-lg">{workout.name}</CardTitle>
                          <div className="flex items-center gap-4 text-sm text-gray-600 mt-2">
                            <div className="flex items-center gap-1">
                              <Calendar className="h-4 w-4" />
                              {new Date(workout.date).toLocaleDateString()}
                            </div>
                            <div className="flex items-center gap-1">
                              <Dumbbell className="h-4 w-4" />
                              {workout.exercises?.length || 0} exercises
                            </div>
                          </div>
                        </div>
                        <Button variant="outline" size="sm" asChild>
                          <Link href={`/workouts/${workout.id}`}>View Details</Link>
                        </Button>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                        {workout.exercises?.map((exercise, index) => (
                          <div
                            key={index}
                            className="p-4 bg-slate-800/30 border border-slate-700/50 rounded-lg hover:border-fitness-electric/30 transition-all duration-200"
                          >
                            <div className="flex items-center gap-2 mb-2">
                              <div className="h-2 w-2 rounded-full bg-fitness-electric"></div>
                              <p className="font-medium text-sm text-white truncate">{exercise.exerciseDetails.name}</p>
                            </div>
                            <div className="grid grid-cols-3 gap-2 text-xs">
                              <div className="text-center">
                                <p className="text-slate-400">Sets</p>
                                <p className="font-bold text-fitness-success">{exercise.sets}</p>
                              </div>
                              <div className="text-center">
                                <p className="text-slate-400">Reps</p>
                                <p className="font-bold text-fitness-warning">{exercise.reps}</p>
                              </div>
                              <div className="text-center">
                                <p className="text-slate-400">Weight</p>
                                <p className="font-bold text-fitness-energy">
                                  {exercise.weight > 0 ? `${exercise.weight}kg` : "BW"}
                                </p>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </CardContent>
                  </Card>
                ))
              ) : (
                <p className="text-gray-500">No workouts found</p>
              )}
            </div>
          )}
        </div>
      </div>
    </DashboardLayout>
  )
}