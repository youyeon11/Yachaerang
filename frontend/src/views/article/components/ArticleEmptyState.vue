<template>
  <div class="flex flex-col items-center justify-center py-24 animate-fade-in">
    <div class="relative mb-8">
      <div class="absolute inset-0 bg-[#FECC21]/10 rounded-full"></div>
      <div
        class="relative w-20 h-20 bg-white rounded-full shadow-sm flex items-center justify-center border border-gray-50"
      >
        <IconSearch class="w-10 h-10 text-[#FECC21]" />
        <div class="absolute bottom-4 right-4 w-3 h-3 bg-[#FECC21] rounded-full border-2 border-white"></div>
      </div>
    </div>

    <div class="text-center space-y-3 mb-12">
      <h3 class="text-2xl font-black text-gray-900 tracking-tight">검색 결과가 없어요</h3>
      <p class="text-gray-500 font-medium">
        <template v-if="keyword">
          찾으시는 <span class="text-[#e5b800] font-bold">"{{ keyword }}"</span>에 대한 결과가 없습니다.
        </template>
        <template v-else>검색어에 맞는 기사를 찾지 못했습니다.</template>
        <br />
        <span class="text-sm opacity-80">키워드를 변경하거나 전체 목록을 확인해 보세요.</span>
      </p>
    </div>

    <button
      type="button"
      @click="$emit('reset')"
      class="group flex items-center gap-2.5 px-8 py-4 rounded-2xl bg-[#FECC21] text-gray-800 font-bold shadow-lg shadow-[#FECC21]/20 hover:bg-[#e5b800] hover:-translate-y-1 transition-all active:scale-95"
    >
      <IconRefresh class="w-5 h-5 transition-transform duration-500 group-hover:rotate-180" />
      전체 기사 보기
    </button>
  </div>
</template>

<script setup>
import IconSearch from '@/components/icons/IconSearch.vue';
import IconRefresh from '@/components/icons/IconRefresh.vue';

defineProps({
  keyword: {
    type: String,
    default: '',
  },
});

defineEmits(['reset']);
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.5s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
