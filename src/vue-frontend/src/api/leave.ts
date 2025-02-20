import axiosIns from "@/plugins/axios";
import { ILeaveTypes } from "@/types/type";

// 休暇リスト取得API呼び出し
export async function getLeaves(): Promise<ILeaveTypes[]> {
  try {
    const response = await axiosIns.get<ILeaveTypes[]>("/leave-types/list");
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
// 休暇削除API呼び出し
export async function deleteLeave(id: number): Promise<void> {
  try {
    console.log("Delete leave id:", id);
    await axiosIns.delete(`/leave-types/delete/${id}`);
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
