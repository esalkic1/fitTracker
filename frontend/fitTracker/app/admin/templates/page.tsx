"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Search, Plus, Edit, Trash2, Target, ArrowLeft, Eye, Users } from "lucide-react"
import Link from "next/link"
import { DashboardLayout } from "@/components/dashboard-layout"

export default function AdminTemplatesPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [categoryFilter, setCategoryFilter] = useState("all")
  const [statusFilter, setStatusFilter] = useState("all")

  // Mock workout templates data
  const templates = [
    {
      id: 1,
      uuid: "template-uuid-001",
      name: "Push Day Power",
      description: "Focus on chest, shoulders, and triceps with compound movements",
      category: "Strength",
      difficulty: "Intermediate",
      exercises: 6,
      estimatedDuration: "45-60 min",
      createdBy: "Admin",
      createdDate: "2024-01-10",
      usageCount: 234,
      isPublic: true,
      isVerified: true,
      status: "active",
    },
    {
      id: 2,
      uuid: "template-uuid-002",
      name: "Pull Day Strength",
      description: "Complete back and biceps workout for building pulling strength",
      category: "Strength",
      difficulty: "Advanced",
      exercises: 5,
      estimatedDuration: "50-65 min",
      createdBy: "John Doe",
      createdDate: "2024-01-08",
      usageCount: 156,
      isPublic: true,
      isVerified: true,
      status: "active",
    },
    {
      id: 3,
      uuid: "template-uuid-003",
      name: "Leg Day Beast",
      description: "Comprehensive lower body workout targeting all major muscle groups",
      category: "Strength",
      difficulty: "Intermediate",
      exercises: 7,
      estimatedDuration: "60-75 min",
      createdBy: "Jane Smith",
      createdDate: "2024-01-05",
      usageCount: 189,
      isPublic: false,
      isVerified: false,
      status: "pending",
    },
    {
      id: 4,
      uuid: "template-uuid-004",
      name: "Full Body HIIT",
      description: "High-intensity interval training for maximum calorie burn",
      category: "HIIT",
      difficulty: "Beginner",
      exercises: 6,
      estimatedDuration: "25-35 min",
      createdBy: "Mike Wilson",
      createdDate: "2024-01-12",
      usageCount: 312,
      isPublic: true,
      isVerified: true,
      status: "active",
    },
    {
      id: 5,
      uuid: "template-uuid-005",
      name: "Cardio Blast",
      description: "High-energy cardio workout for endurance building",
      category: "Cardio",
      difficulty: "Intermediate",
      exercises: 8,
      estimatedDuration: "30-40 min",
      createdBy: "Sarah Jones",
      createdDate: "2024-01-15",
      usageCount: 87,
      isPublic: true,
      isVerified: false,
      status: "pending",
    },
  ]

  const categories = ["all", "Strength", "HIIT", "Cardio", "Flexibility", "Powerlifting", "Bodybuilding"]
  const statuses = ["all", "active", "pending", "inactive"]

  const filteredTemplates = templates.filter((template) => {
    const matchesSearch =
      template.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      template.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
      template.createdBy.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesCategory = categoryFilter === "all" || template.category === categoryFilter
    const matchesStatus = statusFilter === "all" || template.status === statusFilter

    return matchesSearch && matchesCategory && matchesStatus
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

  const getCategoryColor = (category: string) => {
    const colors = {
      Strength: "bg-fitness-electric/20 text-fitness-electric",
      HIIT: "bg-fitness-energy/20 text-fitness-energy",
      Cardio: "bg-fitness-success/20 text-fitness-success",
      Flexibility: "bg-purple-500/20 text-purple-400",
      Powerlifting: "bg-red-500/20 text-red-400",
      Bodybuilding: "bg-pink-500/20 text-pink-400",
    }
    return colors[category as keyof typeof colors] || "bg-slate-600/20 text-slate-400"
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case "active":
        return "bg-fitness-success/20 text-fitness-success"
      case "pending":
        return "bg-fitness-warning/20 text-fitness-warning"
      case "inactive":
        return "bg-slate-600/20 text-slate-400"
      default:
        return "bg-slate-600/20 text-slate-400"
    }
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
              <span className="fitness-text-gradient">Workout Templates</span>
            </h1>
            <p className="text-slate-400">Create and manage workout templates</p>
          </div>
          <Button className="fitness-button-primary" asChild>
            <Link href="/workout-templates/new">
              <Plus className="h-4 w-4 mr-2" />
              Create Template
            </Link>
          </Button>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <Card className="fitness-card border-fitness-electric/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">Total Templates</CardTitle>
              <Target className="h-4 w-4 text-fitness-electric" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-electric">{templates.length}</div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-success/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">Active Templates</CardTitle>
              <Target className="h-4 w-4 text-fitness-success" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-success">
                {templates.filter((t) => t.status === "active").length}
              </div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-warning/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">Pending Review</CardTitle>
              <Target className="h-4 w-4 text-fitness-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-warning">
                {templates.filter((t) => t.status === "pending").length}
              </div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-energy/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">Total Usage</CardTitle>
              <Users className="h-4 w-4 text-fitness-energy" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-energy">
                {templates.reduce((sum, template) => sum + template.usageCount, 0)}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Filters */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
            <Input
              placeholder="Search templates..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 bg-slate-800/50 border-slate-700 text-white"
            />
          </div>

          <Select value={categoryFilter} onValueChange={setCategoryFilter}>
            <SelectTrigger className="bg-slate-800/50 border-slate-700 text-white">
              <SelectValue placeholder="Filter by category" />
            </SelectTrigger>
            <SelectContent>
              {categories.map((category) => (
                <SelectItem key={category} value={category}>
                  {category === "all" ? "All Categories" : category}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Select value={statusFilter} onValueChange={setStatusFilter}>
            <SelectTrigger className="bg-slate-800/50 border-slate-700 text-white">
              <SelectValue placeholder="Filter by status" />
            </SelectTrigger>
            <SelectContent>
              {statuses.map((status) => (
                <SelectItem key={status} value={status}>
                  {status === "all" ? "All Statuses" : status.charAt(0).toUpperCase() + status.slice(1)}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Templates Table */}
        <Card className="fitness-card">
          <CardHeader>
            <CardTitle className="text-white">Templates ({filteredTemplates.length})</CardTitle>
            <CardDescription className="text-slate-400">Manage all workout templates in the system</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow className="border-slate-700">
                    <TableHead className="text-slate-300">Template</TableHead>
                    <TableHead className="text-slate-300">Category</TableHead>
                    <TableHead className="text-slate-300">Details</TableHead>
                    <TableHead className="text-slate-300">Creator</TableHead>
                    <TableHead className="text-slate-300">Status</TableHead>
                    <TableHead className="text-slate-300">Usage</TableHead>
                    <TableHead className="text-slate-300">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredTemplates.map((template) => (
                    <TableRow key={template.id} className="border-slate-700 hover:bg-slate-800/30">
                      <TableCell>
                        <div className="flex items-center gap-3">
                          <div className="h-8 w-8 rounded-full bg-gradient-to-r from-fitness-electric to-fitness-success flex items-center justify-center">
                            <Target className="h-4 w-4 text-fitness-dark" />
                          </div>
                          <div>
                            <p className="font-medium text-white">{template.name}</p>
                            <p className="text-xs text-slate-400 max-w-xs truncate">{template.description}</p>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="space-y-1">
                          <Badge className={getCategoryColor(template.category)}>{template.category}</Badge>
                          <Badge className={getDifficultyColor(template.difficulty)}>{template.difficulty}</Badge>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="text-sm">
                          <p className="text-slate-300">{template.exercises} exercises</p>
                          <p className="text-slate-400">{template.estimatedDuration}</p>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="text-sm">
                          <p className="text-slate-300">{template.createdBy}</p>
                          <p className="text-slate-400">{template.createdDate}</p>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="space-y-1">
                          <Badge className={getStatusColor(template.status)}>
                            {template.status.charAt(0).toUpperCase() + template.status.slice(1)}
                          </Badge>
                          {template.isPublic && (
                            <Badge variant="outline" className="text-xs border-slate-600 text-slate-400">
                              Public
                            </Badge>
                          )}
                        </div>
                      </TableCell>
                      <TableCell>
                        <p className="text-sm text-slate-300">{template.usageCount} times</p>
                      </TableCell>
                      <TableCell>
                        <div className="flex gap-2">
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-fitness-success hover:bg-fitness-success/20"
                            asChild
                          >
                            <Link href={`/workout-templates/${template.uuid}`}>
                              <Eye className="h-4 w-4" />
                            </Link>
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-fitness-electric hover:bg-fitness-electric/20"
                            asChild
                          >
                            <Link href={`/workout-templates/${template.uuid}/edit`}>
                              <Edit className="h-4 w-4" />
                            </Link>
                          </Button>
                          <Button variant="ghost" size="sm" className="text-red-400 hover:bg-red-500/20">
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  )
}
