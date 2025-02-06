import { IAccessToken, ILeaveRequest } from '@/types/type';
import axiosIns from '@/plugins/axios';

export async function getRequstLists(): Promise<ILeaveRequest[]> {
  try {
    const response = await axiosIns.get<ILeaveRequest[]>('/user/requsts');
    return response.data;
  } catch (error) {
    console.error('List user failed:', error);
    throw error;
  }
}