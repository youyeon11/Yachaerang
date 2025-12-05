import axios from 'axios';
import { logout } from '@/stores/auth';

const apiClient = axios.create({
  baseURL: import.meta.env.MODE === 'production' ? import.meta.env.VITE_API_BASE_URL : '',
  headers: { 'Content-Type': 'application/json;charset=UTF-8' },
});

apiClient.interceptors.request.use(
  (config) => {
    const url = config.url || '';

    const authFree = url.startsWith('/api/v1/auth/login') || url.startsWith('/api/v1/auth/reissue');

    if (!authFree) {
      const accessToken = localStorage.getItem('accessToken');
      if (accessToken) {
        config.headers.Authorization = `Bearer ${accessToken}`;
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        logout();
        return Promise.reject(error);
      }

      try {
        const response = await apiClient.post(
          '/api/v1/auth/reissue',
          {},
          {
            headers: {
              Authorization: refreshToken,
              'Content-Type': 'application/json;charset=UTF-8',
              Accept: 'application/json',
            },
          }
        );

        const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data.data;

        localStorage.setItem('accessToken', newAccessToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        logout();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
