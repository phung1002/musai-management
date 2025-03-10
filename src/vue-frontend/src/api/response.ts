import { ILeaveResponse } from "@/types/type";
import axiosIns from "@/plugins/axios";

// 申請確認リストAPI呼び出し
export async function getLeaveRequests(): Promise<ILeaveResponse[]> {
  try {
    const response = await axiosIns.get<ILeaveResponse[]>(
      "/leave-applications"
    );
    return response.data;
  } catch (error) {
    console.error("List applications failed:", error);
    throw error;
  }
}
// 申請確認検索API呼び出し
export async function searchLeaveRequest(
  key: string
): Promise<ILeaveResponse[]> {
  try {
    const response = await axiosIns.get<ILeaveResponse[]>(
      "/leave-applications///",
      {
        params: { keyword: key },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Search applications failed:", error);
    throw error;
  }
}
