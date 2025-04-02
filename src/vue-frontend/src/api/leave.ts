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
  } catch (error : any) {
    console.error("List leave failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// 休暇タイプ取得API呼び出し
export async function getLeavesTree(): Promise<ILeaveTypes[]> {
  try {
    const response = await axiosIns.get<ILeaveTypes[]>("/leave-types/tree");
    return response.data;
  } catch (error : any) {
    console.error("List leave failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// 休暇追加API呼び出し
export async function addLeave(params: ILeaveTypes): Promise<void> {
  try {
    await axiosIns.post("/leave-types", params);
  } catch (error: any) {
    console.error("Add leave failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// 休暇更新API呼び出し
export async function updateLeave(
  id: number,
  params: ILeaveTypes
): Promise<void> {
  try {
    await axiosIns.put(`/leave-types/${id}`, params);
  } catch (error: any) {
    console.error("Update leave failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
// 休暇削除API呼び出し
export async function deleteLeave(id: number): Promise<void> {
  try {
    await axiosIns.delete(`/leave-types/${id}`);
  } catch (error: any) {
    console.error("Delete leave failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
