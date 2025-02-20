export interface ApiResp<T = never> {
  data: T;
}

export interface IAccessToken {
  access_token: string;
  expire_in: number;
}

export type confrimStatus = "pending" | "accepted";

export interface ILeaveList {
  leave_name: string;
  public_leave: string;
  special_occasions_leave: string;
  special_day_leave: string;
}
