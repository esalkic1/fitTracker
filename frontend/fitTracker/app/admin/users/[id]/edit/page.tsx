"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation" // Standard import for App Router
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Skeleton } from "@/components/ui/skeleton"
import { ArrowLeft, Loader2, Save } from "lucide-react"
import axios from "axios" // Import axios for API calls

// Define the type for the user object based on your backend response
interface UserData {
  id?: number; // Optional, as handle is the primary lookup.
  handle: string; // This is the UUID from the backend
  email: string;
  role: "USER" | "ADMIN";
  // Add other fields if your backend User object has them and they are relevant
}

// Updated component props: Expect 'id' in params, which will contain the UUID handle
export default function EditUserPage({ params }: { params: { id: string } }) {
  // The 'id' from params is actually the user's handle (UUID)
  const userHandle = params.id;

  // Initialize user state with the handle from URL params
  const [user, setUser] = useState<UserData>({
    handle: userHandle, // Set initial handle from URL parameter
    email: "",
    role: "USER",
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [fetchError, setFetchError] = useState<string | null>(null); // State for errors during initial fetch
  const [updateError, setUpdateError] = useState<string | null>(null); // State for errors during update
  const router = useRouter();

  // --- Fetch User Data from Backend API ---
  useEffect(() => {
    const fetchUserData = async () => {
      setLoading(true);
      setFetchError(null); // Clear any previous fetch errors

      // Ensure userHandle is available before attempting to fetch
      if (!userHandle) {
        setLoading(false);
        setFetchError("User handle not provided in the URL.");
        // Consider redirecting if handle is critically missing:
        // router.push("/admin/users");
        return;
      }

      try {
        const token = localStorage.getItem("token"); // Get bearer token from localStorage
        if (!token) {
          console.error("No token found in localStorage. Redirecting to login.");
          router.push("/login"); // Redirect if not authenticated
          return;
        }

        // Make the API call to your backend using axios, using the userHandle
        const response = await axios.get<UserData>(`http://localhost:8000/auth/api/v1/user/${userHandle}`, {
          headers: {
            Authorization: `Bearer ${token}`, // Include the bearer token
          },
        });

        // Set the user state with the fetched data
        setUser(response.data);
      } catch (error) {
        console.error("Error fetching user data:", error);
        if (axios.isAxiosError(error)) {
          // Handle specific HTTP errors
          if (error.response?.status === 404) {
            setFetchError("User not found.");
          } else if (error.response?.status === 401 || error.response?.status === 403) {
            setFetchError("Unauthorized. Please log in again.");
            router.push("/login"); // Redirect on authentication/authorization issues
          } else {
            // General server error message
            setFetchError(error.response?.data?.message || "Failed to fetch user data.");
          }
        } else {
          // Network or unexpected error
          setFetchError("An unexpected error occurred while fetching user data.");
        }
      } finally {
        setLoading(false); // Always set loading to false after attempt
      }
    };

    fetchUserData();
  }, [userHandle, router]); // Re-run effect if userHandle changes or router instance changes

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    // Validate email
    if (!user.email.trim()) {
      newErrors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(user.email)) {
      newErrors.email = "Please enter a valid email address";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setUpdateError(null); // Clear previous update errors

    if (!validateForm()) {
      return;
    }

    setSaving(true);

    try {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("No token found. Cannot perform update.");
        router.push("/login");
        return;
      }

      // Construct the request body as per your backend's UserUpdateRequest DTO
      const requestBody = {
        email: user.email,
        role: user.role,
        // Only include fields that are part of your UserUpdateRequest DTO
      };

      // Send PUT request to the update route using the user's handle
      // Use user.handle which was correctly initialized from params.id
      await axios.put(`http://localhost:8000/auth/api/v1/user/${user.handle}`, requestBody, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      console.log("User updated successfully:", user);
      router.push("/admin/users"); // Redirect back to users list on success
    } catch (error) {
      console.error("Error updating user:", error);
      if (axios.isAxiosError(error)) {
        setUpdateError(error.response?.data?.message || "Failed to update user data.");
        if (error.response?.status === 401 || error.response?.status === 403) {
            router.push("/login"); // Redirect on authentication/authorization issues
        }
      } else {
        setUpdateError("An unexpected error occurred while updating user data.");
      }
    } finally {
      setSaving(false); // Always set saving to false after attempt
    }
  };

  // Type-safe handling of input changes
  const handleInputChange = (field: keyof UserData, value: string) => {
    setUser((prev) => ({ ...prev, [field]: value }));

    // Clear error when user starts typing
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: "" }));
    }
  };

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="max-w-2xl mx-auto">
          <div className="flex items-center gap-4 mb-8">
            <Skeleton className="h-8 w-8" />
            <Skeleton className="h-8 w-48" />
          </div>
          <Card>
            <CardHeader>
              <Skeleton className="h-6 w-32" />
              <Skeleton className="h-4 w-64" />
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Skeleton className="h-4 w-16" />
                  <Skeleton className="h-10 w-full" />
                </div>
                <div className="space-y-2">
                  <Skeleton className="h-4 w-16" />
                  <Skeleton className="h-10 w-full" />
                </div>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Skeleton className="h-4 w-16" />
                  <Skeleton className="h-10 w-full" />
                </div>
                <div className="space-y-2">
                  <Skeleton className="h-4 w-16" />
                  <Skeleton className="h-10 w-full" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    );
  }

  // Display fetch error if any
  if (fetchError) {
    return (
      <div className="container mx-auto px-4 py-8 text-center text-red-500">
        <h2 className="text-xl font-bold mb-4">Error Loading User</h2>
        <p>{fetchError}</p>
        <Button onClick={() => router.push("/admin/users")} className="mt-4">
          Go Back to Users
        </Button>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="flex items-center gap-4 mb-8">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => router.push("/admin/users")}
            className="flex items-center gap-2"
          >
            <ArrowLeft className="h-4 w-4" />
            Back to Users
          </Button>
          <div>
            <h1 className="text-2xl font-bold">Edit User ({user.handle})</h1>
            <p className="text-muted-foreground">Update user information and settings</p>
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          <Card>
            <CardHeader>
              <CardTitle>User Information</CardTitle>
              <CardDescription>Update the user's basic information and account settings</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
                {/* Display update error if any */}
                {updateError && (
                    <div className="bg-red-500/20 text-red-400 p-3 rounded-lg mb-4">
                        <p>{updateError}</p>
                    </div>
                )}
              {/* Basic Information */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="email">Email Address</Label>
                  <Input
                    id="email"
                    type="email"
                    value={user.email}
                    onChange={(e) => handleInputChange("email", e.target.value)}
                    placeholder="Enter email address"
                    className={errors.email ? "border-red-500" : ""}
                  />
                  {errors.email && <p className="text-sm text-red-500">{errors.email}</p>}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="role">Role</Label>
                  <Select
                    value={user.role}
                    onValueChange={(value: "USER" | "ADMIN") => handleInputChange("role", value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="USER">User</SelectItem>
                      <SelectItem value="ADMIN">Admin</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </CardContent>
            <CardFooter className="flex justify-between">
              <Button type="button" variant="outline" onClick={() => router.push("/admin/users")}>
                Cancel
              </Button>
              <Button type="submit" disabled={saving} className="bg-blue-600 hover:bg-blue-700">
                {saving ? (
                  <>
                    <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                    Updating...
                  </>
                ) : (
                  <>
                    <Save className="h-4 w-4 mr-2" />
                    Update User
                  </>
                )}
              </Button>
            </CardFooter>
          </Card>
        </form>
      </div>
    </div>
  );
}
