export interface ILeaveRequest {
  id: number | null;
  leaveTypeId: number | null;
  leaveTypeName: string;
  startDate: Date | null;
  endDate: Date | null;
  reason: string;
  status: string;
  leaveTypeValue: string;
}

export interface IEmployee {
  id: number | null;
  employeeId: string;
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
export interface IPasswordChange {
  currentPassword: string; // 現在のパスワード
  newPassword: string; // 新しいパスワード
  confirmPassword: string; // 新しいパスワード（確認用）
}
export interface ILeaveTypes {
  id: number | null; // 一意のID
  name: string; // 休暇の名前
  value: string | null; // 休暇の値
  parentId: number | null; // 親カテゴリのID（ルートカテゴリなら省略）
  children?: ILeaveTypes[]; // 子カテゴリ（サブカテゴリー）
}
export interface IEmployeeLeaves {
  id: number | null; // ����ID
  leaveTypeId: number; // 休暇のタイプ
  leaveTypeName: string; //
  leaveTypeValue: string;
  employeeId: number; // 一意のID
  employeeFullName: string; // 休暇の名前
  totalDays: number;
  usedDays: number;
  remainedDays: number;
  validFrom: string;
  validTo: string;
  name: string; // 追加
}
export interface ILeaveResponse {
  id: number;
  employeeFullName: string;
  leaveTypeName: string;
  leaveTypeValue: string;
  createdAt: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: string;
  respondedAt: string;
  respondedByFullName: string;
}
// 書類提出
export interface IDocument {
  id: number;
  title: string;
  uploadBy: string;
  filePath: string;
  submitDate: string;
  employeeId: string;
}

export interface IEvent {
  title: string;
  start: Date;
  end: Date;
  color: string;
  allDay: boolean;
}
