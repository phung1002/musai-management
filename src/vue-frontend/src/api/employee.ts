import { IEmployee } from "@/types/type";
import axiosIns from "@/plugins/axios";

// call to api list employee
export async function getAllEmployees(key: string): Promise<IEmployee[]> {
  try {
    const response = await axiosIns.get<IEmployee[]>("/employees", {
      params: { keyword: key },
    });
    return response.data;
  } catch (error) {
    console.error("List employee failed:", error);
    throw error;
  }
}

export async function getAllMembers(key: string): Promise<IEmployee[]> {
  try {
    const response = await axiosIns.get<IEmployee[]>("/employees/list-member", {
      params: { keyword: key },
    });
    return response.data;
  } catch (error) {
    console.error("List employee failed:", error);
    throw error;
  }
}
// call to api create employee
export async function createEmployee(params: IEmployee): Promise<void> {
  try {
    await axiosIns.post("/employees", params);
  } catch (error: any) {
    console.error("Add employee failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// call to api update employee
export async function updateEmployee(
  id: number,
  params: IEmployee
): Promise<void> {
  try {
    await axiosIns.put(`/employees/${id}`, params);
  } catch (error: any) {
    console.error("Update employee failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// call to api delete employee
export async function deleteEmployee(id: number): Promise<void> {
  try {
    await axiosIns.delete(`/employees/${id}`);
  } catch (error: any) {
    console.error("Delete employee failed:", error);
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
    await axiosIns.put(`/employees/change-password`, payload);
  } catch (error: any) {
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
};
