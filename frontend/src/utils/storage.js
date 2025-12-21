const TOKEN_KEYS = {
  ACCESS: 'accessToken',
  REFRESH: 'refreshToken',
};

export const tokenStorage = {
  getAccessToken() {
    return localStorage.getItem(TOKEN_KEYS.ACCESS);
  },

  getRefreshToken() {
    return localStorage.getItem(TOKEN_KEYS.REFRESH);
  },

  setTokens(accessToken, refreshToken) {
    localStorage.setItem(TOKEN_KEYS.ACCESS, accessToken);
    if (refreshToken?.trim()) {
      localStorage.setItem(TOKEN_KEYS.REFRESH, refreshToken);
    }
  },

  clear() {
    localStorage.removeItem(TOKEN_KEYS.ACCESS);
    localStorage.removeItem(TOKEN_KEYS.REFRESH);
  },

  hasTokens() {
    return !!this.getAccessToken();
  },
};