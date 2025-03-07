import { ILeaveApplication } from "@/types/type";
import axiosIns from "@/plugins/axios";

export async function listLeaveApplicationForMember(): Promise<
  ILeaveApplication[]
> {
  try {
    const response = await axiosIns.get<ILeaveApplication[]>(
      "/member/leave-applications"
    );
    return response.data;
  } catch (error: any) {
    console.error("List leave application failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// call to api cancel leave application
export async function cancelApplication(id: number): Promise<void> {
  try {
    await axiosIns.put(`/leave-applications/cancel/${id}`);
  } catch (error: any) {
    console.error("Cancel leave application failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// call to api apply a leave application
export async function applyLeaveApplication(
  params: ILeaveApplication
): Promise<void> {
  try {
    await axiosIns.post("/leave-applications", params);
  } catch (error: any) {
    console.error("Apply failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}

// call to api update leave application
export async function updateApplication(
  id: number,
  params: ILeaveApplication
): Promise<void> {
  try {
    await axiosIns.put(`/leave-applications/update/${id}`, params);
  } catch (error: any) {
    console.error("Update leave application failed:", error);
    throw new Error("error." + (error.response?.data?.message ?? "unexpected"));
  }
}
