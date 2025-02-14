export interface ApiResp<T = never> {
  data: T;
}

export interface IAccessToken {
  access_token: string;
  expire_in: number;
}

export interface ILeaveRequestList {
  data: ILeaveRequest;
}

export interface IUserResp {
  data: ILeaveRequest[];
}

export type confrimStatus = "pending" | "accepted";

export interface ILeaveRequest {
  userid: number;
  username: string;
  leave_type: string;
  leave_duration_from: Date;
  leave_duration_to: Date;
  leave_reason: string;
  status: confrimStatus;
  access_token?: string;
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
