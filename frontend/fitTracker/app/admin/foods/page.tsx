"use client";

import { useState, useEffect } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
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
} from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Search, Plus, Edit, Trash2, Apple, ArrowLeft } from "lucide-react";
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

export default function AdminFoodsPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [meals, setMeals] = useState<Meal[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMeals = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(
          `http://localhost:8000/nutrition-service/api/v1/meal`,
          {
            headers: {
              "Content-Type": "application/json",
              "X-Handle": "u",
              "X-Role": "u",
            },
          },
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

  const deleteMeal = async (mealId: string) => {
    try {
      const res = await fetch(
        `http://localhost:8000/nutrition-service/api/v1/meal/full/${mealId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            "X-Handle": "u",
            "X-Role": "u",
          },
        },
      );

      if (!res.ok) {
        throw new Error(`Failed to delete meal: ${res.statusText}`);
      }

      console.log("Meal deleted successfully:", mealId);
      setMeals((prevMeals) => prevMeals.filter((meal) => meal.id !== mealId));
    } catch (err) {
      console.error(err);
      alert(`Error deleting meal: ${err.message}`);
    }
  };

  const filteredMeals = meals.filter((meal) => {
    const mealNameMatches = meal.name
      .toLowerCase()
      .includes(searchTerm.toLowerCase());

    const foodNameMatches = meal.foods.some((food) =>
      food.name.toLowerCase().includes(searchTerm.toLowerCase()),
    );

    return mealNameMatches || foodNameMatches;
  });

  const totalFoods = meals.reduce((sum, meal) => sum + meal.foods.length, 0);

  const averageCaloriesPerMeal = () => {
    if (meals.length === 0) return 0;

    const totalCalories = meals.reduce((sum, meal) => {
      const mealCalories = meal.foods.reduce(
        (foodSum, food) => foodSum + (food.calories || 0),
        0,
      );
      return sum + mealCalories;
    }, 0);

    return Math.round((totalCalories / meals.length) * 100) / 100;
  };

  const averageCalories = averageCaloriesPerMeal();

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Button
                variant="ghost"
                size="sm"
                asChild
                className="text-slate-400 hover:text-white"
              >
                <Link href="/admin">
                  <ArrowLeft className="h-4 w-4 mr-1" />
                  Back to Admin
                </Link>
              </Button>
            </div>
            <h1 className="text-4xl font-bold">
              <span className="fitness-text-gradient">Meal Database</span>
            </h1>
            <p className="text-slate-400">
              Manage nutrition database and food items
            </p>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="fitness-card border-fitness-success/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Total Meals
              </CardTitle>
              <Apple className="h-4 w-4 text-fitness-success" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-success">
                {meals.length}
              </div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-electric/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Total Foods
              </CardTitle>
              <Apple className="h-4 w-4 text-fitness-electric" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-electric">
                {totalFoods}
              </div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-energy/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Average Calories per Meal
              </CardTitle>
              <Apple className="h-4 w-4 text-fitness-energy" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-fitness-energy">
                {averageCalories}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Filters */}
        <div className="grid grid-cols-1">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
            <Input
              placeholder="Search meals and foods..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 bg-slate-800/50 border-slate-700 text-white"
            />
          </div>
        </div>

        {/* Foods Table */}
        <Card className="fitness-card">
          <CardHeader>
            <CardTitle className="text-white">
              Meals ({filteredMeals.length})
            </CardTitle>
            <CardDescription className="text-slate-400">
              Manage all meals in the database
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow className="border-slate-700">
                    <TableHead className="text-slate-300">Meal</TableHead>
                    <TableHead className="text-slate-300">Foods</TableHead>
                    <TableHead className="text-slate-300">
                      Total Calories
                    </TableHead>
                    <TableHead className="text-slate-300 text-end">
                      Actions
                    </TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredMeals.map((meal) => (
                    <TableRow
                      key={meal.id}
                      className="border-slate-700 hover:bg-slate-800/30"
                    >
                      <TableCell>
                        <div className="flex items-center gap-3">
                          <div className="h-8 w-8 rounded-full bg-gradient-to-r from-fitness-success to-fitness-electric flex items-center justify-center">
                            <Apple className="h-4 w-4 text-fitness-dark" />
                          </div>
                          <div>
                            <p className="text-md font-medium text-white">
                              {meal.name}
                            </p>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="text-sm space-y-1">
                          <div className="flex gap-4">
                            <span className="text-fitness-success">
                              {meal.foods.map((food) => (
                                <Badge variant="outline" className="text-md">
                                  {food.name}
                                </Badge>
                              ))}
                            </span>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <p className="text-md font-medium text-slate-300">
                          {meal.foods.reduce(
                            (sum, food) => sum + (food.calories || 0),
                            0,
                          )}
                        </p>
                      </TableCell>
                      <TableCell>
                        <div className="flex justify-end gap-2">
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-fitness-electric hover:bg-fitness-electric/20"
                          >
                            <Edit className="h-4 w-4" />
                          </Button>
                          <AlertDialog>
                            <AlertDialogTrigger asChild>
                              <Button
                                variant="ghost"
                                size="sm"
                                className="text-fitness-energy hover:bg-fitness-energy/20"
                              >
                                <Trash2 className="h-4 w-4" />
                              </Button>
                            </AlertDialogTrigger>
                            <AlertDialogContent className="fitness-card border-slate-700">
                              <AlertDialogHeader>
                                <AlertDialogTitle className="text-white">
                                  Delete Meal
                                </AlertDialogTitle>
                                <AlertDialogDescription className="text-slate-400">
                                  Are you sure you want to delete this meal?
                                  This action cannot be undone.
                                </AlertDialogDescription>
                              </AlertDialogHeader>
                              <AlertDialogFooter>
                                <AlertDialogCancel className="border-slate-600 text-slate-300 hover:bg-slate-800">
                                  Cancel
                                </AlertDialogCancel>
                                <AlertDialogAction
                                  onClick={() => deleteMeal(meal.id)}
                                  className="bg-red-500 hover:bg-red-600 text-white"
                                >
                                  Delete Meal
                                </AlertDialogAction>
                              </AlertDialogFooter>
                            </AlertDialogContent>
                          </AlertDialog>
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
  );
}
