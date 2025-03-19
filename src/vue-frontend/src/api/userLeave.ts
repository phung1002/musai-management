import axiosIns from "@/plugins/axios";
import { IUserLeaves } from "@/types/type";
// 社員休暇管理
// 社員休暇リスト取得
export async function getUserLeaves(key: string): Promise<IUserLeaves[]> {
  try {
    const response = await axiosIns.get<IUserLeaves[]>("/user-leaves/all", {
      params: { keyword: key },
    });
    console.log("response.data", response.data);

    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
export async function getUserLeavesForMember(): Promise<IUserLeaves[]> {
  try {
    const response = await axiosIns.get<IUserLeaves[]>("/user-leaves");
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
// 休社員休暇更新API呼び出し
export async function updateUserLeave(
  id: number,
  params: IUserLeaves
): Promise<void> {
  try {
    await axiosIns.put(`/user-leaves/${id}`, params);
    console.log("Update user successfully");
  } catch (error: any) {
    console.error("Update user failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// 社員休暇追加API呼び出し
export async function addUserLeave(params: IUserLeaves): Promise<void> {
  try {
    await axiosIns.post("/user-leaves", params);
    console.log("Add leave successfully");
  } catch (error: any) {
    console.error("Add leave failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
