import { ref, computed } from 'vue';

const isLoggedIn = ref(false);
const user = ref(null);

// 로그인
export function login(userData) {
  isLoggedIn.value = true;
  user.value = userData;
  localStorage.setItem('accessToken', userData.accessToken || '');
  localStorage.setItem('refreshToken', userData.refreshToken || '');
}

// 로그아웃
export function logout() {
  isLoggedIn.value = false;
  user.value = null;
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
}

// 로그인 상태 확인
export function checkAuth() {
  const token = localStorage.getItem('accessToken');
  if (token) {
    isLoggedIn.value = true;
  } else {
    isLoggedIn.value = false;
  }
  return isLoggedIn.value;
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
