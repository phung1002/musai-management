import { ILeaveApplication } from "@/types/type";
import axiosIns from "@/plugins/axios";

export async function getRequstLists(): Promise<ILeaveApplication[]> {
  try {
    const response = await axiosIns.get<ILeaveApplication[]>("/user/requsts");
    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
