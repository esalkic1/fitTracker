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
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Plus, Target, TrendingUp, Trophy, Clock } from "lucide-react";
import Link from "next/link";
import { DashboardLayout } from "@/components/dashboard-layout";
import { useRouter } from "next/navigation";

type Goal = {
  id: string;
  type: string;
  frequency: string;
  target: number;
};

type Meal = {
  id: string;
  name: string;
  foods: {
    id: string;
    name: string;
    calories: number;
  }[];
  date: Date;
};

export default function GoalsPage() {
  const [goals, setGoals] = useState<Goal[]>([]);
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState({
    total: 0,
    completed: 0,
    inProgress: 0,
    completionRate: 0,
  });

  const router = useRouter();

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user")!);
    const authHeaders = () => {
      const user = JSON.parse(localStorage.getItem("user")!);
      return {
        "Content-Type": "application/json",
        "X-Handle": user.handle || "",
        "X-Role": user.role || "",
      };
    };

    const fetchMeals = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(
          `http://localhost:8000/nutrition-service/api/v1/meal?uuid=${user.handle}`,
          { headers: authHeaders() },
        );

        if (!res.ok) {
          throw new Error(`Failed to fetch meals: ${res.statusText}`);
        }

        const data = await res.json();
        setMeals(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    const fetchGoals = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(
          `http://localhost:8000/notification-service/api/v1/goal?uuid=${user.handle}`,
          { headers: authHeaders() },
        );

        if (!res.ok) {
          throw new Error(`Failed to fetch goals: ${res.statusText}`);
        }

        const data = await res.json();
        setGoals(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchMeals();
    fetchGoals();
  }, []);

  const getGoalTypeColor = (type: string) => {
    return type === "WORKOUT"
      ? "bg-blue-100 text-blue-800"
      : "bg-green-100 text-green-800";
  };

  const getFrequencyText = (frequency: string) => {
    return frequency.toLowerCase();
  };

  const getProgressPercentage = (goal: Goal, meals: Meal[]) => {
    const now = new Date();

    const filteredMeals = meals
      .map((meal) => {
        const totalCalories = meal.foods.reduce(
          (sum, food) => sum + food.calories,
          0,
        );
        return {
          ...meal,
          totalCalories,
        };
      })
      .filter((meal) => {
        const mealDate = new Date(meal.date);

        if (goal.type === "DAILY") {
          return (
            mealDate.getFullYear() === now.getFullYear() &&
            mealDate.getMonth() === now.getMonth() &&
            mealDate.getDate() === now.getDate()
          );
        }

        if (goal.type === "WEEKLY") {
          const startOfWeek = new Date(now);
          startOfWeek.setDate(now.getDate() - now.getDay());
          startOfWeek.setHours(0, 0, 0, 0);

          const endOfWeek = new Date(startOfWeek);
          endOfWeek.setDate(startOfWeek.getDate() + 7);

          return mealDate >= startOfWeek && mealDate < endOfWeek;
        }

        if (goal.type === "MONTHLY") {
          return (
            mealDate.getFullYear() === now.getFullYear() &&
            mealDate.getMonth() === now.getMonth()
          );
        }

        return false;
      });

    const current = filteredMeals.reduce(
      (sum, meal) => sum + (meal.totalCalories ?? 0),
      0,
    );

    return Math.min((current / goal.target) * 100, 100);
  };

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold">Goals</h1>
            <p className="text-gray-600">
              Set and track your fitness and nutrition goals
            </p>
          </div>
          <div className="flex flex-col sm:flex-row gap-3">
            <Button
              onClick={() => router.push("/goals/new")}
              className="bg-blue-600 hover:bg-blue-700"
            >
              <Plus className="h-4 w-4 mr-2" />
              New Goal
            </Button>
          </div>
        </div>

        {/* Goals Overview */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Total Goals</CardTitle>
              <Target className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{goals.length}</div>
              <p className="text-xs text-muted-foreground">Goals created</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Completed</CardTitle>
              <Trophy className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-green-600">
                {stats.completed}
              </div>
              <p className="text-xs text-muted-foreground">Goals achieved</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">In Progress</CardTitle>
              <Clock className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-blue-600">
                {stats.inProgress}
              </div>
              <p className="text-xs text-muted-foreground">Active goals</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">
                Success Rate
              </CardTitle>
              <TrendingUp className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.completionRate}%</div>
              <p className="text-xs text-muted-foreground">Completion rate</p>
            </CardContent>
          </Card>
        </div>

        {/* Goals List */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Your Goals</h2>
          {goals.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <Target className="h-12 w-12 text-gray-400 mb-4" />
                <h3 className="text-lg font-medium text-gray-900 mb-2">
                  No goals yet
                </h3>
                <p className="text-gray-600 text-center mb-4">
                  Create your first goal to start tracking your fitness and
                  nutrition progress.
                </p>
                <Button asChild>
                  <Link href="/goals/new">
                    <Plus className="h-4 w-4 mr-2" />
                    Create Your First Goal
                  </Link>
                </Button>
              </CardContent>
            </Card>
          ) : (
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {goals.map((goal, idx) => (
                <Card key={goal.id}>
                  <CardHeader>
                    <div className="flex justify-between items-start">
                      <div>
                        <CardTitle className="text-lg flex items-center gap-2">
                          Goal #{idx + 1}
                        </CardTitle>
                      </div>
                      <div className="flex gap-2">
                        <Badge className={getGoalTypeColor(goal.type)}>
                          {goal.type.toLowerCase()}
                        </Badge>
                        <Badge variant="outline">
                          {getFrequencyText(goal.frequency)}
                        </Badge>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      <div>
                        <div className="flex justify-between text-sm mb-2">
                          <span>Progress</span>
                          <span>
                            {goal.current} / {goal.target}
                          </span>
                        </div>
                        <Progress
                          value={getProgressPercentage(goal, meals)}
                          className="h-2"
                        />
                        <div className="flex justify-between text-xs text-gray-500 mt-1">
                          <span>
                            {Math.round(getProgressPercentage(goal, meals))}%
                            complete
                          </span>
                        </div>
                      </div>

                      <div className="flex justify-end gap-2">
                        <Button variant="outline" size="sm" asChild>
                          <Link href={`/goals/${goal.id}`}>View Details</Link>
                        </Button>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>

        {/* Goal Categories */}
        {goals.length > 0 && (
          <div className="space-y-6">
            <h2 className="text-xl font-semibold mb-4">Goal Categories</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Target className="h-5 w-5 text-blue-600" />
                    Workout Goals
                  </CardTitle>
                  <CardDescription>
                    Track your exercise frequency and consistency
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {goals
                      .filter((goal) => goal.type === "WORKOUT")
                      .map((goal) => (
                        <Link
                          key={goal.id}
                          href={`/goals/${goal.id}`}
                          className="flex flex-col sm:flex-row sm:items-center justify-between p-4 bg-slate-800/30 border border-slate-700/50 rounded-lg hover:border-fitness-electric/30 transition-all duration-200"
                        >
                          <div className="flex items-center gap-3 mb-2 sm:mb-0">
                            <div className="w-2 h-10 bg-blue-500 rounded-full"></div>
                            <div>
                              <span className="font-medium block">
                                {goal.title}
                              </span>
                              <span className="text-xs text-gray-500">
                                {goal.frequency.toLowerCase()} • {goal.target}{" "}
                                {goal.target > 1 ? "units" : "unit"}
                              </span>
                            </div>
                            {goal.isCompleted && (
                              <Badge className="ml-2 bg-green-100 text-green-800 hover:bg-green-200">
                                <Trophy className="h-3 w-3 mr-1" /> Completed
                              </Badge>
                            )}
                          </div>
                          <div className="flex items-center gap-3">
                            <div className="flex flex-col items-end">
                              <span className="text-blue-700 font-semibold text-sm">
                                {goal.current}/{goal.target}
                              </span>
                              <span className="text-xs text-gray-500">
                                {Math.round(getProgressPercentage(goal, meals))}
                                % complete
                              </span>
                            </div>
                            <div className="w-16 h-2 bg-blue-200 rounded-full overflow-hidden">
                              <div
                                className="h-full bg-blue-600 transition-all duration-300"
                                style={{
                                  width: `${getProgressPercentage(goal, meals)}%`,
                                }}
                              ></div>
                            </div>
                          </div>
                        </Link>
                      ))}
                    {goals.filter((goal) => goal.type === "WORKOUT").length ===
                      0 && (
                      <div className="flex flex-col items-center justify-center py-6 text-center">
                        <Target className="h-8 w-8 text-gray-300 mb-2" />
                        <p className="text-gray-500 text-sm mb-3">
                          No workout goals yet
                        </p>
                        <Button variant="outline" size="sm" asChild>
                          <Link href="/goals/new">
                            <Plus className="h-3 w-3 mr-1" />
                            Add Workout Goal
                          </Link>
                        </Button>
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Target className="h-5 w-5 text-green-600" />
                    Nutrition Goals
                  </CardTitle>
                  <CardDescription>
                    Monitor your dietary habits and intake
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {goals
                      .filter((goal) => goal.type === "NUTRITION")
                      .map((goal) => (
                        <Link
                          key={goal.id}
                          href={`/goals/${goal.id}`}
                          className="flex flex-col sm:flex-row sm:items-center justify-between p-4 bg-slate-800/30 border border-slate-700/50 rounded-lg hover:border-fitness-green/30 transition-all duration-200"
                        >
                          <div className="flex items-center gap-3 mb-2 sm:mb-0">
                            <div className="w-2 h-10 bg-green-500 rounded-full"></div>
                            <div>
                              <span className="font-medium block">
                                {goal.title}
                              </span>
                              <span className="text-xs text-gray-500">
                                {goal.frequency.toLowerCase()} • {goal.target}{" "}
                                {goal.target > 1 ? "units" : "unit"}
                              </span>
                            </div>
                            {goal.isCompleted && (
                              <Badge className="ml-2 bg-green-100 text-green-800 hover:bg-green-200">
                                <Trophy className="h-3 w-3 mr-1" /> Completed
                              </Badge>
                            )}
                          </div>
                          <div className="flex items-center gap-3">
                            <div className="flex flex-col items-end">
                              <span className="text-green-700 font-semibold text-sm">
                                {goal.current}/{goal.target}
                              </span>
                              <span className="text-xs text-gray-500">
                                {Math.round(getProgressPercentage(goal, meals))}
                                % complete
                              </span>
                            </div>
                            <div className="w-16 h-2 bg-green-200 rounded-full overflow-hidden">
                              <div
                                className="h-full bg-green-600 transition-all duration-300"
                                style={{
                                  width: `${getProgressPercentage(goal, meals)}%`,
                                }}
                              ></div>
                            </div>
                          </div>
                        </Link>
                      ))}
                    {goals.filter((goal) => goal.type === "NUTRITION")
                      .length === 0 && (
                      <div className="flex flex-col items-center justify-center py-6 text-center">
                        <Target className="h-8 w-8 text-gray-300 mb-2" />
                        <p className="text-gray-500 text-sm mb-3">
                          No nutrition goals yet
                        </p>
                        <Button variant="outline" size="sm" asChild>
                          <Link href="/goals/new">
                            <Plus className="h-3 w-3 mr-1" />
                            Add Nutrition Goal
                          </Link>
                        </Button>
                      </div>
                    )}
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
