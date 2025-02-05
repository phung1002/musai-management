import { IAccessToken } from './type';
import axiosIns from '@/plugins/axios';

export async function getAllUsers() {
  const options = {
    method: 'GET',
    url: `/user/list`,
    headers: {
      'Content-Type': 'application/json'
    }
  };
  return axiosIns.request<IAccessToken>(options);
}
export async function getProfile() {
  const options = {
    method: 'GET',
    url: `/user/profile`,
    headers: {
      'Content-Type': 'application/json'
    }
  };
  return axiosIns.request<IAccessToken>(options);
}