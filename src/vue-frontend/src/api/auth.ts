import { IAccessToken } from "@/types/type";
import axiosIns from "@/plugins/axios";
import { useUserStore } from "@/store/userStore";

interface LoginParams {
  username: string;
  password: string;
}

//call to API login, POSt method
export async function login(params: LoginParams): Promise<IAccessToken> {
  try {
    // Send request to API login
    const response = await axiosIns.post<IAccessToken>("/auth/login", params);
    const userStore = useUserStore();
    const { data } = response;

    // update userStore
    userStore.setAuthenticated(true);
    userStore.setId(data.id);
    userStore.setRoles(data.roles);
    userStore.setUsername(data.username);
    userStore.setFullName(data.fullName);
    userStore.setGender(data.gender);

    // return data
    return response.data;
  } catch (error: any) {
    if (error.response) {
      console.error("Login failed:", error.response.data);
    } else {
      console.error("Network or unexpected error:", error);
    }
    throw error;
  }
}

// call to api logout
export async function logout() {
  const userStore = useUserStore();
  try {
    await axiosIns.post("/auth/logout");
    console.log("Logged out successfully");

    // Reset user state
    userStore.setAuthenticated(false);
    userStore.setRoles([]);
    userStore.setUsername("");
    userStore.setFullName("");
  } catch (error) {
    console.error("Logout failed", error);
  }
}

//api validate authentication
export async function validate() {
  try {
    const response = await axiosIns.get("/auth/validate", { timeout: 5000 });
    return response;
  } catch (error) {
    throw error;
  }
}

//api get profile
export async function profile() {
  try {
    const response = await axiosIns.get("/auth/profile", { timeout: 5000 });
    return response;
  } catch (error) {
    throw error;
  }
}
