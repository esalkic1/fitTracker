"use client";

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Progress } from "@/components/ui/progress";
import { Plus, Search, Calendar, Apple, Target } from "lucide-react";
import Link from "next/link";
import { DashboardLayout } from "@/components/dashboard-layout";
import { Badge } from "@/components/ui/badge";

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
};

function isToday(date: Date) {
  const today = new Date();
  return (
    date.getDate() === today.getDate() &&
    date.getMonth() === today.getMonth() &&
    date.getFullYear() === today.getFullYear()
  );
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

export default function NutritionPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user")!);
    const authHeaders = () => {
      const user = JSON.parse(localStorage.getItem("user")!);
      return {
        "Content-Type": "application/json",
        "X-Handle": user.handle,
        "X-Role": user.role,
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

    fetchMeals();
  }, []);

  const filteredMeals = meals
    .filter(
      (meal) =>
        meal.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        meal.foods.some((food) =>
          food.name.toLowerCase().includes(searchTerm.toLowerCase()),
        ),
    )
    .map((meal) => {
      const totalCalories = meal.foods.reduce(
        (sum, food) => sum + food.calories,
        0,
      );
      return {
        ...meal,
        totalCalories,
      };
    });

  const mealsToday = filteredMeals
    .filter((meal) => isToday(new Date(meal.date)))
    .map((meal) => {
      const totalCalories = meal.foods.reduce(
        (sum, food) => sum + food.calories,
        0,
      );
      return {
        ...meal,
        totalCalories,
      };
    });

  const totalCalories = filteredMeals.reduce(
    (sum, meal) => sum + meal.totalCalories,
    0,
  );
  const totalFoods = filteredMeals.reduce(
    (sum, meal) => sum + meal.foods.length,
    0,
  );

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold">Nutrition</h1>
            <p className="text-gray-600">
              Track your daily nutrition and calories
            </p>
          </div>
          <div className="flex gap-2">
            <Button asChild>
              <Link href="/nutrition/new">
                <Plus className="h-4 w-4 mr-2" />
                Add Meal
              </Link>
            </Button>
          </div>
        </div>

        {/* Daily Summary */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="fitness-card border-fitness-success/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Total Calories
              </CardTitle>
              <Apple className="h-4 w-4 text-fitness-success" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-success">
                {totalCalories}
              </div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-electric/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Number of Meals
              </CardTitle>
              <Target className="h-4 w-4 text-fitness-electric" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-electric">
                {filteredMeals.length}
              </div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-warning/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Number of Foods
              </CardTitle>
              <Target className="h-4 w-4 text-fitness-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-warning">
                {totalFoods}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            placeholder="Search meals or foods..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>

        {/* Meals */}
        <div>
          <h2 className="text-xl font-semibold mb-4">Today's Meals</h2>
          <div className="space-y-4">
            {mealsToday.map((meal) => (
              <Card key={meal.id} className="fitness-card">
                <CardHeader>
                  <div className="flex justify-between items-start">
                    <div>
                      <CardTitle className="text-lg">{meal.name}</CardTitle>
                      <div className="flex items-center gap-4 text-sm text-gray-600 mt-2">
                        <div className="flex items-center gap-1">
                          <Apple className="h-4 w-4" />
                          {meal.totalCalories} calories
                        </div>
                        <div>
                          {new Date(meal.date).toLocaleTimeString([], {
                            hour: "2-digit",
                            minute: "2-digit",
                          })}
                        </div>
                      </div>
                    </div>
                    <Button variant="outline" size="sm" asChild>
                      <Link href={`/nutrition/meals/${meal.id}`}>
                        View Details
                      </Link>
                    </Button>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {meal.foods.map((food, index) => (
                      <div
                        key={index}
                        className="p-4 bg-slate-800/30 border border-slate-700/50 rounded-lg hover:border-fitness-electric/30 transition-all duration-200"
                      >
                        <div className="flex justify-between items-center">
                          <div>
                            <h4 className="font-medium text-white">
                              {food.name}
                            </h4>
                            <p className="text-sm text-slate-400">
                              Food item {index + 1}
                            </p>
                          </div>
                          <div className="text-right">
                            <Badge className="bg-fitness-success/20 text-fitness-success">
                              {food.calories} calories
                            </Badge>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>

        <div>
          <h2 className="text-xl font-semibold mb-4">All Meals</h2>
          <div className="space-y-4">
            {filteredMeals.map((meal) => (
              <Card key={meal.id} className="fitness-card">
                <CardHeader>
                  <div className="flex justify-between items-start">
                    <div>
                      <CardTitle className="text-lg">{meal.name}</CardTitle>
                      <div className="flex items-center gap-4 text-sm text-gray-600 mt-2">
                        <div className="flex items-center gap-1">
                          <Apple className="h-4 w-4" />
                          {meal.totalCalories} calories
                        </div>
                        <div>{formatDateTime(meal.date.toString())}</div>
                      </div>
                    </div>
                    <Button variant="outline" size="sm" asChild>
                      <Link href={`/nutrition/meals/${meal.id}`}>
                        View Details
                      </Link>
                    </Button>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    {meal.foods.map((food, index) => (
                      <div
                        key={index}
                        className="p-4 bg-slate-800/30 border border-slate-700/50 rounded-lg hover:border-fitness-electric/30 transition-all duration-200"
                      >
                        <div className="flex justify-between items-center">
                          <div>
                            <h4 className="font-medium text-white">
                              {food.name}
                            </h4>
                            <p className="text-sm text-slate-400">
                              Food item {index + 1}
                            </p>
                          </div>
                          <div className="text-right">
                            <Badge className="bg-fitness-success/20 text-fitness-success">
                              {food.calories} calories
                            </Badge>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
}
