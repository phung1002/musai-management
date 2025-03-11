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
// 申請確認レスポンス返しAPI呼び出し
export async function updateLeaveRespond(
  id: number,
  status: string
): Promise<void> {
  try {
    await axiosIns.put(`leave-applications/respond-to-leave/${id}`, { status });
  } catch (error: any) {
    console.error("Update leave application respond failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
