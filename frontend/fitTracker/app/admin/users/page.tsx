"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Search, Plus, Edit, Trash2, Shield, User, ArrowLeft } from "lucide-react"
import Link from "next/link"
import { DashboardLayout } from "@/components/dashboard-layout"
import axios from "axios"

export default function AdminUsersPage() {
  const [searchTerm, setSearchTerm] = useState("")
  const [roleFilter, setRoleFilter] = useState("all")

  const [users, setUsers] = useState([])

useEffect(() => {
  const fetchUsers = async () => {
    try {
      const token = localStorage.getItem("token") // Adjust key if needed
      if (!token) {
        console.error("No token found in localStorage")
        return
      }

      const response = await axios.get("http://localhost:8000/auth/api/v1/user", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })

      setUsers(response.data)
    } catch (error) {
      console.error("Error fetching users:", error)
    }
  }

  fetchUsers()
}, [])


  const filteredUsers = users.filter((user) => {
    const matchesSearch =
      user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.handle.toLowerCase().includes(searchTerm.toLowerCase())

    const matchesRole = roleFilter === "all" || user.role === roleFilter

    return matchesSearch && matchesRole
  })

  const getRoleColor = (role: string) => {
    switch (role) {
      case "ADMIN":
        return "bg-fitness-energy/20 text-fitness-energy"
      case "USER":
        return "bg-fitness-electric/20 text-fitness-electric"
      default:
        return "bg-slate-600/20 text-slate-400"
    }
  }

  const handleDeleteUser = async (userHandle: string) => {
    if (!window.confirm(`Are you sure you want to delete user with handle: ${userHandle}? This action cannot be undone.`)) {
      return; // User cancelled the deletion
    }

    try {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("No token found. Cannot perform delete.");
        router.push("/login");
        return;
      }

      await axios.delete(`http://localhost:8000/auth/api/v1/user/${userHandle}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      // Update the local state to remove the deleted user
      setUsers(prevUsers => prevUsers.filter(user => user.handle !== userHandle));
      console.log(`User ${userHandle} deleted successfully.`);
      // Optionally, add a success notification here (e.g., using a toast library)

    } catch (error) {
      console.error(`Error deleting user ${userHandle}:`, error);
      // More robust error handling for delete:
      if (axios.isAxiosError(error) && error.response) {
          if (error.response.status === 401 || error.response.status === 403) {
              console.error("Authentication error during delete. Redirecting to login.");
              router.push("/login");
          } else if (error.response.status === 404) {
              console.error("User not found.");
          } else {
              console.error("Server error during delete:", error.response.data);
          }
      } else {
          console.error("An unexpected error occurred during delete.");
      }
      // Optionally, add an error notification here
    }
  };

  return (
    <DashboardLayout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Button variant="ghost" size="sm" asChild className="text-slate-400 hover:text-white">
                <Link href="/admin">
                  <ArrowLeft className="h-4 w-4 mr-1" />
                  Back to Admin
                </Link>
              </Button>
            </div>
            <h1 className="text-4xl font-bold">
              <span className="fitness-text-gradient">User Management</span>
            </h1>
            <p className="text-slate-400">Manage user accounts, roles, and permissions</p>
          </div>
        </div>

        {/* Filters */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
            <Input
              placeholder="Search users..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 bg-slate-800/50 border-slate-700 text-white"
            />
          </div>

          <Select value={roleFilter} onValueChange={setRoleFilter}>
            <SelectTrigger className="bg-slate-800/50 border-slate-700 text-white">
              <SelectValue placeholder="Filter by role" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Roles</SelectItem>
              <SelectItem value="USER">Users</SelectItem>
              <SelectItem value="ADMIN">Admins</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Users Table */}
        <Card className="fitness-card">
          <CardHeader>
            <CardTitle className="text-white">Users ({filteredUsers.length})</CardTitle>
            <CardDescription className="text-slate-400">Manage all user accounts in the system</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table>
                <TableHeader>
                  <TableRow className="border-slate-700">
                    <TableHead className="text-slate-300">User</TableHead>
                    <TableHead className="text-slate-300">Role</TableHead>
                    <TableHead className="text-slate-300">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredUsers.map((user) => (
                    <TableRow key={user.id} className="border-slate-700 hover:bg-slate-800/30">
                      <TableCell>
                        <div className="flex items-center gap-3">
                          <div className="h-8 w-8 rounded-full bg-gradient-to-r from-fitness-electric to-fitness-success flex items-center justify-center">
                            <User className="h-4 w-4 text-fitness-dark" />
                          </div>
                          <div>
                            <p className="text-xs text-slate-400">{user.email}</p>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <Badge className={getRoleColor(user.role)}>
                          {user.role === "ADMIN" && <Shield className="h-3 w-3 mr-1" />}
                          {user.role}
                        </Badge>
                      </TableCell>
                      <TableCell>
                        <div className="flex gap-2">
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-fitness-electric hover:bg-fitness-electric/20"
                            asChild // Important: tells Button to render as its child (Link)
                          >
                            <Link href={`/admin/users/${user.handle}/edit`}>
                              <Edit className="h-4 w-4" />
                            </Link>
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            className="text-red-400 hover:bg-red-500/20"
                            onClick={() => handleDeleteUser(user.handle)} // Attach onClick handler
                          >
                            <Trash2 className="h-4 w-4" />
                          </Button>
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
  )
}
