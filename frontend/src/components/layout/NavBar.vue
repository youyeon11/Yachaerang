<template>
  <aside class="sidebar">
    <div class="logo-section" @click="goToMain" style="cursor: pointer">
      <img src="@/assets/logo.svg" alt="Logo" class="logo" @error="handleImageError" />
    </div>

    <nav class="nav-section">
      <button
        v-for="tab in tabs"
        :key="tab.id"
        class="nav-button"
        :class="{ active: activeTab === tab.id }"
        :aria-label="tab.label"
        :title="tab.label"
        @click="handleTabClick(tab)"
      >
        <span class="icon" v-html="tab.icon"></span>
      </button>
    </nav>

    <div class="user-section">
      <button
        class="user-button"
        :class="{ active: isProfileActive }"
        aria-label="User profile"
        title="Profile"
        @click="handleUserClick"
      >
        <img
          v-if="props.userAvatar"
          :src="props.userAvatar"
          class="user-avatar"
          alt="Profile"
          @error="handleAvatarError"
        />
        <span v-else class="icon" v-html="userIcon"></span>
      </button>
    </div>
  </aside>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuth } from '@/stores/auth';

const router = useRouter();
const route = useRoute();
const { isLoggedIn } = useAuth();

const props = defineProps({
  userAvatar: { type: String, default: '' },
});

const tabs = [
  {
    id: 1,
    label: '오늘의 품목',
    path: '/searchMainpage',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <rect x="3" y="3" width="7" height="7"></rect>
      <rect x="14" y="3" width="7" height="7"></rect>
      <rect x="14" y="14" width="7" height="7"></rect>
      <rect x="3" y="14" width="7" height="7"></rect>
    </svg>`,
  },
  {
    id: 2,
    label: '세부 가격 정보',
    path: '/search',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <line x1="18" y1="20" x2="18" y2="10"></line>
      <line x1="12" y1="20" x2="12" y2="4"></line>
      <line x1="6" y1="20" x2="6" y2="14"></line>
    </svg>`,
  },
  {
    id: 3,
    label: '농촌 기사',
    path: '/articles',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
      <polyline points="14 2 14 8 20 8"></polyline>
      <line x1="16" y1="13" x2="8" y2="13"></line>
      <line x1="16" y1="17" x2="8" y2="17"></line>
      <polyline points="10 9 9 9 8 9"></polyline>
    </svg>`,
  },
  {
    id: 4,
    label: 'AI 야치',
    path: '/ai-chat',
    icon: `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
      <path d="M13 8H7"></path>
      <path d="M17 12H7"></path>
    </svg>`,
  },
];

const userIcon = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
  <circle cx="12" cy="7" r="4"></circle>
</svg>`;

const isProfileActive = computed(() => route.path === '/mypage');

const getActiveTabFromRoute = (path) => {
  const tab = tabs.find((t) => t.path === path);
  return tab ? tab.id : null;
};

const activeTab = ref(getActiveTabFromRoute(route.path));

watch(
  () => route.path,
  (newPath) => {
    activeTab.value = getActiveTabFromRoute(newPath);
  }
);

const goToMain = () => {
  router.push('/');
};

const handleTabClick = (tab) => {
  activeTab.value = tab.id;
  router.push(tab.path);
};

const handleUserClick = () => {
  const accessToken = localStorage.getItem('accessToken');

  const hasValidToken =
    accessToken &&
    accessToken !== null &&
    accessToken.trim() !== '' &&
    accessToken.trim() !== 'null' &&
    accessToken.trim() !== 'undefined';

  if (hasValidToken) {
    router.push('/mypage');
  } else {
    router.push('/login');
  }
};

const handleImageError = (e) => {
  e.target.style.display = 'none';
};

const handleAvatarError = (e) => {
  e.target.style.display = 'none';
};
</script>

<style scoped>
.sidebar {
  width: 60px;
  height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  background: #fff;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  padding: 16px 0;
  box-sizing: border-box;
  overflow: hidden;
}
.logo-section {
  display: flex;
  justify-content: center;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  margin-bottom: 12px;
}

.logo {
  width: 44px;
  height: 44px;
  object-fit: contain;
}

.nav-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0 8px;
}

.nav-button {
  height: 46px;
  border: none;
  border-radius: 12px;
  background: transparent;
  cursor: pointer;
  display: grid;
  place-items: center;
  color: #666;
  outline: none;
  transition: color 0.18s ease;
}

.nav-button:focus {
  outline: none;
}

.icon {
  width: 24px;
  height: 24px;
  display: grid;
  place-items: center;
}

.icon :deep(svg) {
  width: 24px;
  height: 24px;
  transition: filter 0.18s ease, color 0.18s ease;
}

.nav-button:hover {
  background: transparent;
  box-shadow: none;
  transform: none;
  color: rgba(254, 204, 33, 0.65);
}

.nav-button:hover .icon :deep(svg) {
  filter: drop-shadow(0 6px 10px rgba(254, 204, 33, 0.35));
}

.nav-button.active {
  background: transparent;
  box-shadow: none;
  transform: none;
  color: #fecc21;
}

.nav-button.active .icon :deep(svg) {
  filter: none;
}

.nav-section button:nth-child(-n + 2):hover,
.nav-section button:nth-child(-n + 2).active {
  color: #e53935 !important;
}

.nav-section button:nth-child(n + 3):hover,
.nav-section button:nth-child(n + 3).active {
  color: #fecc21 !important;
}

.user-section {
  padding: 12px 8px 0;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.user-button {
  width: 100%;
  height: 52px;
  border: none;
  border-radius: 14px;
  background: transparent;
  cursor: pointer;
  display: grid;
  place-items: center;
  outline: none;
}

.user-button .icon :deep(svg) {
  stroke: #000;
  stroke-width: 2;
  transition: stroke 0.18s ease, stroke-width 0.18s ease, filter 0.18s ease;
}

.user-button:hover .icon :deep(svg),
.user-button.active .icon :deep(svg) {
  stroke: #65be34 !important;
  stroke-width: 2.4 !important;
  filter: drop-shadow(0 6px 10px rgba(101, 190, 52, 0.35));
}

.user-button:hover .user-avatar,
.user-button.active .user-avatar {
  filter: drop-shadow(0 6px 10px rgba(101, 190, 52, 0.35));
}

.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 9999px;
  object-fit: cover;
  transition: filter 0.18s ease;
}
</style>
