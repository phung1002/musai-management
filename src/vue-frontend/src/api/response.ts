import { ILeaveApplications } from "@/types/type";
import axiosIns from "@/plugins/axios";

// 申請確認リストAPI呼び出し
export async function getLeaveApplicationsLists(): Promise<
  ILeaveApplications[]
> {
  try {
    const response = await axiosIns.get<ILeaveApplications[]>(
      "/leave-applications"
    );
    return response.data;
  } catch (error) {
    console.error("List applications failed:", error);
    throw error;
  }
}
// 申請確認検索API呼び出し
export async function searchLeaveApplications(
  key: string
): Promise<ILeaveApplications[]> {
  try {
    const response = await axiosIns.get<ILeaveApplications[]>(
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
