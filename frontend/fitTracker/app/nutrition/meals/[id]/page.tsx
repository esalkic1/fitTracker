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
import {
  Clock,
  Apple,
  ArrowLeft,
  Edit,
  Trash2,
  Calendar,
  Target,
} from "lucide-react";
import Link from "next/link";
import { DashboardLayout } from "@/components/dashboard-layout";
import { useRouter, useParams } from "next/navigation";

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

export default function MealDetailsPage() {
  const router = useRouter();
  const params = useParams();
  const mealId = params.id?.toString();

  const [meal, setMeal] = useState<Meal>({
    id: mealId || "",
    name: "",
    foods: [],
    date: new Date(),
  });
  const [isLoading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMeal = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(
          `http://localhost:8000/nutrition-service/api/v1/meal/${mealId}`,
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
        setMeal(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchMeal();
  }, []);

  const deleteMeal = async () => {
    try {
      const res = await fetch(
        `http://localhost:8000/nutrition-service/api/v1/meal/${mealId}`,
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
      router.push("/nutrition");
    } catch (err) {
      console.error(err);
      alert(`Error deleting meal: ${err.message}`);
    }
  };

  const totalCalories = meal.foods.reduce(
    (sum, food) => sum + food.calories,
    0,
  );

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
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
    };
  };

  const { date, time } = formatDate(meal.date.toString());

  if (isLoading) {
    return (
      <DashboardLayout>
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-fitness-electric mx-auto mb-4"></div>
            <p className="text-slate-400">Loading meal...</p>
          </div>
        </div>
      </DashboardLayout>
    );
  }

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
                <Link href="/nutrition">
                  <ArrowLeft className="h-4 w-4 mr-1" />
                  Back to Nutrition
                </Link>
              </Button>
            </div>
            <h1 className="text-4xl font-bold text-white">{meal.name}</h1>
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
            <Button className="fitness-button-primary" asChild>
              <Link href={`/nutrition/meals/${mealId}/edit`}>
                <Edit className="h-4 w-4 mr-2" />
                Edit Meal
              </Link>
            </Button>
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button
                  variant="outline"
                  className="border-red-500 text-red-400 hover:bg-red-500 hover:text-white"
                >
                  <Trash2 className="h-4 w-4 mr-2" />
                  Delete
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent className="fitness-card border-slate-700">
                <AlertDialogHeader>
                  <AlertDialogTitle className="text-white">
                    Delete Meal
                  </AlertDialogTitle>
                  <AlertDialogDescription className="text-slate-400">
                    Are you sure you want to delete this meal? This action
                    cannot be undone.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel className="border-slate-600 text-slate-300 hover:bg-slate-800">
                    Cancel
                  </AlertDialogCancel>
                  <AlertDialogAction
                    onClick={deleteMeal}
                    className="bg-red-500 hover:bg-red-600 text-white"
                  >
                    Delete Meal
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          </div>
        </div>

        {/* Meal Summary */}
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
                Food Items
              </CardTitle>
              <Target className="h-4 w-4 text-fitness-electric" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-electric">
                {meal.foods.length}
              </div>
            </CardContent>
          </Card>

          <Card className="fitness-card border-fitness-warning/30">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-slate-200">
                Average Calories per Meal
              </CardTitle>
              <Target className="h-4 w-4 text-fitness-warning" />
            </CardHeader>
            <CardContent>
              <div className="text-3xl font-bold text-fitness-warning">
                {meal.foods.length > 0
                  ? Math.round(totalCalories / meal.foods.length)
                  : 0}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Food Items */}
        <Card className="fitness-card">
          <CardHeader>
            <CardTitle className="text-white">
              Food Items ({meal.foods.length})
            </CardTitle>
            <CardDescription className="text-slate-400">
              Complete breakdown of this meal
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {meal.foods.map((food, index) => (
                <div
                  key={food.id}
                  className="p-4 bg-slate-800/30 border border-slate-700/50 rounded-lg"
                >
                  <div className="flex justify-between items-center">
                    <div>
                      <h4 className="font-medium text-white">{food.name}</h4>
                      <p className="text-sm text-slate-400">
                        Food item {index + 1}
                      </p>
                    </div>
                    <div className="text-right">
                      <Badge className="bg-fitness-success/20 text-fitness-success">
                        {food.calories} calories
                      </Badge>
                      <p className="text-xs text-slate-400 mt-1">
                        {totalCalories > 0
                          ? Math.round((food.calories / totalCalories) * 100)
                          : 0}
                        % of meal
                      </p>
                    </div>
                  </div>

                  {/* Visual calorie bar */}
                  <div className="mt-3">
                    <div className="h-2 w-full bg-slate-700 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-fitness-success to-fitness-electric transition-all duration-300"
                        style={{
                          width: `${Math.min((food.calories / Math.max(totalCalories, 1)) * 100, 100)}%`,
                        }}
                      />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </DashboardLayout>
  );
}
