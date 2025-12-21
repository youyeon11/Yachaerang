<template>
  <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
    <div class="mb-4">
      <h2 class="text-lg font-semibold text-gray-900">계정 관리</h2>
      <p class="text-sm text-gray-600">보안 및 계정 설정</p>
    </div>
    <div class="space-y-3">
      <button 
        @click="goToChangePassword"
        class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2 text-left font-medium text-gray-700 
               cursor-pointer
               transition-all duration-200 
               hover:bg-gray-100 hover:border-gray-400 hover:-translate-y-0.5 hover:shadow-md
               active:translate-y-0 active:shadow-sm"
      >
        비밀번호 변경하기
      </button>
      <button 
        @click="handleLogout"
        class="flex w-full items-center gap-2 rounded-lg border border-gray-300 bg-transparent px-4 py-2 text-left font-medium text-red-500 
               cursor-pointer
               transition-all duration-200 
               hover:bg-red-50 hover:border-red-300 hover:-translate-y-0.5 hover:shadow-md
               active:translate-y-0 active:shadow-sm"
      >
        <IconLogout class="h-4 w-4" />
        로그아웃
      </button>
    </div>
  </div>
</template>
<script setup>
import { useRouter } from 'vue-router';
import IconLogout from '@/components/icons/IconLogout.vue';
import { useAuth } from '@/stores/auth.js';

const router = useRouter();
const {logout} = useAuth();

// 비밀번호 변경 페이지로 이동
const goToChangePassword = () => {
  router.push('/mypage/password');
};

const handleLogout = async () => {
  try {
    await apiClient.post('/api/v1/auth/logout', {});
  } catch (e) {
    console.error('logout error', e);
  } finally {
    logout();
    router.push('/login');
  }
};
</script>