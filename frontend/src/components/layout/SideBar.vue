<template>
  <div class="flex h-screen w-64 flex-col border-r border-gray-200 bg-white">
    <!-- Logo/Header -->
    <div 
      class="flex h-16 items-center border-b border-gray-200 px-6 cursor-pointer"
      @click="goToMain"
    >
      <BrandLogo :src="logoUrl" size="sm" />
    </div>
    <!-- Navigation -->
    <nav class="flex-1 space-y-1 p-4">
      <router-link
        v-for="item in navigation"
        :key="item.name"
        :to="item.href"
        custom
        v-slot="{ navigate, isActive }"
      >
        <div
          @click="navigate"
          class="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors cursor-pointer"
          :class="isActive 
            ? 'bg-[#F44323] text-white' 
            : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'"
        >
          <component :is="item.icon" class="h-5 w-5" />
          {{ item.name }}
        </div>
      </router-link>
    </nav>

    <!-- User Profile -->
    <div class="border-t border-gray-200 p-4">
      <div
        @click="handleUserClick"
        class="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors cursor-pointer"
        :class="isProfileActive 
          ? 'bg-[#F44323] text-white' 
          : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900'"
      >
        <img
          v-if="props.userAvatar"
          :src="props.userAvatar"
          class="h-5 w-5 rounded-full object-cover"
          alt="Profile"
          @error="handleAvatarError"
        />
        <IconUser v-else class="h-5 w-5" />
        My Page
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

import BrandLogo from '../brand/BrandLogo.vue'
import logoUrl from '../../assets/logo.svg'

import IconDashboard from '../icons/IconDashboard.vue'
import IconTrendingUp from '../icons/IconTrendingUp.vue'
import IconSprout from '../icons/IconSprout.vue'
import IconNewspaper from '../icons/IconNewspaper.vue'
import IconMessage from '../icons/IconMessage.vue'
import IconUser from '../icons/IconUser.vue'

const router = useRouter()
const route = useRoute()
const { isLoggedIn } = useAuthStore()

const props = defineProps({
  userAvatar: { type: String, default: '' },
})

const navigation = [
  { name: '대시보드', href: '/dashboard', icon: IconDashboard },
  { name: '랭킹', href: '/rank', icon: IconTrendingUp },
  { name: '나의 농장', href: '/myfarm', icon: IconSprout },
  { name: '농촌 기사', href: '/articles', icon: IconNewspaper },
  { name: '챗봇 야치', href: '/ai-chat', icon: IconMessage },
]

// 프로필 활성화 상태 확인
const isProfileActive = computed(() => route.path === '/mypage')

// 메인으로 이동
const goToMain = () => {
  router.push('/')
}

// 유저 클릭 핸들러 (로그인 상태에 따라 분기)
const handleUserClick = () => {
  const accessToken = localStorage.getItem('accessToken')

  const hasValidToken =
    accessToken &&
    accessToken !== null &&
    accessToken.trim() !== '' &&
    accessToken.trim() !== 'null' &&
    accessToken.trim() !== 'undefined'

  if (hasValidToken) {
    router.push('/mypage')
  } else {
    router.push('/login')
  }
}

// 이미지 에러 핸들러
const handleImageError = (e) => {
  e.target.style.display = 'none'
}

const handleAvatarError = (e) => {
  e.target.style.display = 'none'
}
</script>