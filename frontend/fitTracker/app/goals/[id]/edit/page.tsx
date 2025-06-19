"use client";
import { useState, useEffect } from "react";
import type React from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ArrowLeft, Target, Save, X } from "lucide-react";
import Link from "next/link";
import { useRouter, useParams } from "next/navigation";

interface Goal {
  id: string;
  type: string;
  frequency: string;
  target: number;
}

export default function EditGoalPage() {
  const router = useRouter();
  const params = useParams();
  const goalId = params.id?.toString();

  const [goal, setGoal] = useState<Goal>({
    id: "",
    type: "",
    frequency: "",
    target: 0,
  });
  const [isLoading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const authHeaders = () => {
    const user = JSON.parse(localStorage.getItem("user")!);
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle || "",
      "X-Role": user.role || "",
    };
  };

  useEffect(() => {
    const fetchGoal = async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await fetch(
          `http://localhost:8000/notification-service/api/v1/goal/${goalId}`,
          {
            headers: authHeaders(),
          },
        );

        if (!res.ok) {
          throw new Error(`Failed to fetch goal: ${res.statusText}`);
        }

        const data = await res.json();
        setGoal(data);
      } catch (err: any) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchGoal();
  }, []);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const user = JSON.parse(localStorage.getItem("user")!);

    const data = {
      type: goal.type,
      frequency: goal.frequency,
      target: goal.target,
      userHandle: user.handle,
    };

    console.log(data);

    try {
      const response = await fetch(
        `http://localhost:8000/notification-service/api/v1/goal/${goalId}`,
        {
          method: "PUT",
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
      console.log("Goal updated successfully:", result);
      router.push("/goals");
    } catch (error) {
      console.error("Network or server error:", error);
    }
  };

  const handleCancel = () => {
    if (goal) {
      router.push(`/goals/${goalId}`);
    } else {
      router.push("/goals");
    }
  };

  const getTargetLabel = () => {
    if (goal.type === "WORKOUT") {
      return goal.frequency === "DAILY"
        ? "Workouts per day"
        : goal.frequency === "WEEKLY"
          ? "Workouts per week"
          : "Workouts per month";
    } else if (goal.type === "NUTRITION") {
      return goal.frequency === "DAILY"
        ? "Calories per day"
        : goal.frequency === "WEEKLY"
          ? "Meals per week"
          : "Meals per month";
    }
    return "Target value";
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 p-4 md:p-6">
      <div className="max-w-2xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="sm"
            asChild
            className="text-white hover:bg-slate-800"
          >
            <Link href={`/goals/${goalId}`}>
              <ArrowLeft className="h-4 w-4" />
            </Link>
          </Button>
          <div>
            <h1 className="text-2xl md:text-3xl font-bold text-white">
              Edit Goal
            </h1>
            <p className="text-slate-400">
              Update your goal settings and targets
            </p>
          </div>
        </div>

        {/* Form */}
        <Card className="bg-slate-800/50 border-slate-700 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-white">
              <Target className="h-5 w-5 text-green-400" />
              Goal Information
            </CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-6">
              {error && (
                <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-lg">
                  <p className="text-red-400 text-sm">{error}</p>
                </div>
              )}

              {/* Goal Type */}
              <div className="space-y-2">
                <Label htmlFor="type" className="text-slate-200">
                  Goal Type *
                </Label>
                <Select
                  value={goal.type}
                  onValueChange={(value: "WORKOUT" | "NUTRITION") =>
                    setGoal({ ...goal, type: value })
                  }
                >
                  <SelectTrigger className="bg-slate-700 border-slate-600 text-white">
                    <SelectValue placeholder="Select goal type" />
                  </SelectTrigger>
                  <SelectContent className="bg-slate-800 border-slate-700">
                    <SelectItem
                      value="WORKOUT"
                      className="text-white hover:bg-slate-700"
                    >
                      Workout
                    </SelectItem>
                    <SelectItem
                      value="NUTRITION"
                      className="text-white hover:bg-slate-700"
                    >
                      Nutrition
                    </SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* Frequency */}
              <div className="space-y-2">
                <Label htmlFor="frequency" className="text-slate-200">
                  Frequency *
                </Label>
                <Select
                  value={goal.frequency}
                  onValueChange={(value: "DAILY" | "WEEKLY" | "MONTHLY") =>
                    setGoal({ ...goal, frequency: value })
                  }
                >
                  <SelectTrigger className="bg-slate-700 border-slate-600 text-white">
                    <SelectValue placeholder="Select frequency" />
                  </SelectTrigger>
                  <SelectContent className="bg-slate-800 border-slate-700">
                    <SelectItem
                      value="DAILY"
                      className="text-white hover:bg-slate-700"
                    >
                      Daily
                    </SelectItem>
                    <SelectItem
                      value="WEEKLY"
                      className="text-white hover:bg-slate-700"
                    >
                      Weekly
                    </SelectItem>
                    <SelectItem
                      value="MONTHLY"
                      className="text-white hover:bg-slate-700"
                    >
                      Monthly
                    </SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* Target */}
              <div className="space-y-2">
                <Label htmlFor="target" className="text-slate-200">
                  {getTargetLabel()} *
                </Label>
                <Input
                  id="target"
                  type="number"
                  min="1"
                  placeholder="Enter target value"
                  value={goal.target || ""}
                  onChange={(e) =>
                    setGoal({
                      ...goal,
                      target: Number.parseInt(e.target.value) || 0,
                    })
                  }
                  className="bg-slate-700 border-slate-600 text-white placeholder:text-slate-400"
                />
              </div>

              {/* Action Buttons */}
              <div className="flex flex-col sm:flex-row gap-3 pt-4">
                <Button
                  type="submit"
                  className="flex-1 bg-green-600 hover:bg-green-700 text-white"
                >
                  Update Goal
                </Button>
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleCancel}
                  className="flex-1 border-slate-600 text-slate-300 hover:bg-slate-700"
                >
                  Cancel
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
