import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Activity, Apple, Target, TrendingUp, Zap, Flame } from "lucide-react"
import Link from "next/link"

export default function HomePage() {
  return (
    <div className="min-h-screen fitness-gradient">
      {/* Hero Section */}
      <div className="container mx-auto px-4 py-16">
        <div className="text-center mb-16">
          <h1 className="text-6xl font-bold mb-6">
            <span className="fitness-text-gradient">FitTracker</span> <span className="text-white">Pro</span>
          </h1>
          <p className="text-xl text-slate-300 mb-8 max-w-2xl mx-auto">
            Your complete fitness companion. Track workouts, monitor nutrition, set goals, and achieve your fitness
            dreams with our vibrant, eye-friendly interface.
          </p>
          <div className="flex gap-4 justify-center">
            <Button size="lg" className="fitness-button-primary">
              <Link href="/auth/signup" className="flex items-center gap-2">
                <Zap className="h-5 w-5" />
                Get Started
              </Link>
            </Button>
            <Button
              variant="outline"
              size="lg"
              className="border-fitness-electric text-fitness-electric hover:bg-fitness-electric hover:text-fitness-dark"
            >
              <Link href="/auth/login">Sign In</Link>
            </Button>
          </div>
        </div>

        {/* Features Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
          <Card className="fitness-card text-center group hover:fitness-glow transition-all duration-300">
            <CardHeader>
              <div className="mx-auto mb-4 p-3 rounded-full bg-fitness-electric/20 w-fit">
                <Activity className="h-8 w-8 text-fitness-electric" />
              </div>
              <CardTitle className="text-white">Workout Tracking</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-300">
                Log exercises, track sets, reps, and weights. Create custom workout templates with our intuitive
                interface.
              </CardDescription>
            </CardContent>
          </Card>

          <Card className="fitness-card text-center group hover:fitness-glow transition-all duration-300">
            <CardHeader>
              <div className="mx-auto mb-4 p-3 rounded-full bg-fitness-success/20 w-fit">
                <Apple className="h-8 w-8 text-fitness-success" />
              </div>
              <CardTitle className="text-white">Nutrition Logging</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-300">
                Track your meals and calories with vibrant progress indicators. Monitor your daily nutrition intake.
              </CardDescription>
            </CardContent>
          </Card>

          <Card className="fitness-card text-center group hover:fitness-glow transition-all duration-300">
            <CardHeader>
              <div className="mx-auto mb-4 p-3 rounded-full bg-fitness-energy/20 w-fit">
                <Target className="h-8 w-8 text-fitness-energy" />
              </div>
              <CardTitle className="text-white">Goal Setting</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-300">
                Set daily, weekly, or monthly fitness and nutrition goals with energetic progress tracking.
              </CardDescription>
            </CardContent>
          </Card>

          <Card className="fitness-card text-center group hover:fitness-glow transition-all duration-300">
            <CardHeader>
              <div className="mx-auto mb-4 p-3 rounded-full bg-fitness-warning/20 w-fit">
                <TrendingUp className="h-8 w-8 text-fitness-warning" />
              </div>
              <CardTitle className="text-white">Progress Analytics</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="text-slate-300">
                Visualize your progress with detailed charts and vibrant statistics in our eye-friendly dark interface.
              </CardDescription>
            </CardContent>
          </Card>
        </div>

        {/* CTA Section */}
        <div className="text-center fitness-card p-8 shadow-2xl border border-fitness-electric/30">
          <h2 className="text-4xl font-bold text-white mb-4">
            Ready to <span className="fitness-text-gradient">Transform</span> Your Fitness Journey?
          </h2>
          <p className="text-slate-300 mb-6 text-lg">
            Join thousands of users who have achieved their fitness goals with our vibrant, eye-friendly interface.
          </p>
          <Button size="lg" className="fitness-button-energy">
            <Link href="/auth/signup" className="flex items-center gap-2">
              <Flame className="h-5 w-5" />
              Start Your Journey
            </Link>
          </Button>
        </div>
      </div>
    </div>
  )
}
