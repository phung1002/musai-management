import axiosIns from "@/plugins/axios";
import { ILeaveTypes } from "@/types/type";
// 休暇管理
// 休暇リスト取得API呼び出し
export async function getLeaves(key: string): Promise<ILeaveTypes[]> {
  try {
    const response = await axiosIns.get<ILeaveTypes[]>("/leave-types", {
      params: { keyword: key },
    });
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
// 休暇タイプ取得API呼び出し
export async function getLeavesTree(): Promise<ILeaveTypes[]> {
  try {
    const response = await axiosIns.get<ILeaveTypes[]>("/leave-types/tree");
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
// 休暇追加API呼び出し
export async function addLeave(params: ILeaveTypes): Promise<void> {
  try {
    await axiosIns.post("/leave-types", params);
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
// 休暇更新API呼び出し
export async function updateLeave(
  id: number,
  params: ILeaveTypes
): Promise<void> {
  try {
    await axiosIns.put(`/leave-types/${id}`, params);
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
// 休暇削除API呼び出し
export async function deleteLeave(id: number): Promise<void> {
  try {
    console.log("Delete leave id:", id);
    await axiosIns.delete(`/leave-types/${id}`);
    console.log("Delete leave successfully");
  } catch (error: any) {
    if (error.response) {
      console.error("Delete leave failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}
