import axios from 'axios';
import { logout } from '@/stores/auth';

const apiClient = axios.create({
  baseURL: import.meta.env.MODE === 'production' ? import.meta.env.VITE_API_BASE_URL : '',
  headers: { 'Content-Type': 'application/json;charset=UTF-8' },
});

let isRefreshing = false;
let refreshQueue = [];

function processQueue(error, token) {
  refreshQueue.forEach((p) => {
    if (error) p.reject(error);
    else p.resolve(token);
  });
  refreshQueue = [];
}

apiClient.interceptors.request.use(
  (config) => {
    const url = config.url || '';

    const authFree =
      url.startsWith('/api/v1/auth/login') ||
      url.startsWith('/api/v1/auth/signup') ||
      url.startsWith('/api/v1/auth/reissue');

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

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          refreshQueue.push({ resolve, reject });
        })
          .then((newAccessToken) => {
            originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
            return apiClient(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      isRefreshing = true;

      try {
        const response = await apiClient.post(
          '/api/v1/auth/reissue',
          {},
          {
            headers: {
              Authorization: refreshToken,
            },
          }
        );

        const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response.data.data;

        if (!newAccessToken) {
          throw new Error('accessToken이 응답에 없습니다.');
        }

        localStorage.setItem('accessToken', newAccessToken);

        if (newRefreshToken && newRefreshToken.trim() !== '') {
          localStorage.setItem('refreshToken', newRefreshToken);
        }

        processQueue(null, newAccessToken);
        isRefreshing = false;

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        isRefreshing = false;
        logout();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
