import axios from 'axios';

// Create instance Axios
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api', // URL cơ sở
  timeout: 10000, // Thời gian chờ
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor request
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('access_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor response
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      console.error('Unauthorized, redirecting to login...');
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
