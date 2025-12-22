const STORAGE_KEYS = {
  ACCESS: 'accessToken',
  REFRESH: 'refreshToken',
  USER: 'user',
};

export const tokenStorage = {
  // 토큰 관련
  getAccessToken() {
    return localStorage.getItem(STORAGE_KEYS.ACCESS);
  },

  getRefreshToken() {
    return localStorage.getItem(STORAGE_KEYS.REFRESH);
  },

  setTokens(accessToken, refreshToken) {
    localStorage.setItem(STORAGE_KEYS.ACCESS, accessToken);

    if (refreshToken?.trim()) {
      localStorage.setItem(STORAGE_KEYS.REFRESH, refreshToken);
    }
  },

  // user 관련
  setUser(user) {
    if (!user) return;
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
  },

  getUser() {
    const raw = localStorage.getItem(STORAGE_KEYS.USER);
    if (!raw) return null;

    try {
      return JSON.parse(raw);
    } catch (e) {
      console.error('Invalid user in storage', e);
      return null;
    }
  },
    
  updateUserTokens(accessToken, refreshToken) {
    const user = this.getUser?.() ?? null;
    if (!user) return;

    const nextUser = {
      ...user,
      accessToken,
      refreshToken,
    };

    this.setUser(nextUser);
  },

  clearUser() {
    localStorage.removeItem(STORAGE_KEYS.USER);
  },

  // 공통
  clear() {
    localStorage.removeItem(STORAGE_KEYS.ACCESS);
    localStorage.removeItem(STORAGE_KEYS.REFRESH);
    localStorage.removeItem(STORAGE_KEYS.USER);
  },

  hasTokens() {
    return !!this.getAccessToken();
  },
};
