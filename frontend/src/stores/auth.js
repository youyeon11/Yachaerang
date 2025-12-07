// src/stores/auth.js
import { ref, computed } from 'vue';

const isLoggedIn = ref(false);
const user = ref(null);

// 로그인
export function login(userData) {
  if (userData.accessToken && userData.accessToken.trim() !== '') {
    isLoggedIn.value = true;
    user.value = userData;
    localStorage.setItem('accessToken', userData.accessToken);
    if (userData.refreshToken && userData.refreshToken.trim() !== '') {
      localStorage.setItem('refreshToken', userData.refreshToken);
    }
  } else {
    isLoggedIn.value = false;
    user.value = null;
  }
}

// ✅ 로그아웃: 상태/토큰만 정리
export function logout() {
  isLoggedIn.value = false;
  user.value = null;
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
}

// 로그인 상태 확인
export function checkAuth() {
  isLoggedIn.value = false;

  const token = localStorage.getItem('accessToken');
  if (!token || token === null) {
    isLoggedIn.value = false;
    return false;
  }

  const trimmedToken = token.trim();
  if (trimmedToken === '' || trimmedToken === 'null' || trimmedToken === 'undefined') {
    isLoggedIn.value = false;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    return false;
  }

  isLoggedIn.value = true;
  return true;
}

export function useAuth() {
  return {
    isLoggedIn: computed(() => isLoggedIn.value),
    user: computed(() => user.value),
    login,
    logout,
    checkAuth,
  };
}
