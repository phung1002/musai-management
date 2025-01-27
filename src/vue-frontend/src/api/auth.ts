import { IAccessToken } from './type';
import axiosIns from '@/plugins/axios';
import { useUserStore } from '@/store/userStore';

interface LoginParams {
  username: string;
  password: string;
}

//call to API login, POSt method
export async function login(params: LoginParams): Promise<IAccessToken> {
  try {
    // Send request to API login
    const response = await axiosIns.post<IAccessToken>('/auth/login', params);
    const userStore = useUserStore();
    const { data } = response;

    // update userStore
    userStore.setAuthenticated(true);
    userStore.setRoles(data.roles);
    userStore.setUsername(data.username);

    // return data
    return response.data;
  } catch (error) {
    // Catch error
    console.error('Login failed', error);
    throw error;
  }
}

// call to api logout
export async function logout() {
  try {
    // server will delete cookie
    await axiosIns.post('/auth/logout');
    console.log('Logged out successfully');
  } catch (error) {
    console.error('Logout failed', error);
  }
}

//api validate authentication
export async function validate() {
  try {
    const response = await axiosIns.get('/auth/validate', { timeout: 5000 });
    return response;
  } catch (error) {
    throw error;
  }
}
