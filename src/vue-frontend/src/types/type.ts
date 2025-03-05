export interface ApiResp<T = never> {
  data: T;
}

export interface IAccessToken {
  access_token: string;
  expire_in: number;
}

export interface ILeaveApplicationList {
  data: ILeaveApplication;
}

export interface IUserResp {
  data: ILeaveApplication[];
}


export interface ILeaveApplication {
  id: number | null;
  leaveTypeId: number | null;
  leaveTypeName: string;
  startDate: Date | null;
  endDate: Date | null;
  reason: string;
  status: string;
}
export type confrimStatus = "pending" | "accepted";

export interface ILeaveRequest {
  id: number;
  name: string;
  leave_type: string;
  leave_duration_from: string;
  leave_duration_to: string;
  leave_reason: string;
  parentId: number | null; // 親カテゴリのID（ルートカテゴリなら省略）
  children?: ILeaveTypes[]; // 子カテゴリ（サブカテゴリー）
}

export interface IUser {
  id: number | null;
  username: string;
  email: string;
  password: string;
  fullName: string;
  fullNameFurigana: string;
  birthday: string | null;
  roles: string[];
  department: string;
  workPlace: string;
  joinDate: string | null;
  gender: string;
}
export interface ILeaveTypes {
  id: number | null; // 一意のID
  name: string; // 休暇の名前
  value: string | null;
  // leave_type: string; // 休暇のタイプ
  parentId: number | null; // 親カテゴリのID（ルートカテゴリなら省略）
  children?: ILeaveTypes[]; // 子カテゴリ（サブカテゴリー）
}
export interface IUserLeaves {
  user_id: number | null; // 一意のID
  username: string; // 休暇の名前
  leave_name: string; //
  leave_type_id: number | null; // 休暇のタイプ
  available_days: number;
  total_leaves: number; // ����の数
  used_days: number;
  valid_from: string;
  valid_to: string;
}
