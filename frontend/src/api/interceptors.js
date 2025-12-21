import { apiClient, refreshClient } from './axios';
import { tokenStorage } from '@/utils/storage';

let logoutHandler = () => {
  tokenStorage.clear();
  window.location.href = '/login';
};

export function setLogoutHandler(handler) {
  logoutHandler = handler;
}

// 토큰 재발급 큐 관리
let isRefreshing = false;
let refreshQueue = [];

const processQueue = (error, newAccessToken) => {
  refreshQueue.forEach(({ resolve, reject }) => {
    error ? reject(error) : resolve(newAccessToken);
  });
  refreshQueue = [];
};

const refreshTokens = async () => {
  const refreshToken = tokenStorage.getRefreshToken();
  // refreshClient 사용
  const response = await refreshClient.post(
    '/api/v1/auth/reissue',
    {},
    { headers: { Authorization: `Bearer ${refreshToken}` } }
  );

  const { accessToken, refreshToken: newRefresh } = response.data?.data || {};
  
  if (!accessToken) {
    throw new Error('토큰 재발급 실패');
  }

  tokenStorage.setTokens(accessToken, newRefresh);
  return accessToken;
};

export function setupInterceptors() {
  // Request: 토큰 자동 첨부 (Free Path 제외)
  apiClient.interceptors.request.use((config) => {
    if (!isAuthFreePath(config.url)) {
      const token = tokenStorage.getAccessToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  }, (error) => Promise.reject(error));

  // Response: 401 처리 + 토큰 재발급
  apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;
      const status = error.response?.status;

      // 401에러
      if (status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        if (!tokenStorage.getRefreshToken()) {
          logoutHandler();
          return Promise.reject(error);
        }

        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            refreshQueue.push({ resolve, reject });
          }).then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return apiClient(originalRequest);
          });
        }

        isRefreshing = true;

        try {
          const newToken = await refreshTokens();
          processQueue(null, newToken);
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return apiClient(originalRequest);
        } catch (refreshError) {
          processQueue(refreshError, null);
          logoutHandler();
          return Promise.reject(refreshError);
        } finally {
          isRefreshing = false;
        }
      }

      return Promise.reject(error);
    }
  );
}