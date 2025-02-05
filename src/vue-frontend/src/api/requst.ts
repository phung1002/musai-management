import { IAccessToken } from './type';
import axiosIns from '@/plugins/axios';

export async function getApplyLists() {
  const options = {
    method: 'GET',
    url: `/user/request_list`,
    headers: {
      'Content-Type': 'application/json'
    }
  };
  return axiosIns.request<IAccessToken>(options);
}