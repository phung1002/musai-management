import { IUser } from '@/types/type';
import axiosIns from '@/plugins/axios';

// call to api create user
export async function getAllUsers(): Promise<IUser[]> {
  try {
    const response = await axiosIns.get<IUser[]>('/user/list');
    return response.data;
  } catch (error) {
    console.error('List user failed:', error);
    throw error;
  }
}

// call to api create user
export async function createUser(params: IUser): Promise<void> {
  try {
    await axiosIns.post('/user/add', params);
    console.log('Add user successfully');
  } catch (error: any) {
    if (error.response) {
      console.error('Add user failed:', error.response.data);
    } else {
      console.error('Unexpected error:', error);
    }
    throw error;
  }
}
