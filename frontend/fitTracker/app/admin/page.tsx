"use client";

import { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Users,
  Dumbbell,
  Apple,
  Target,
  Settings,
  BarChart3,
  Shield,
  Database,
} from "lucide-react";
import Link from "next/link";
import { DashboardLayout } from "@/components/dashboard-layout";
import axios from "axios";

export default function AdminDashboardPage() {
  const [stats, setStats] = useState({
    totalUsers: 0,
    activeUsers: 0,
    totalExercises: 0,
    totalMeals: 0,
    activeGoals: 0,
    templates: 24,
    notifications: 156,
    systemHealth: "Excellent",
  });

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        const token = localStorage.getItem("token") || "";

        const userRes = await axios.get(
          "http://localhost:8000/auth/api/v1/user",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        );

        const exerciseRes = await fetch(
          "http://localhost:8000/workout-service/api/v1/exercise-details",
          {
            headers: {
              "Content-Type": "application/json",
              "X-Handle": user.handle || "",
              "X-Role": user.role || "",
            },
          },
        );

        const exercises = await exerciseRes.json();

        const mealRes = await fetch(
          "http://localhost:8000/nutrition-service/api/v1/meal",
          {
            headers: {
              "Content-Type": "application/json",
              "X-Handle": user.handle || "",
              "X-Role": user.role || "",
            },
          },
        );

        const meals = await mealRes.json();

        const goalRes = await fetch(
          "http://localhost:8000/notification-service/api/v1/goal",
          {
            headers: {
              "Content-Type": "application/json",
              "X-Handle": user.handle || "",
              "X-Role": user.role || "",
            },
          },
        );

        const goals = await goalRes.json();

        setStats((prev) => ({
          ...prev,
          totalUsers: userRes.data.length,
          activeUsers: userRes.data.filter((u: any) => u.active).length, // assuming 'active' flag
          totalExercises: exercises.length || 0,
          totalMeals: meals.length || 0,
          activeGoals: goals.length || 0,
        }));
      } catch (error) {
        console.error("Failed to fetch stats", error);
      }
    };

    fetchStats();
  }, []);

  const quickActions = [
    {
      title: "User Management",
      description: "Manage user accounts, roles, and permissions",
      icon: Users,
      href: "/admin/users",
      color: "text-fitness-electric",
      bgColor: "bg-fitness-electric/20",
    },
    {
      title: "Exercise Library",
      description: "Add, edit, and organize exercise database",
      icon: Dumbbell,
      href: "/admin/exercises",
      color: "text-fitness-success",
      bgColor: "bg-fitness-success/20",
    },
    {
      title: "Meal Database",
      description: "Manage nutrition database and food items",
      icon: Apple,
      href: "/admin/foods",
      color: "text-fitness-energy",
      bgColor: "bg-fitness-energy/20",
    },
  ];

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-4xl font-bold">
              <span className="fitness-text-gradient">Admin Dashboard</span>
            </h1>
            <p className="text-slate-400">
              Manage your FitTracker Pro application
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Badge className="bg-fitness-success/20 text-fitness-success">
              <Shield className="h-3 w-3 mr-1" />
              Admin Access
            </Badge>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card className="fitness-card border-fitness-electric/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Total Users
              </CardTitle>
              <Users className="h-4 w-4 text-fitness-electric" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-electric">
                {stats.totalUsers}
              </div>
              <p className="text-xs text-slate-400">users registered</p>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-success/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Total Exercises
              </CardTitle>
              <Dumbbell className="h-4 w-4 text-fitness-success" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-success">
                {stats.totalExercises}
              </div>
              <p className="text-xs text-slate-400">exercises available</p>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-energy/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Nutrition Entries
              </CardTitle>
              <Apple className="h-4 w-4 text-fitness-energy" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-energy">
                {stats.totalMeals}
              </div>
              <p className="text-xs text-slate-400">Meals logged by users</p>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-warning/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Total Goals
              </CardTitle>
              <Target className="h-4 w-4 text-fitness-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-warning">
                {stats.activeGoals}
              </div>
              <p className="text-xs text-slate-400">User goals being tracked</p>
            </CardContent>
          </Card>
        </div>

        <div>
          <h2 className="text-2xl font-bold text-white mb-4">Quick Actions</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 gap-6">
            {quickActions.map((action) => {
              const Icon = action.icon;
              return (
                <Card
                  key={action.title}
                  className="fitness-card hover:border-fitness-electric/30 transition-all duration-300 cursor-pointer group"
                >
                  <CardHeader>
                    <div className="flex items-center gap-3">
                      <div className={`p-2 rounded-lg ${action.bgColor}`}>
                        <Icon className={`h-6 w-6 ${action.color}`} />
                      </div>
                      <div>
                        <CardTitle className="text-white group-hover:text-fitness-electric transition-colors">
                          {action.title}
                        </CardTitle>
                        <CardDescription className="text-slate-400">
                          {action.description}
                        </CardDescription>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <Button
                      variant="outline"
                      className="w-full border-slate-600 text-slate-300 hover:bg-slate-800"
                      asChild
                    >
                      <Link href={action.href}>Manage</Link>
                    </Button>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}
