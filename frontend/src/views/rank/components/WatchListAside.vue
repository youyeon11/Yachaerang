<template>
  <aside class="w-full lg:w-[320px] xl:w-[360px] flex-shrink-0 lg:pt-[116px] transition-all">
    <div class="bg-white border border-gray-300 rounded-2xl p-6 sm:p-7 shadow-sm">
      <div class="flex justify-between items-center mb-8">
        <h2 class="text-xl font-bold text-gray-900">나의 품목정보</h2>
      </div>

      <div class="space-y-7">
        <div
          v-for="fav in items"
          :key="fav.favoriteId"
          @click="$emit('select', fav)"
          class="flex justify-between items-center border-b border-gray-50 pb-4 last:border-0 last:pb-0 cursor-pointer group min-w-0"
        >
          <div class="flex-1 min-w-0 mr-4">
            <p class="text-[13px] font-bold text-gray-600 mb-1">
              <span class="bg-gray-200 inline-block px-1.5 py-0.5 rounded text-[11px]">{{ fav.periodLabel }}</span>
            </p>
            <p class="text-base font-bold text-gray-800 group-hover:text-gray-600 transition-colors truncate" :title="fav.displayName">
              {{ fav.displayName }}
            </p>
          </div>
          <button @click.stop="$emit('remove', fav.favoriteId)" class="text-sm text-red-400 hover:text-red-600 flex-shrink-0 p-1 font-bold">✕</button>
        </div>
      </div>

      <div class="mt-10 p-5 bg-gray-50 rounded-xl border border-gray-100">
        <p class="text-sm text-gray-700 font-bold mb-2 flex items-center">
          <span class="mr-2 text-red-500">💡</span>
          시세 정보 안내
        </p>
        <p class="text-xs text-gray-500 leading-relaxed">품목별 상세 시세는 각 카드를 클릭하시면 확인하실 수 있습니다.</p>
      </div>
    </div>
  </aside>
</template>

<script setup>
defineProps({
  items: Array,
});
defineEmits(["select", "remove"]);
</script>
