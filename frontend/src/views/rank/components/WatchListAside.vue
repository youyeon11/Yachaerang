<template>
  <aside class="w-full lg:w-[320px] xl:w-[360px] flex-shrink-0 transition-all">
    <div class="bg-white border border-gray-300 rounded-2xl p-6 sm:p-7 shadow-sm mb-6">
      <div class="flex justify-between items-center mb-8">
        <h2 class="text-xl font-bold text-gray-900">나의 품목정보</h2>
      </div>

      <div class="min-h-[160px] relative">
        <div v-if="isAuthenticated">
          <div v-if="items && items.length > 0" class="space-y-7 max-h-[480px] overflow-y-auto pr-2 custom-scrollbar">
            <div
              v-for="fav in items"
              :key="fav.favoriteId"
              @click="$emit('select', fav)"
              class="flex justify-between items-center border-b border-gray-100 pb-4 last:border-0 last:pb-0 cursor-pointer group min-w-0"
            >
              <div class="flex-1 min-w-0 mr-4">
                <p class="text-[13px] font-bold text-gray-600 mb-1">
                  <span class="bg-gray-200 inline-block px-1.5 py-0.5 rounded text-[11px]">{{ fav.periodLabel }}</span>
                </p>
                <p
                  class="text-base font-bold text-gray-800 group-hover:text-gray-600 transition-colors truncate"
                  :title="fav.displayName"
                >
                  {{ fav.displayName }}
                </p>
              </div>
              <button
                @click.stop="$emit('remove', fav.favoriteId)"
                class="text-sm text-red-400 hover:text-red-600 flex-shrink-0 p-1 font-bold"
              >
                ✕
              </button>
            </div>
          </div>

          <p v-else class="text-sm text-gray-400 text-center pt-4">
            아직 등록된 나의 품목이 없습니다. <br />대시보드에서 나의 품목을 추가해 보세요.
          </p>
        </div>

        <div v-else>
          <div class="space-y-6 opacity-40 blur-sm pointer-events-none select-none px-2">
            <div v-for="n in 3" :key="n" class="flex justify-between items-center border-b border-gray-100 pb-4">
              <div class="flex-1">
                <div class="h-3 w-12 bg-gray-200 rounded mb-2"></div>
                <div class="h-4 w-24 bg-gray-300 rounded"></div>
              </div>
              <div class="h-4 w-4 bg-gray-200 rounded"></div>
            </div>
          </div>

          <div class="absolute inset-0 flex flex-col items-center justify-center text-center px-4">
            <p class="text-sm text-gray-800 font-semibold mb-2">
              로그인 후 나의 품목을 등록하면<br />
              자주 보는 품목을 볼 수 있어요.
            </p>
            <button
              type="button"
              class="mt-4 inline-flex items-center justify-center px-5 py-2.5 text-xs font-bold text-white bg-red-500 rounded-full hover:bg-red-600 transition-colors shadow-md"
              @click="goLogin"
            >
              로그인 하러 가기
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="p-5 bg-gray-50 rounded-xl border border-gray-200 shadow-sm">
      <p class="text-sm text-gray-700 font-bold mb-2 flex items-center">
        <span class="mr-2 text-red-500">💡</span>
        시세 정보 안내
      </p>
      <p class="text-xs text-gray-500 leading-relaxed">품목별 상세 시세는 각 카드를 클릭하시면 확인하실 수 있습니다.</p>
    </div>
  </aside>
</template>

<script setup>
import { useRouter } from 'vue-router';

const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  },
  isAuthenticated: {
    type: Boolean,
    default: false,
  },
});

defineEmits(['select', 'remove', 'edit']);

const router = useRouter();

const goLogin = () => {
  router.push({
    name: 'login',
    query: { redirect: router.currentRoute.value.fullPath },
  });
};
</script>
<style scoped>
li {
  will-change: transform, box-shadow;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}

.truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
