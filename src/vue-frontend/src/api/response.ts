import { ILeaveResponse } from "@/types/type";
import axiosIns from "@/plugins/axios";

// 申請確認リストと検索API呼び出し
export async function allLeaveRequests(key: string): Promise<ILeaveResponse[]> {
  try {
    const response = await axiosIns.get<ILeaveResponse[]>(
      "/leave-applications/all",
      { params: { keyword: key } }
    );
    return response.data;
  } catch (error: any) {
    console.error("List leave application failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
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

export async function fetchApprovedLeaveRequests(): Promise<ILeaveResponse[]> {
  try {
    const response = await axiosIns.get<ILeaveResponse[]>("/leave-applications/approved");
    return response.data;
  } catch (error: any) {
    console.error("List leave application failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
