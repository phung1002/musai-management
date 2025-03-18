import axiosIns from "@/plugins/axios";
import { ILeaveTypes, IUserLeaves } from "@/types/type";
// 休暇管理
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
// 休暇追加API呼び出し
export async function addLeave(params: ILeaveTypes): Promise<void> {
  try {
    await axiosIns.post("/leave-types/add", params);
    console.log("Add leave successfully");
  } catch (error: any) {
    if (error.response) {
      console.error("Add leave failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}
// 休暇更新API呼び出し
export async function updateLeave(
  id: number,
  params: ILeaveTypes
): Promise<void> {
  try {
    await axiosIns.put(`/leave-types/update/${id}`, params);
    console.log("Update user successfully");
  } catch (error: any) {
    if (error.response) {
      console.error("Update user failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
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
// 休暇検索API呼び出し
export async function searchLeave(key: string): Promise<ILeaveTypes[]> {
  try {
    const response = await axiosIns.get<ILeaveTypes[]>("/leave-types/search", {
      params: { keyword: key },
    });
    return response.data;
  } catch (error) {
    console.error("Search leave failed:", error);
    throw error;
  }
}

// 社員休暇管理
// 社員休暇リスト取得
export async function getUserLeaves(): Promise<IUserLeaves[]> {
  try {
    const response = await axiosIns.get<IUserLeaves[]>("/user-leaves/all");
    console.log("response.data", response.data);

    return response.data;
  } catch (error) {
    console.error("List user failed:", error);
    throw error;
  }
}
// 休社員休暇更新API呼び出し
export async function updateUserLeave(
  id: number,
  params: IUserLeaves
): Promise<void> {
  try {
    await axiosIns.put(`/user-leaves/update/${id}`, params);
    console.log("Update user successfully");
  } catch (error: any) {
    if (error.response) {
      console.error("Update user failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}
// 社員休暇検索API呼び出し
export async function searchUserLeave(key: string): Promise<IUserLeaves[]> {
  try {
    const response = await axiosIns.get<IUserLeaves[]>("/user-leaves/search", {
      params: { keyword: key },
    });
    return response.data;
  } catch (error) {
    console.error("Search Userleave failed:", error);
    throw error;
  }
}
// 社員休暇追加API呼び出し
export async function addUserLeave(params: IUserLeaves): Promise<void> {
  try {
    await axiosIns.post("/user-leaves/add", params);
    console.log("Add leave successfully");
  } catch (error: any) {
    if (error.response) {
      console.error("Add leave failed:", error.response.data);
    } else {
      console.error("Unexpected error:", error);
    }
    throw error;
  }
}
