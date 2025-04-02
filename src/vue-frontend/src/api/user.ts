import { IUser } from "@/types/type";
import axiosIns from "@/plugins/axios";

// call to api create user
export async function getAllUsers(key: string): Promise<IUser[]> {
  try {
    const response = await axiosIns.get<IUser[]>("/users", {
      params: { keyword: key },
    });
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}

// call to api create user
export async function createUser(params: IUser): Promise<void> {
  try {
    await axiosIns.post("/users", params);
  } catch (error: any) {
    console.error("Add user failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// call to api update user
export async function updateUser(id: number, params: IUser): Promise<void> {
  try {
    await axiosIns.put(`/users/${id}`, params);
  } catch (error: any) {
    console.error("Update user failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// call to api delete user
export async function deleteUser(id: number): Promise<void> {
  try {
    await axiosIns.delete(`/users/${id}`);
  } catch (error: any) {
    console.error("Delete user failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// パスワート変更API呼び出し
export interface ChangePasswordPayload {
  password: string;
  newPassword: string;
}
export const changePassword = async (
  payload: ChangePasswordPayload
): Promise<void> => {
  try {
    await axiosIns.put(`/users/change-password`, payload);
  } catch (error: any) {
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
};
