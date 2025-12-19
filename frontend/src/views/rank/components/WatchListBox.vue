<template>
  <div class="w-80">
    <div class="sticky top-0 rounded-lg border border-gray-200 bg-white shadow-sm">
      <!-- 로그인 안 한 상태 -->
      <div v-if="!isLoggedIn" class="flex flex-col items-center justify-center gap-4 p-8 text-center min-h-[220px]">
        <div class="text-gray-500">
          <LockIcon class="h-8 w-8" />
        </div>
        <p class="text-sm font-semibold text-gray-900">로그인 서비스입니다.</p>
        <button 
          class="rounded-lg bg-gray-900 px-4 py-2.5 text-sm font-semibold text-white transition-opacity hover:opacity-90"
          @click="goLogin"
        >
          로그인 하러가기
        </button>
      </div>

      <!-- 로그인 한 상태 -->
      <template v-else>
        <!-- Header -->
        <div class="flex items-center justify-between border-b border-gray-200 p-4">
          <div>
            <h2 class="flex items-center gap-2 text-lg font-semibold text-gray-900">
              <IconHeart class="h-5 w-5 fill-[#F44323] text-[#F44323]" />
              나의 품목
            </h2>
            <p class="text-sm text-gray-600">관심 품목을 빠르게 확인하세요</p>
          </div>
          <button 
            class="rounded-md bg-gray-100 px-3 py-1.5 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-200"
            @click="$emit('edit')"
          >
            수정
          </button>
        </div>

        <!-- List -->
        <div class="max-h-[600px] overflow-y-auto p-4">
          <div class="space-y-3">
            <button
              v-for="item in items"
              :key="item.favoriteId ?? item.productCode ?? item.displayName"
              class="w-full cursor-pointer rounded-lg border border-gray-200 p-3 text-left transition-colors hover:bg-gray-50"
              type="button"
              @click="$emit('select', item)"
            >
              <p class="font-medium text-gray-900">{{ item.displayName ?? item }}</p>
              <p v-if="item.periodLabel" class="text-sm text-gray-600">{{ item.periodLabel }}</p>
            </button>

            <p v-if="items.length === 0" class="py-4 text-center text-sm text-gray-500">
              아직 등록한 관심 품목이 없어요
            </p>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import LockIcon from '@/components/icons/LockIcon.vue'
import IconHeart from '@/components/icons/IconHeart.vue'

const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  },
})

defineEmits(['select', 'edit'])

const router = useRouter()

const isLoggedIn = computed(() => {
  const token = localStorage.getItem('accessToken')
  return token && token !== 'null' && token !== 'undefined' && token.trim() !== ''
})

const goLogin = () => {
  router.push('/login')
}
</script>