export interface ApiResp<T = never> {
  data: T;
}

export interface IAccessToken {
  access_token: string;
  expire_in: number;
}

export interface ILeaveRequest {
  id: number | null;
  leaveTypeId: number | null;
  leaveTypeName: string;
  startDate: Date | null;
  endDate: Date | null;
  reason: string;
  status: string;
}
export type confrimStatus = "pending" | "accepted";

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
  value?: string;
  // leave_type: string; // 休暇のタイプ
  parentId: number | null; // 親カテゴリのID（ルートカテゴリなら省略）
  children?: ILeaveTypes[]; // 子カテゴリ（サブカテゴリー）
}
export interface IUserLeaves {
  id: number | null; // ����ID
  leaveTypeId: number | null; // 休暇のタイプ
  leaveTypeName: string; //
  userId: number | null; // 一意のID
  userName: string; // 休暇の名前
  totalDays: number | null;
  usedDays: number | null; // ����の数
  remainedDay: number | null;
  validFrom: string;
  validTo: string;
  name: string; // 追加
  parentId: null; // 追加
}
export interface ILeaveResponse {
  id: number;
  userFullName: string;
  leaveTypeName: string;
  createdAt: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: string;
  respondedAt: string;
  respondedByFullName: string;
}
