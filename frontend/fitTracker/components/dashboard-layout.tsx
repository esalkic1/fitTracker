"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet"
import { Activity, Apple, Target, User, Menu, Home, LogOut, Bell } from "lucide-react"
import Link from "next/link"
import { useRouter, usePathname } from "next/navigation"

const navigation = [
  { name: "Dashboard", href: "/dashboard", icon: Home, color: "text-fitness-electric" },
  { name: "Workouts", href: "/workouts", icon: Activity, color: "text-fitness-electric" },
  { name: "Nutrition", href: "/nutrition", icon: Apple, color: "text-fitness-success" },
  { name: "Goals", href: "/goals", icon: Target, color: "text-fitness-energy" },
]

export function DashboardLayout({ children }: { children: React.ReactNode }) {
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const [userEmail, setUserEmail] = useState<string | null>(null)
  const router = useRouter()
  const pathname = usePathname()

  const handleLogout = () => {
    localStorage.removeItem("user")
    localStorage.removeItem("token")
    router.push("/")
  }

  useEffect(() => {
    const storedUser = localStorage.getItem("user")
    if (storedUser) {
      try {
        const parsed = JSON.parse(storedUser)
        setUserEmail(parsed.email || "Unknown User")
      } catch (e) {
        console.error("Error parsing user from localStorage:", e)
      }
    }
  }, [])

  return (
    <div className="min-h-screen fitness-gradient">
      {/* Mobile sidebar */}
      <Sheet open={sidebarOpen} onOpenChange={setSidebarOpen}>
        <SheetContent side="left" className="w-64 fitness-card border-r border-slate-700">
          <div className="flex flex-col h-full">
            <div className="flex items-center px-4 py-6">
              <h2 className="text-lg font-semibold">
                <span className="fitness-text-gradient">FitTracker</span> <span className="text-white">Pro</span>
              </h2>
            </div>
            <nav className="flex-1 px-4 space-y-2">
              {navigation.map((item) => {
                const Icon = item.icon
                const isActive = pathname === item.href
                return (
                  <Link
                    key={item.name}
                    href={item.href}
                    className={`flex items-center px-3 py-2 rounded-md text-sm font-medium transition-all duration-200 ${
                      isActive
                        ? "bg-gradient-to-r from-fitness-electric/20 to-fitness-success/20 text-white border border-fitness-electric/30"
                        : "text-slate-300 hover:bg-slate-800/50 hover:text-white"
                    }`}
                    onClick={() => setSidebarOpen(false)}
                  >
                    <Icon className={`h-5 w-5 mr-3 ${isActive ? item.color : ""}`} />
                    {item.name}
                  </Link>
                )
              })}
            </nav>
            <div className="p-4">
              <Button
                variant="outline"
                className="w-full border-red-500 text-red-400 hover:bg-red-500 hover:text-white"
                onClick={handleLogout}
              >
                <LogOut className="h-4 w-4 mr-2" />
                Logout
              </Button>
            </div>
          </div>
        </SheetContent>

        {/* Desktop sidebar */}
        <div className="hidden lg:fixed lg:inset-y-0 lg:flex lg:w-64 lg:flex-col">
          <div className="flex flex-col flex-grow fitness-card border-r border-slate-700">
            <div className="flex items-center px-4 py-6">
              <h2 className="text-lg font-semibold">
                <span className="fitness-text-gradient">FitTracker</span> <span className="text-white">Pro</span>
              </h2>
            </div>
            <nav className="flex-1 px-4 space-y-2">
              {navigation.map((item) => {
                const Icon = item.icon
                const isActive = pathname === item.href
                return (
                  <Link
                    key={item.name}
                    href={item.href}
                    className={`flex items-center px-3 py-2 rounded-md text-sm font-medium transition-all duration-200 ${
                      isActive
                        ? "bg-gradient-to-r from-fitness-electric/20 to-fitness-success/20 text-white border border-fitness-electric/30"
                        : "text-slate-300 hover:bg-slate-800/50 hover:text-white"
                    }`}
                  >
                    <Icon className={`h-5 w-5 mr-3 ${isActive ? item.color : ""}`} />
                    {item.name}
                  </Link>
                )
              })}
            </nav>
            <div className="p-4">
              <Button
                variant="outline"
                className="w-full border-red-500 text-red-400 hover:bg-red-500 hover:text-white"
                onClick={handleLogout}
              >
                <LogOut className="h-4 w-4 mr-2" />
                Logout
              </Button>
            </div>
          </div>
        </div>

        {/* Main content */}
        <div className="lg:pl-64">
          {/* Top bar */}
          <div className="sticky top-0 z-40 fitness-card border-b border-slate-700 px-4 py-4 sm:px-6 lg:px-8 backdrop-blur-sm">
            <div className="flex items-center justify-between">
              <SheetTrigger asChild>
                <Button variant="ghost" size="sm" className="lg:hidden text-white hover:bg-slate-800">
                  <Menu className="h-6 w-6" />
                </Button>
              </SheetTrigger>
              <div className="flex items-center space-x-4">
                <Button variant="ghost" size="sm" className="text-fitness-warning hover:bg-slate-800">
                  <Bell className="h-5 w-5" />
                </Button>
                <div className="flex items-center space-x-2">
                  <div className="h-8 w-8 rounded-full bg-gradient-to-r from-fitness-electric to-fitness-success flex items-center justify-center">
                    <User className="h-4 w-4 text-fitness-dark" />
                  </div>
                  <span className="text-sm font-medium text-white">{userEmail ?? "Loading..."}</span>
                </div>
              </div>
            </div>
          </div>

          {/* Page content */}
          <main className="p-4 sm:p-6 lg:p-8">{children}</main>
        </div>
      </Sheet>
    </div>
  )
}
