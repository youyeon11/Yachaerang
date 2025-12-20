<template>
  <div class="bg-white p-5 md:p-7 rounded-2xl shadow-sm border border-gray-200" v-if="hasSearched">
    <div class="flex items-center gap-2 mb-4">
      <span class="px-2 py-0.5 bg-slate-100 text-slate-600 text-[10px] font-bold rounded-md">
        {{ periodLabel }}
      </span>
      <span class="text-[10px] text-gray-500 font-medium">
        {{ dateRangeLabel }}
      </span>
    </div>

    <div class="flex justify-between items-end gap-2">
      <div class="flex-1 min-w-0">
        <h2 class="text-xl md:text-2xl font-black text-slate-800 leading-tight truncate">
          {{ itemName }}
          <span class="text-sm md:text-lg font-bold text-slate-400 ml-1">{{ varietyName }}</span>
        </h2>

        <div class="mt-2 flex items-center gap-2">
          <span class="text-3xl md:text-4xl font-black text-red-500 tracking-tighter whitespace-nowrap">
            {{ averagePrice.toLocaleString() }}<span class="text-[0.5em] ml-0.5 font-bold text-red-500">원</span>
          </span>
          <div class="flex flex-col leading-none border-l border-slate-100 pl-2">
            <span class="text-[8px] font-bold text-gray-400 mb-0.5 uppercase">평균가</span>
            <span class="text-[10px] md:text-sm font-black text-slate-600 whitespace-nowrap">조회기준</span>
          </div>
        </div>
      </div>

      <div
        class="flex items-center gap-2 md:gap-4 bg-gray-50 px-3 py-2 md:px-4 md:py-3 rounded-xl border border-gray-100 shadow-inner shrink-0"
      >
        <div class="flex flex-col items-center min-w-[40px]">
          <span class="text-[8px] text-gray-400 font-bold mb-0.5">최고</span>
          <span class="text-xs md:text-base font-black text-slate-700">{{ maxPrice.toLocaleString() }}</span>
        </div>
        <div class="w-[1px] h-4 bg-gray-200"></div>
        <div class="flex flex-col items-center min-w-[40px]">
          <span class="text-[8px] text-gray-400 font-bold mb-0.5">최저</span>
          <span class="text-xs md:text-base font-black text-slate-700">{{ minPrice.toLocaleString() }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  priceResult: Array,
  itemName: String,
  varietyName: String,
  hasSearched: Boolean,
  periodType: String,
  dateRangeLabel: String,
});

const periodMap = {
  day: '일간 시세',
  week: '주간 시세',
  month: '월간 시세',
  year: '연간 시세',
};

const periodLabel = computed(() => periodMap[props.periodType] || '시세 정보');

const validPrices = computed(() =>
  props.priceResult.map((r) => r.priceLabel).filter((p) => p !== null && p !== undefined)
);

const averagePrice = computed(() => {
  if (!validPrices.value.length) return 0;
  return Math.floor(validPrices.value.reduce((a, b) => a + b, 0) / validPrices.value.length);
});

const maxPrice = computed(() => (validPrices.value.length ? Math.max(...validPrices.value) : 0));
const minPrice = computed(() => (validPrices.value.length ? Math.min(...validPrices.value) : 0));
</script>
