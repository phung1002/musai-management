import axiosIns from "@/plugins/axios";
import { ILeaveTypes, IUserLeaves } from "@/types/type";
// 社員休暇管理
// 社員休暇リスト取得
export async function getUserLeaves(): Promise<IUserLeaves[]> {
  try {
    const response = await axiosIns.get<IUserLeaves[]>("/user-leaves/all");
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
    console.log("response.data", response.data);

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
    await axiosIns.put(`/user-leaves/update/${id}`, params);
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
// 社員休暇検索API呼び出し
export async function searchUserLeave(key: string): Promise<IUserLeaves[]> {
  try {
    const response = await axiosIns.get<IUserLeaves[]>("/user-leaves/search", {
      params: { keyword: key },
    });
    return response.data;
  } catch (error) {
    console.error("Search Userleave failed:", error);
    throw error;
  }
}
// 社員休暇追加API呼び出し
export async function addUserLeave(params: IUserLeaves): Promise<void> {
  try {
    await axiosIns.post("/user-leaves/add", params);
    console.log("Add leave successfully");
  } catch (error: any) {
    if (error.response) {
      console.error("Add leave failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}
