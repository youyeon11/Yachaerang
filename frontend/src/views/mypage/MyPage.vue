<template>
  <main class="flex-1 overflow-y-auto p-8 bg-gray-50">
    <div class="mx-auto max-w-4xl space-y-6">
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-gray-900">마이페이지</h1>
        <p class="text-gray-600">나의 정보를 관리해보세요.</p>
      </div>

      <router-view v-if="isChildRoute" />

      <template v-else>
        <ProfileSection
          :profile="profile"
          @update:profile="handleProfileUpdate"
        />

        <AccountActions />

        <div class="grid gap-6 md:grid-cols-2">
          <FavoriteDashboards />
          <FavoriteArticles />
        </div>
      </template>
    </div>
  </main>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

import ProfileSection from './components/ProfileSection.vue';
import AccountActions from './components/AccountActions.vue';
import FavoriteDashboards from '@/views/mypage/components/FavoriteDashboards.vue';
import FavoriteArticles from '@/views/mypage/components/FavoriteArticles.vue';

const route = useRoute();

const isChildRoute = computed(() => route.path !== '/mypage');
</script>