"use client";

import { useState } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Plus, Trash2, Save, Calendar } from "lucide-react";
import { DashboardLayout } from "@/components/dashboard-layout";
import { useRouter } from "next/navigation";

interface Food {
  id: string;
  name: string;
  calories: number;
}

export default function NewMealPage() {
  const router = useRouter();
  const [mealName, setMealName] = useState("");
  const [mealDate, setMealDate] = useState(new Date().toString());
  const [foods, setFoods] = useState<Food[]>([]);

  const authHeaders = () => {
    const user = JSON.parse(localStorage.getItem("user")!);
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle || "",
      "X-Role": user.role || "",
    };
  };

  const addFood = () => {
    const newFood: Food = {
      id: Date.now().toString(),
      name: "",
      calories: 0,
    };
    setFoods([...foods, newFood]);
  };

  const removeFood = (id: string) => {
    setFoods(foods.filter((food) => food.id !== id));
  };

  const updateFood = (id: string, field: keyof Food, value: any) => {
    setFoods(
      foods.map((food) => {
        if (food.id === id) {
          return { ...food, [field]: value };
        }
        return food;
      }),
    );
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const user = JSON.parse(localStorage.getItem("user")!);

    const data = {
      meal: {
        name: mealName,
        userId: 1,
        date: new Date(mealDate).toISOString().split(".")[0] + "Z",
      },
      foods: foods,
      userHandle: user.handle,
    };

    console.log(data);

    try {
      const response = await fetch(
        "http://localhost:8000/nutrition-service/api/v1/meal/full",
        {
          method: "POST",
          headers: authHeaders(),
          body: JSON.stringify(data),
        },
      );

      if (!response.ok) {
        const errorData = await response.json();
        console.error("Error response:", errorData);
        return;
      }

      const result = await response.json();
      console.log("Meal created successfully:", result);
      router.push("/nutrition");
    } catch (error) {
      console.error("Network or server error:", error);
    }
  };

  const totalCalories = foods.reduce(
    (sum, food) => sum + (food.calories || 0),
    0,
  );

  const formatDateTime = (dateTimeString: string) => {
    const date = new Date(dateTimeString);
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

  const { date, time } = formatDateTime(mealDate);

  return (
    <DashboardLayout>
      <form onSubmit={handleSubmit}>
        <div className="space-y-6">
          {/* Header */}
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-4xl font-bold">
                <span className="fitness-text-gradient">New Meal</span>
              </h1>
              <p className="text-slate-400">
                Log a new meal and track your nutrition
              </p>
            </div>
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => router.back()}>
                Cancel
              </Button>
              <Button className="fitness-button-primary">
                <Save className="h-4 w-4 mr-2" />
                Save Meal
              </Button>
            </div>
          </div>

          {/* Meal Details */}
          <Card className="fitness-card">
            <CardHeader>
              <CardTitle className="text-white">Meal Details</CardTitle>
              <CardDescription className="text-slate-400">
                Basic information about your meal
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="mealName" className="text-slate-200">
                    Meal Name *
                  </Label>
                  <Input
                    id="mealName"
                    placeholder="e.g., Breakfast, Lunch, Dinner, Snack"
                    value={mealName}
                    onChange={(e) => setMealName(e.target.value)}
                    className="bg-slate-800/50 border-slate-700 text-white"
                    required
                  />
                </div>

                <div>
                  <Label htmlFor="mealDate" className="text-slate-200">
                    Date & Time
                  </Label>
                  <div className="h-10 px-3 flex items-center bg-slate-800/30 border border-slate-700/50 rounded-lg">
                    <div className="flex items-center gap-2 text-slate-300">
                      <Calendar className="h-4 w-4 text-fitness-success" />
                      <span className="text-sm">
                        {date} at {time}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Food Items */}
          <Card className="fitness-card">
            <CardHeader>
              <div className="flex justify-between items-center">
                <div>
                  <CardTitle className="text-white">Food Items</CardTitle>
                  <CardDescription className="text-slate-400">
                    Add food items and their calorie content
                  </CardDescription>
                </div>
                <div className="flex items-center gap-4">
                  {totalCalories > 0 && (
                    <div className="text-right">
                      <p className="text-sm text-slate-400">Total Calories</p>
                      <p className="text-2xl font-bold text-fitness-success">
                        {totalCalories}
                      </p>
                    </div>
                  )}
                  <Button
                    onClick={(e) => {
                      e.preventDefault();
                      addFood();
                    }}
                    className="fitness-button-primary"
                  >
                    <Plus className="h-4 w-4 mr-2" />
                    Add Food
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent>
              {foods.length === 0 ? (
                <div className="text-center py-8 text-slate-500">
                  No food items added yet. Click "Add Food" to get started.
                </div>
              ) : (
                <div className="space-y-4">
                  {foods.map((food, index) => (
                    <div
                      key={food.id}
                      className="p-4 border border-slate-700/50 rounded-lg bg-slate-800/30 space-y-4"
                    >
                      <div className="flex justify-between items-center">
                        <h4 className="font-medium text-white">
                          Food Item {index + 1}
                        </h4>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => removeFood(food.id)}
                        >
                          <Trash2 className="h-4 w-4 text-red-400" />
                        </Button>
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                          <Label className="text-slate-200">Food Name *</Label>
                          <Input
                            placeholder="e.g., Grilled Chicken Breast, Brown Rice, Apple"
                            value={food.name}
                            onChange={(e) =>
                              updateFood(food.id, "name", e.target.value)
                            }
                            className="bg-slate-800/50 border-slate-700 text-white"
                            required
                          />
                        </div>

                        <div>
                          <Label className="text-slate-200">Calories *</Label>
                          <Input
                            type="number"
                            placeholder="0"
                            value={food.calories || ""}
                            onChange={(e) =>
                              updateFood(
                                food.id,
                                "calories",
                                Number.parseFloat(e.target.value) || 0,
                              )
                            }
                            min="0"
                            step="1"
                            className="bg-slate-800/50 border-slate-700 text-white"
                            required
                          />
                        </div>
                      </div>

                      {food.calories > 0 && (
                        <div className="flex items-center gap-2">
                          <div className="h-2 w-full bg-slate-700 rounded-full overflow-hidden">
                            <div
                              className="h-full bg-gradient-to-r from-fitness-success to-fitness-electric transition-all duration-300"
                              style={{
                                width: `${Math.min((food.calories / Math.max(totalCalories, 1)) * 100, 100)}%`,
                              }}
                            />
                          </div>
                          <span className="text-xs text-slate-400 whitespace-nowrap">
                            {totalCalories > 0
                              ? Math.round(
                                  (food.calories / totalCalories) * 100,
                                )
                              : 0}
                            %
                          </span>
                        </div>
                      )}
                    </div>
                  ))}

                  {/* Summary */}
                  {foods.length > 0 && (
                    <div className="mt-6 p-4 bg-gradient-to-r from-fitness-success/10 to-fitness-electric/10 border border-fitness-success/30 rounded-lg">
                      <div className="flex justify-between items-center">
                        <div>
                          <h4 className="font-medium text-white">
                            Meal Summary
                          </h4>
                          <p className="text-sm text-slate-400">
                            {foods.length} food items
                          </p>
                        </div>
                        <div className="text-right">
                          <p className="text-sm text-slate-400">
                            Total Calories
                          </p>
                          <p className="text-3xl font-bold text-fitness-success">
                            {totalCalories}
                          </p>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      </form>
    </DashboardLayout>
  );
}
