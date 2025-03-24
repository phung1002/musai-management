import axios from 'axios';
import { useUserStore } from '@/store/userStore';
import router from '@/router';
// Create instance Axios
const axiosInstance = axios.create({
  // baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api', // backend URL in local
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://163.43.229.228:8080/api', // backend URL in server
  timeout: 10000, // Waiting time
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Important: Automation sent cookie each request
});

// Interceptor response
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response && error.response.status === 401) {
      // const userStore = useUserStore();
      // userStore.setToken('');
      // userStore.setUsername('');
      // userStore.setRoles([]);
      // userStore.setAuthenticated(false);

      // Delete token from cookie
      // document.cookie = "access_token=; Max-Age=0; path=/";

      // Direct to login page
      await router.replace({ name: 'login', query: { to: router.currentRoute.value.fullPath } });
    }
    return Promise.reject(error);
  }
);


export default axiosInstance;
