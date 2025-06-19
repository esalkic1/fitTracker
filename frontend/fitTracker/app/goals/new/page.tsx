"use client";

import type React from "react";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
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

interface GoalFormData {
  type: string;
  frequency: string;
  target: number;
}

export default function NewGoalPage() {
  const router = useRouter();
  const [formData, setFormData] = useState<GoalFormData>({
    type: "",
    frequency: "",
    target: 0,
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const authHeaders = () => {
    const user = JSON.parse(localStorage.getItem("user")!);
    return {
      "Content-Type": "application/json",
      "X-Handle": user.handle || "",
      "X-Role": user.role || "",
    };
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const user = JSON.parse(localStorage.getItem("user")!);

    const data = {
      type: formData.type,
      frequency: formData.frequency,
      target: formData.target,
      userHandle: user.handle,
    };

    console.log(data);

    try {
      const response = await fetch(
        `http://localhost:8000/notification-service/api/v1/goal?user_handle=${user.handle}`,
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
      console.log("Goal created successfully:", result);
      router.push("/goals");
    } catch (error) {
      console.error("Network or server error:", error);
    }
  };

  const handleCancel = () => {
    router.push("/goals");
  };

  return (
    <div className="container mx-auto py-8 px-4 max-w-2xl">
      {/* Header */}
      <div className="flex items-center gap-4 mb-8">
        <Button variant="ghost" size="sm" asChild>
          <Link href="/goals">
            <ArrowLeft className="h-4 w-4" />
          </Link>
        </Button>
        <div>
          <h1 className="text-3xl font-bold">Create New Goal</h1>
          <p className="text-muted-foreground mt-1">
            Set a goal that will be checked periodically
          </p>
        </div>
      </div>

      {/* Form Card */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Target className="h-5 w-5 text-blue-600" />
            Goal Details
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Error Message */}
            {error && (
              <div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg">
                <p className="text-destructive text-sm">{error}</p>
              </div>
            )}

            {/* Goal Type */}
            <div className="space-y-2">
              <Label htmlFor="type">Goal Type *</Label>
              <Select
                value={formData.type}
                onValueChange={(value) =>
                  setFormData((prev) => ({ ...prev, type: value }))
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select goal type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="WORKOUT">Workout</SelectItem>
                  <SelectItem value="NUTRITION">Nutrition</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Frequency */}
            <div className="space-y-2">
              <Label htmlFor="frequency">Frequency *</Label>
              <Select
                value={formData.frequency}
                onValueChange={(value) =>
                  setFormData((prev) => ({ ...prev, frequency: value }))
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select frequency" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="DAILY">Daily</SelectItem>
                  <SelectItem value="WEEKLY">Weekly</SelectItem>
                  <SelectItem value="MONTHLY">Monthly</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Target */}
            <div className="space-y-2">
              <Label htmlFor="target">Target *</Label>
              <Input
                id="target"
                type="number"
                min="1"
                placeholder="Enter target value"
                value={formData.target}
                onChange={(e) =>
                  setFormData((prev: any) => ({
                    ...prev,
                    target: e.target.value,
                  }))
                }
              />
              <p className="text-xs text-muted-foreground">
                {formData.type === "WORKOUT"
                  ? "Number of workouts to complete"
                  : formData.type === "NUTRITION"
                    ? "Calories or meals target"
                    : "Target value for your goal"}
              </p>
            </div>

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row gap-3 pt-6">
              <Button
                type="submit"
                disabled={
                  isSubmitting ||
                  !formData.type ||
                  !formData.frequency ||
                  !formData.target ||
                  Number(formData.target) <= 0
                }
                className="flex-1 bg-green-600 hover:bg-green-700"
              >
                {isSubmitting ? "Saving Goal..." : "Save Goal"}
              </Button>
              <Button
                type="button"
                variant="outline"
                onClick={handleCancel}
                className="flex-1"
                disabled={isSubmitting}
              >
                Cancel
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
