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
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Progress } from "@/components/ui/progress";
import { Badge } from "@/components/ui/badge";
import {
  ArrowLeft,
  Target,
  TrendingUp,
  Edit,
  Trash2,
  Plus,
  Minus,
  CheckCircle,
  Trophy,
  Clock,
} from "lucide-react";
import Link from "next/link";
import { useRouter, useParams } from "next/navigation";
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

interface Goal {
  id: string;
  type: string;
  frequency: string;
  target: number;
}

function capitalize(str: string) {
  if (!str) return str;
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export default function GoalDetailsPage() {
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
  const [error, setError] = useState<string | null>(null);
  const [progressInput, setProgressInput] = useState("0");

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

  const handleProgressUpdate = async (newProgress: number) => {
    if (!goal) return;

    setError(null);

    try {
      const updatedGoal = await goalsService.updateProgress(
        goal.id,
        newProgress,
      );
      if (updatedGoal) {
        setGoal(updatedGoal);
        setProgressInput(updatedGoal.current.toString());
      }
    } catch (error) {
      console.error("Error updating progress:", error);
      setError(error.message);
    }
  };

  const handleProgressInputChange = async () => {
    const newProgress = Number.parseInt(progressInput);
    if (isNaN(newProgress) || newProgress < 0) {
      setError("Please enter a valid number");
      return;
    }

    await handleProgressUpdate(newProgress);
  };

  const handleDelete = async () => {
    try {
      const res = await fetch(
        `http://localhost:8000/notification-service/api/v1/goal/${goalId}`,
        {
          method: "DELETE",
          headers: authHeaders(),
        },
      );

      if (!res.ok) {
        throw new Error(`Failed to delete meal: ${res.statusText}`);
      }

      console.log("Goal deleted successfully:", goalId);
      router.push("/goals");
    } catch (err) {
      console.error(err);
      alert(`Error deleting goal: ${err.message}`);
    }
  };

  const getProgressPercentage = () => {
    if (!goal) return 0;
    return Math.min((goal.current / goal.target) * 100, 100);
  };

  const getProgressColor = () => {
    const percentage = getProgressPercentage();
    if (percentage >= 100) return "bg-green-500";
    if (percentage >= 75) return "bg-blue-500";
    if (percentage >= 50) return "bg-yellow-500";
    return "bg-red-500";
  };

  const getGoalTypeColor = (type: string) => {
    return type === "WORKOUT"
      ? "bg-blue-100 text-blue-800"
      : "bg-green-100 text-green-800";
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 p-4 md:p-6">
      <div className="max-w-4xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <Button
              variant="ghost"
              size="sm"
              asChild
              className="text-white hover:bg-slate-800"
            >
              <Link href="/goals">
                <ArrowLeft className="h-4 w-4" />
              </Link>
            </Button>
            <div>
              <h1 className="text-2xl md:text-3xl font-bold text-white">
                {`View ${goal.type.toLowerCase()} goal`}
              </h1>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Badge className={getGoalTypeColor(goal.type)}>
              {goal.type.toLowerCase()}
            </Badge>
            <Badge
              variant="outline"
              className="text-slate-300 border-slate-600"
            >
              {goal.frequency.toLowerCase()}
            </Badge>
          </div>
        </div>

        {error && (
          <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-lg">
            <p className="text-red-400 text-sm">{error}</p>
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Content */}
          <div className="lg:col-span-2 space-y-6">
            {/* Progress Card */}
            <Card className="bg-slate-800/50 border-slate-700">
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-white">
                  <TrendingUp className="h-5 w-5 text-blue-400" />
                  Progress Tracking
                </CardTitle>
                <CardDescription className="text-slate-400">
                  Track your progress towards completing this goal
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {/* Progress Bar */}
                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span className="text-slate-300">Current Progress</span>
                    <span className="text-white font-medium">
                      {goal.current} / {goal.target} (
                      {Math.round(getProgressPercentage())}%)
                    </span>
                  </div>
                  <Progress value={getProgressPercentage()} className="h-3" />
                </div>

                {/* Progress Controls */}
                <div className="space-y-4">
                  <div className="flex items-center gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() =>
                        handleProgressUpdate(Math.max(0, goal.current - 1))
                      }
                      className="border-slate-600 text-slate-300 hover:bg-slate-700"
                    >
                      <Minus className="h-4 w-4" />
                    </Button>
                    <div className="flex-1">
                      <Label htmlFor="progress" className="sr-only">
                        Current Progress
                      </Label>
                      <Input
                        id="progress"
                        type="number"
                        min="0"
                        max={goal.target}
                        value={progressInput}
                        onChange={(e) => setProgressInput(e.target.value)}
                        onBlur={handleProgressInputChange}
                        onKeyDown={(e) => {
                          if (e.key === "Enter") {
                            handleProgressInputChange();
                          }
                        }}
                        className="bg-slate-700 border-slate-600 text-white text-center"
                      />
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleProgressUpdate(goal.current + 1)}
                      className="border-slate-600 text-slate-300 hover:bg-slate-700"
                    >
                      <Plus className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>

            {/* Goal Details */}
            <Card className="bg-slate-800/50 border-slate-700">
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-white">
                  <Target className="h-5 w-5 text-green-400" />
                  Goal Details
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label className="text-slate-400">Type</Label>
                    <p className="text-white font-medium">
                      {capitalize(goal.type)}
                    </p>
                  </div>
                  <div>
                    <Label className="text-slate-400">Frequency</Label>
                    <p className="text-white font-medium">
                      {capitalize(goal.frequency)}
                    </p>
                  </div>
                  <div>
                    <Label className="text-slate-400">Target</Label>
                    <p className="text-white font-medium">{goal.target}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Sidebar */}
          <div className="space-y-6">
            {/* Quick Stats */}
            <Card className="bg-slate-800/50 border-slate-700">
              <CardHeader>
                <CardTitle className="text-white">Quick Stats</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Progress</span>
                  <span className="text-white font-bold">
                    {Math.round(getProgressPercentage())}%
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Remaining</span>
                  <span className="text-white font-bold">
                    {Math.max(0, goal.target - goal.current)}
                  </span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Status</span>
                  <span
                    className={`font-bold ${goal.isCompleted ? "text-green-400" : "text-yellow-400"}`}
                  >
                    {goal.isCompleted ? "Complete" : "In Progress"}
                  </span>
                </div>
              </CardContent>
            </Card>

            {/* Actions */}
            <Card className="bg-slate-800/50 border-slate-700">
              <CardHeader>
                <CardTitle className="text-white">Actions</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                <Button asChild className="w-full" variant="outline">
                  <Link href={`/goals/${goalId}/edit`}>
                    <Edit className="h-4 w-4 mr-2" />
                    Edit Goal
                  </Link>
                </Button>

                <AlertDialog>
                  <AlertDialogTrigger asChild>
                    <Button variant="destructive" className="w-full">
                      <Trash2 className="h-4 w-4 mr-2" />
                      Delete Goal
                    </Button>
                  </AlertDialogTrigger>
                  <AlertDialogContent className="bg-slate-800 border-slate-700">
                    <AlertDialogHeader>
                      <AlertDialogTitle className="text-white">
                        Delete Goal
                      </AlertDialogTitle>
                      <AlertDialogDescription className="text-slate-400">
                        Are you sure you want to delete this goal? This action
                        cannot be undone.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel className="border-slate-600 text-slate-300 hover:bg-slate-700">
                        Cancel
                      </AlertDialogCancel>
                      <AlertDialogAction
                        onClick={handleDelete}
                        className="bg-red-600 hover:bg-red-700 text-white"
                      >
                        Delete Goal
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
}
