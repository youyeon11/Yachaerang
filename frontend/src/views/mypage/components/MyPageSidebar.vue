<template>
  <aside class="mypage-sidebar">
    <div class="profile-box">
      <div class="profile-avatar">
        <img v-if="profileImageUrl" :src="profileImageUrl" alt="프로필" />
        <span v-else class="avatar-icon">👤</span>
      </div>

      <div class="profile-info">
        <p class="profile-name">{{ nickname }}</p>
        <p class="profile-email">{{ email }}</p>
      </div>
    </div>

    <nav class="mypage-menu">
      <button
        v-for="item in menuItems"
        :key="item.id"
        class="menu-item"
        :class="{ active: isActive(item) }"
        @click="go(item.path)"
      >
        <span class="menu-icon">-</span>
        <span class="menu-label">{{ item.label }}</span>
      </button>
    </nav>

    <!-- 로그아웃 -->
    <button class="logout-button" @click="handleLogout">
      <span class="logout-icon">⏻</span>
      <span class="logout-label">로그아웃</span>
    </button>
  </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getMyProfile } from '@/api/member';
import { logout } from '@/stores/auth';
import apiClient from '@/api/axios';

const router = useRouter();
const route = useRoute();

const email = ref('');
const nickname = ref('');
const profileImageUrl = ref('');

onMounted(async () => {
  try {
    const { data } = await getMyProfile();
    if (data.success) {
      email.value = data.data.email;
      nickname.value = data.data.nickname;
      profileImageUrl.value = data.data.imageUrl || '';
    }
  } catch (e) {
    console.error('프로필 조회 실패', e);
  }
});

const menuItems = [
  { id: 'farm', label: '나의 농장', path: '/mypage/farm' },
  { id: 'profile', label: '프로필 수정', path: '/mypage/profile' },
  { id: 'password', label: '비밀번호 변경', path: '/mypage/password' },
  { id: 'items', label: '나의 품목 관리', path: '/mypage/items' },
];

const isActive = (item) => {
  return route.path === item.path;
};

const go = (path) => {
  router.push(path);
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

<style scoped>
.mypage-sidebar {
  border-left: 1px solid #e5e7eb;
  padding: 40px 40px 32px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 260px;
  box-sizing: border-box;
}

.profile-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.profile-avatar {
  width: 80px;
  height: 80px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 999px;
  object-fit: cover;
}

.avatar-icon {
  font-size: 32px;
}

.profile-info {
  text-align: center;
}

.profile-name {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 600;
  color: #111827;
}

.profile-email {
  margin: 0;
  font-size: 12px;
  color: #6b7280;
}

.mypage-menu {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.menu-item {
  padding: 0;
  margin: 0;
  border: none;
  background: transparent;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #4b5563;
  cursor: pointer;
  text-align: left;
}

.menu-icon {
  font-size: 8px;
}

.menu-label {
  white-space: nowrap;
}

.menu-item.active {
  font-weight: 600;
  color: #111827;
}

.logout-button {
  margin-top: auto;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
  border: none;
  background: transparent;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #4b5563;
  cursor: pointer;
  width: 100%;
}

.logout-icon {
  font-size: 12px;
}

.logout-label {
  white-space: nowrap;
}

@media (max-width: 900px) {
  .mypage-sidebar {
    border-left: none;
    border-top: 1px solid #e5e7eb;
    width: 100%;
    padding: 24px 0 16px;
    margin-top: 32px;
  }

  .profile-box {
    align-items: flex-start;
  }

  .profile-info {
    text-align: left;
  }
}
</style>
