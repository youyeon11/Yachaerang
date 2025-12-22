import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { loginApi } from "@/api/auth";
import { tokenStorage } from "@/utils/storage";

export const useAuthStore = defineStore("auth", () => {
  const router = useRouter();

  // State
  const user = ref(null);
  const isLoading = ref(false);

  // Getters
  const isAuthenticated = computed(() => !!user.value);

  // Actions
  const login = async (email, password) => {
    isLoading.value = true;
    try {
      const response = await loginApi(email, password);
      const { accessToken, refreshToken, user: userData } = response.data.data;

      tokenStorage.setTokens(accessToken, refreshToken);
      user.value = userData;

      return response;
    } finally {
      isLoading.value = false;
    }
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch (e) {
      console.error("logout API error", e);
    } finally {
      tokenStorage.clear();
      user.value = null;
      router.push("/login");
    }
  };

  const checkAuth = () => {
    return tokenStorage.hasTokens();
  };

  return {
    user,
    isLoading,
    isAuthenticated,
    login,
    logout,
    checkAuth,
  };
});
