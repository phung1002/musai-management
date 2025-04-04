import axiosIns from "@/plugins/axios";
import { useUserStore } from "@/store/userStore";

interface LoginParams {
  username: string;
  password: string;
}

// Function to call the login API
export async function login(params: LoginParams): Promise<void> {
  try {
    await axiosIns.post("/auth/login", params);

    // Fetch user profile after successful login
    await fetchUserProfile();
  } catch (error: any) {
    console.error("Login failed:", error.response?.data || error);
    throw error;
  }
}

// Fetch the current user's profile after login or page reload
export async function fetchUserProfile(): Promise<void> {
  const userStore = useUserStore();
  try {
    const response = await axiosIns.get("/auth/profile");
    const data = response.data;

    userStore.setAuthenticated(true);
    userStore.setId(data.id);
    userStore.setRoles(data.roles);
    userStore.setUsername(data.username);
    userStore.setFullName(data.fullName);
    userStore.setGender(data.gender);
  } catch (error:any) {
    console.error("Failed to fetch user profile:", error);

    // If the error is 401, the session might have expired, trigger logout
    if (error.response?.status === 401) {
      await logout();
    }
  }
}

// Function to call the logout API
export async function logout(): Promise<void> {
  const userStore = useUserStore();
  try {
    await axiosIns.post("/auth/logout");

    // Reset user state after logout
    userStore.setId("");
    userStore.setAuthenticated(false);
    userStore.setRoles([]);
    userStore.setUsername("");
    userStore.setFullName("");
    userStore.setGender("");
  } catch (error) {
    console.error("Logout failed:", error);
  }
}

// API validate authentication (check if token is valid)
export async function validate() {
  try {
    const response = await axiosIns.get("/auth/validate");
    return response;
  } catch (error) {
    console.error("Token validation failed:", error);
    throw error;
  }
}

// Helper function to validate token
export async function validateToken() {
  try {
    await validate();
    return true;
  } catch (error) {
    return false;
  }
}

// Helper function to handle logout
export async function handleLogout() {
  await logout();
  window.location.href = "/login";
}

// Get user profile data (returns the data instead of the full response)
export async function profile() {
  try {
    const response = await axiosIns.get("/auth/profile");
    return response.data;
  } catch (error) {
    console.error("Failed to fetch profile:", error);
    throw error;
  }
}
