import { ILeaveApplication } from "@/types/type";
import axiosIns from "@/plugins/axios";

export async function listLeaveApplicationForMember(): Promise<ILeaveApplication[]> {
  try {
    const response = await axiosIns.get<ILeaveApplication[]>("/member/leave-applications");
    return response.data;
  } catch (error) {
    console.error("List leave application failed:", error);
    throw error;
  }
}

// call to api cancel leave application
export async function cancelApplication(id: number): Promise<void> {
  try {
    await axiosIns.put(`/leave-applications/cancel/${id}`);
  } catch (error: any) {
    if (error.response) {
      console.error("Cancel leave application failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}
