import { apiClient, refreshClient } from './axios';
import { tokenStorage } from '@/utils/storage';

/**
 * 로그아웃 핸들러
 */
let logoutHandler = () => {
  tokenStorage.clear();
  window.location.href = '/login';
};

export function setLogoutHandler(handler) {
  if (typeof handler === 'function') logoutHandler = handler;
}

/**
 * 토큰 재발급 동시성 제어
 */
let isRefreshing = false;
let refreshQueue = [];

function processQueue(error, newAccessToken) {
  refreshQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error);
    else resolve(newAccessToken);
  });
  refreshQueue = [];
}

/**
 * refresh token으로 access token 재발급
 */
function sanitizeToken(token) {
  if (!token) return null;
  return String(token).trim().replace(/\s+/g, '');
}

async function refreshTokens() {
  const rawRefreshToken = tokenStorage.getRefreshToken();
  const refreshToken = sanitizeToken(rawRefreshToken);

  if (!refreshToken) {
    throw new Error('리프레시 토큰이 없습니다.');
  }

  const response = await refreshClient.post(
    '/api/v1/auth/reissue',
    {},
    {
      headers: {
        Authorization: `Bearer ${refreshToken}`,
      },
    }
  );

  const { accessToken, refreshToken: newRefresh } = response.data?.data || {};

  const sanitizedAccess = sanitizeToken(accessToken);
  const sanitizedRefresh = sanitizeToken(newRefresh) ?? refreshToken;

  if (!sanitizedAccess) {
    throw new Error('토큰 재발급 실패: accessToken이 없습니다.');
  }

  tokenStorage.setTokens(sanitizedAccess, sanitizedRefresh);

  if (typeof tokenStorage.updateUserTokens === 'function') {
    tokenStorage.updateUserTokens(sanitizedAccess, sanitizedRefresh);
  } else if (typeof tokenStorage.getUser === 'function' && typeof tokenStorage.setUser === 'function') {
    const user = tokenStorage.getUser();
    if (user) tokenStorage.setUser({ ...user, accessToken: sanitizedAccess, refreshToken: sanitizedRefresh });
  }
  return sanitizedAccess;
}

/**
 * 인터셉터 중복 등록 방지
 */
let isSetup = false;

/**
 * setupInterceptors()
 */
export function setupInterceptors() {
  if (isSetup) return;
  isSetup = true;

  // 헤더 자동 첨부 (apiClient : 인증 기반 요청)
  apiClient.interceptors.request.use(
    (config) => {
      const token = tokenStorage.getAccessToken();
      if (token) {
        config.headers = config.headers ?? {};
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // Response: 401 처리 + 토큰 재발급 + 요청 재시도
  apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
      if (!error || !error.config) {
        return Promise.reject(error);
      }

      const originalRequest = error.config;
      const status = error.response?.status;

      const reqUrl = originalRequest.url || '';
      const isReissueCall = reqUrl.includes('/api/v1/auth/reissue');

      // 401이 아니면 그대로 예외로 처리
      if (status !== 401) {
        return Promise.reject(error);
      }

      // 재발급 API에서 401이면 즉시 로그아웃
      if (isReissueCall) {
        logoutHandler();
        return Promise.reject(error);
      }

      // 무한 루프 방지용 플래그
      if (originalRequest._retry) {
        logoutHandler();
        return Promise.reject(error);
      }
      originalRequest._retry = true;

      // refresh token이 없다면 재발급 불가 → 로그아웃
      if (!tokenStorage.getRefreshToken()) {
        logoutHandler();
        return Promise.reject(error);
      }

      // 이미 refresh 진행 중이면 큐에 대기
      if (isRefreshing) {
        try {
          const newAccessToken = await new Promise((resolve, reject) => {
            refreshQueue.push({ resolve, reject });
          });

          originalRequest.headers = originalRequest.headers ?? {};
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
          return apiClient(originalRequest);
        } catch (queueErr) {
          return Promise.reject(queueErr);
        }
      }

      // refresh 시작
      isRefreshing = true;

      try {
        const newAccessToken = await refreshTokens();
        processQueue(null, newAccessToken);

        originalRequest.headers = originalRequest.headers ?? {};
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

        return apiClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        logoutHandler();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
  );
}
