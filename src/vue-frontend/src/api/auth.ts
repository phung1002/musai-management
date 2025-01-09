import { IAccessToken } from './type';
import axiosIns from '@/plugins/axios';

export async function login(params: object) {
  const options = {
    method: 'POST',
    url: `/auth/login`,
    data: params,
    headers: {
      'Content-Type': 'application/json'
    }
  };
  return axiosIns.request<IAccessToken>(options);
}
