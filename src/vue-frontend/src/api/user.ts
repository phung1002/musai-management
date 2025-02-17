import { IAccessToken, IUser } from "@/types/type";
import axiosIns from "@/plugins/axios";

// call to api create user
export async function getAllUsers(): Promise<IUser[]> {
  try {
    const response = await axiosIns.get<IUser[]>("/user/list");
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}

// call to api create user
export async function createUser(params: IUser): Promise<void> {
  try {
    await axiosIns.post("/user/add", params);
    console.log("Add user successfully");
  } catch (error: any) {
    if (error.response) {
      console.error("Add user failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}
// call to api update user
export async function updateUser(id: number, params: IUser): Promise<void> {
  try {
    await axiosIns.put(`/user/edit/${id}`, params);
    console.log("Update user successfully");
  } catch (error: any) {
    if (error.response) {
      console.error("Update user failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}

// call to api delete user
export async function deleteUser(id: number): Promise<void> {
  try {
    await axiosIns.delete(`/user/delete/${id}`);
  } catch (error: any) {
    if (error.response) {
      console.error("Delete user failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}

export async function getProfile() {
  const options = {
    method: "GET",
    url: `/user/profile`,
    headers: {
      "Content-Type": "application/json",
    },
  };
  return axiosIns.request<IAccessToken>(options);
}


// call to api search user
export async function searchUser(key:string): Promise<IUser[]> {
  try {
    const response = await axiosIns.get<IUser[]>("/user/search", { params: { keyword: key } });
    return response.data;
  } catch (error) {
    console.error("Search user failed:", error);
    throw error;
  }
}
