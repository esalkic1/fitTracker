"use client";

import { useState, useEffect } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import {
  Activity,
  Apple,
  Target,
  TrendingUp,
  Plus,
  Calendar,
  Zap,
} from "lucide-react";
import Link from "next/link";
import { DashboardLayout } from "@/components/dashboard-layout";

interface Workout {
  id: number;
  uuid: string;
  name: string;
  date: string;
  exercises: Array<{
    id: number;
    uuid: string;
    weight: number;
    reps: number;
    sets: number;
    exerciseDetails: {
      id: number;
      uuid: string;
      name: string;
      description: string;
      muscleGroup: string;
      equipment: string;
      difficultyLevel: string;
    };
  }>;
}

type Food = {
  id: string;
  name: string;
  calories: number;
};

type Meal = {
  id: string;
  name: string;
  foods: Food[];
  date: Date;
  totalCalories: number;
};

interface Goal {
  type: string;
  frequency: string;
  target: number;
}

const formatDateTime = (dateTimeString: string) => {
  const date = new Date(dateTimeString);
  return (
    date.toLocaleDateString("en-US", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    }) +
    " at " +
    date.toLocaleTimeString("en-US", {
      hour: "2-digit",
      minute: "2-digit",
    })
  );
};

export default function DashboardPage() {
  const [recentWorkouts, setRecentWorkouts] = useState<Workout[]>([]);
  const [loadingWorkouts, setLoadingWorkouts] = useState(true);
  const [errorWorkouts, setErrorWorkouts] = useState<string | null>(null);

  const [todayWorkoutCount, setTodayWorkoutCount] = useState(0);
  const [weeklyWorkoutCount, setWeeklyWorkoutCount] = useState(0);

  const [meals, setMeals] = useState<Meal[]>([]);
  const [loadingMeal, setLoadingMeals] = useState(true);
  const [errorMeals, setErrorMeals] = useState<string | null>(null);

  const [todayCalories, setTodayCalories] = useState(0);

  const [goals, setGoals] = useState<Goal[]>([]);
  const [loadingGoal, setLoadingGoals] = useState(true);
  const [errorGoals, setErrorGoals] = useState<string | null>(null);

  const calorieGoal = 2000;
  const workoutGoal = 3;

  const authHeaders = () => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle || "",
      "X-Role": user.role || "",
    };
  };

  const isToday = (dateStr: string): boolean => {
    const date = new Date(dateStr);
    const now = new Date();
    return (
      date.getDate() === now.getDate() &&
      date.getMonth() === now.getMonth() &&
      date.getFullYear() === now.getFullYear()
    );
  };

  const isThisWeek = (dateStr: string): boolean => {
    const date = new Date(dateStr);
    const now = new Date();
    const startOfWeek = new Date(now);
    startOfWeek.setDate(now.getDate() - now.getDay());
    const endOfWeek = new Date(startOfWeek);
    endOfWeek.setDate(startOfWeek.getDate() + 6);
    return date >= startOfWeek && date <= endOfWeek;
  };

  useEffect(() => {
    const fetchUserMeals = async () => {
      setLoadingMeals(true);
      setErrorMeals(null);
      try {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        if (!user.handle) {
          setErrorMeals("User handle not found. Please log in.");
          setLoadingMeals(false);
          return;
        }

        const response = await fetch(
          `http://localhost:8000/nutrition-service/api/v1/meal?uuid=${user.handle}`,
          { headers: authHeaders() },
        );

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(
            errorData.message || `Failed to fetch meals: ${response.status}`,
          );
        }

        let data: Meal[] = await response.json();
        data.sort(
          (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime(),
        );
        data = data.map((meal) => {
          const totalCalories = meal.foods.reduce(
            (sum, food) => sum + food.calories,
            0,
          );
          return {
            ...meal,
            totalCalories,
          };
        });
        setMeals(data);

        setTodayCalories(
          data
            .filter((meal) => isToday(meal.date.toString()))
            .reduce((sum, meal: Meal) => sum + meal.totalCalories, 0),
        );
      } catch (err: any) {
        console.error("Error fetching meals:", err);
        setErrorMeals(err.message);
      } finally {
        setLoadingMeals(false);
      }
    };

    const fetchUserWorkouts = async () => {
      setLoadingWorkouts(true);
      setErrorWorkouts(null);
      try {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        if (!user.handle) {
          setErrorWorkouts("User handle not found. Please log in.");
          setLoadingWorkouts(false);
          return;
        }

        const response = await fetch(
          `http://localhost:8000/workout-service/api/v1/workout/by-user-uuid/${user.handle}`,
          {
            headers: authHeaders(),
          },
        );

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(
            errorData.message || `Failed to fetch workouts: ${response.status}`,
          );
        }

        const data: Workout[] = await response.json();
        data.sort(
          (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime(),
        );
        setRecentWorkouts(data.slice(0, 3));

        // Calculate stats
        setTodayWorkoutCount(data.filter((w) => isToday(w.date)).length);
        setWeeklyWorkoutCount(data.filter((w) => isThisWeek(w.date)).length);
      } catch (err: any) {
        console.error("Error fetching workouts:", err);
        setErrorWorkouts(err.message);
      } finally {
        setLoadingWorkouts(false);
      }
    };

    const fetchUserGoals = async () => {
      setLoadingGoals(true);
      setErrorGoals(null);
      try {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        if (!user.handle) {
          setErrorGoals("User handle not found. Please log in.");
          setLoadingGoals(false);
          return;
        }

        const response = await fetch(
          `http://localhost:8000/notification-service/api/v1/goal?user_handle=${user.handle}`,
          {
            headers: authHeaders(),
          },
        );

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(
            errorData.message || `Failed to fetch goals: ${response.status}`,
          );
        }

        const data = await response.json();
        setGoals(data);
      } catch (err: any) {
        console.error("Error fetching goals:", err);
        setErrorWorkouts(err.message);
      } finally {
        setLoadingWorkouts(false);
      }
    };

    fetchUserWorkouts();
    fetchUserMeals();
    fetchUserGoals();
  }, []);

  return (
    <DashboardLayout>
      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-4xl font-bold">
              <span className="fitness-text-gradient">Dashboard</span>
            </h1>
            <p className="text-slate-400">
              Welcome back! Here's your fitness overview.
            </p>
          </div>
          <div className="flex gap-2">
            <Button className="fitness-button-primary" asChild>
              <Link href="/workouts/new">
                <Zap className="h-4 w-4 mr-2" />
                New Workout
              </Link>
            </Button>
            <Button className="fitness-button-energy" asChild>
              <Link href="/nutrition/new">
                <Plus className="h-4 w-4 mr-2" />
                Add Meal
              </Link>
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="fitness-card border-fitness-electric/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Today's Workouts
              </CardTitle>
              <Activity className="h-4 w-4 text-fitness-electric" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-electric">
                {todayWorkoutCount}
              </div>
              <p className="text-xs text-slate-400 mb-2">
                Goal: {workoutGoal} workouts/week
              </p>
              <Progress
                value={(weeklyWorkoutCount / workoutGoal) * 100}
                className="mt-2 h-2 bg-slate-700"
              />
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-success/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Calories Today
              </CardTitle>
              <Apple className="h-4 w-4 text-fitness-success" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-success">
                {todayCalories}
              </div>
              <p className="text-xs text-slate-400 mb-2">
                Goal: {calorieGoal} calories
              </p>
              <Progress
                value={(todayCalories / calorieGoal) * 100}
                className="mt-2 h-2 bg-slate-700"
              />
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-warning/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Active Goals
              </CardTitle>
              <Target className="h-4 w-4 text-fitness-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-warning">
                {goals.length}
              </div>
              <p className="text-xs text-slate-400">
                Fitness & nutrition goals
              </p>
            </CardContent>
          </Card>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Card className="fitness-card">
            <CardHeader>
              <CardTitle className="text-white flex items-center gap-2">
                <Activity className="h-5 w-5 text-fitness-electric" />
                Recent Workouts
              </CardTitle>
              <CardDescription className="text-slate-400">
                Your latest training sessions
              </CardDescription>
            </CardHeader>
            <CardContent>
              {loadingWorkouts ? (
                <div className="text-center py-4 text-slate-400">
                  Loading workouts...
                </div>
              ) : errorWorkouts ? (
                <div className="text-center py-4 text-red-500">
                  Error: {errorWorkouts}
                </div>
              ) : recentWorkouts.length === 0 ? (
                <div className="text-center py-4 text-slate-400">
                  No recent workouts found.
                </div>
              ) : (
                <div className="space-y-4">
                  {recentWorkouts.map((workout) => (
                    <div
                      key={workout.uuid}
                      className="flex items-center justify-between p-3 bg-slate-800/50 border border-slate-700/50 rounded-lg hover:border-fitness-electric/30 transition-colors"
                    >
                      <div>
                        <p className="font-medium text-white">{workout.name}</p>
                        <p className="text-sm text-slate-400">
                          {workout.exercises.length} exercises
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="text-sm text-slate-400">
                          {new Date(workout.date).toLocaleDateString("en-US", {
                            year: "numeric",
                            month: "short",
                            day: "numeric",
                          })}
                        </p>
                        <Calendar className="h-4 w-4 text-fitness-electric ml-auto" />
                      </div>
                    </div>
                  ))}
                  <Button
                    variant="outline"
                    className="w-full border-fitness-electric text-fitness-electric hover:bg-fitness-electric hover:text-fitness-dark"
                    asChild
                  >
                    <Link href="/workouts">View All Workouts</Link>
                  </Button>
                </div>
              )}
            </CardContent>
          </Card>

          <Card className="fitness-card">
            <CardHeader>
              <CardTitle className="text-white flex items-center gap-2">
                <Apple className="h-5 w-5 text-fitness-success" />
                Recent Meals
              </CardTitle>
              <CardDescription className="text-slate-400">
                Your latest nutrition entries
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {meals.slice(0, 3).map((meal) => (
                  <div
                    key={meal.id}
                    className="flex items-center justify-between p-3 bg-slate-800/50 border border-slate-700/50 rounded-lg hover:border-fitness-success/30 transition-colors"
                  >
                    <div>
                      <p className="font-medium text-white">{meal.name}</p>
                      <p className="text-sm text-slate-400">
                        {meal.totalCalories} calories
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-slate-400">
                        {formatDateTime(meal.date.toString())}
                      </p>
                    </div>
                  </div>
                ))}
                <Button
                  variant="outline"
                  className="w-full border-fitness-success text-fitness-success hover:bg-fitness-success hover:text-fitness-dark"
                  asChild
                >
                  <Link href="/nutrition">View All Meals</Link>
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </DashboardLayout>
  );
}
