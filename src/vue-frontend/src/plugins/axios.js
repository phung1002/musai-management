import axios from "axios";
const axiosInstance = axios.create({
  // baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api", // backend URL in local
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://163.43.229.228:8080/api', // backend URL in server
  timeout: 10000, // Waiting time
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Important: Automation sent cookie each request
});

// Interceptor response
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;
