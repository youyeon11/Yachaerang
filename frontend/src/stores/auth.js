import { ref, computed } from 'vue';

const isLoggedIn = ref(false);
const user = ref(null);

// 로그인
export function login(userData) {
  if (userData.accessToken && userData.accessToken.trim() !== '') {
    isLoggedIn.value = true;
    user.value = userData;
    localStorage.setItem('accessToken', userData.accessToken);
    localStorage.setItem('user', JSON.stringify(userData));
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

  localStorage.removeItem('user');
}

// 로그인 상태 확인
export function checkAuth() {
  isLoggedIn.value = false;

  const token = localStorage.getItem('accessToken');
  const storedUser = localStorage.getItem('user');

  if (!token || !storedUser) {
    logout();
    return false;
  }

  // 토큰이 빈 문자열이거나 잘못된 값이면 제거하고 로그아웃 상태
  const trimmedToken = token.trim();
  if (trimmedToken === '' || trimmedToken === 'null' || trimmedToken === 'undefined') {
    logout();
    return false;
  }

  try {
    user.value = JSON.parse(storedUser);

    isLoggedIn.value = true;
    return true;
  } catch (e) {
    logout();
    return false;
  }
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
