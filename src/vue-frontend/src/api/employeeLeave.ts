import axiosIns from "@/plugins/axios";
import { IEmployeeLeaves } from "@/types/type";
// 社員休暇管理
// 社員休暇リスト取得
export async function getEmployeeLeaves(
  key: string
): Promise<IEmployeeLeaves[]> {
  try {
    const response = await axiosIns.get<IEmployeeLeaves[]>(
      "/employee-leaves/all",
      {
        params: { keyword: key },
      }
    );
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
export async function getEmployeeLeavesForMember(): Promise<IEmployeeLeaves[]> {
  try {
    const response = await axiosIns.get<IEmployeeLeaves[]>("/employee-leaves");
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
// 休社員休暇更新API呼び出し
export async function updateEmployeeLeave(
  id: number,
  params: IEmployeeLeaves
): Promise<void> {
  try {
    await axiosIns.put(`/employee-leaves/${id}`, params);
  } catch (error: any) {
    console.error("Update employee failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// 社員休暇追加API呼び出し
export async function addEmployeeLeave(params: IEmployeeLeaves): Promise<void> {
  try {
    await axiosIns.post("/employee-leaves", params);
  } catch (error: any) {
    console.error("Add leave failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
