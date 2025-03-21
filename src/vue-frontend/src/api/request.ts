import { ILeaveRequest } from "@/types/type";
import axiosIns from "@/plugins/axios";

// 申請リストと検索API呼び出し
export async function listLeaveRequestForMember(
  key: string
): Promise<ILeaveRequest[]> {
  try {
    const response = await axiosIns.get<ILeaveRequest[]>(
      "/leave-applications",
      { params: { keyword: key } }
    );
    return response.data;
  } catch (error: any) {
    console.error("List leave application failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// call to api cancel leave application
export async function cancelRequest(id: number): Promise<void> {
  try {
    await axiosIns.put(`/leave-applications/cancel/${id}`);
  } catch (error: any) {
    console.error("Cancel leave application failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// call to api apply a leave application
export async function requestLeave(params: ILeaveRequest): Promise<void> {
  try {
    await axiosIns.post("/leave-applications", params);
  } catch (error: any) {
    console.error("Apply failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// call to api update leave application
export async function updateLeaveRequest(
  id: number,
  params: ILeaveRequest
): Promise<void> {
  try {
    await axiosIns.put(`/leave-applications/update/${id}`, params);
  } catch (error: any) {
    console.error("Update leave application failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
