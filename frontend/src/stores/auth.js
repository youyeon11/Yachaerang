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

// 로그아웃
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

  // 토큰이 빈 문자열이거나 잘못된 값이면 제거하고 로그아웃 상태
  const trimmedToken = token.trim();
  if (trimmedToken === '' || trimmedToken === 'null' || trimmedToken === 'undefined') {
    isLoggedIn.value = false;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    return false;
  }

  // 유효한 토큰이 있는 경우만 로그인 상태 (목 데이터 포함)
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
