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

    <div class="flex justify-between items-end gap-3">
      <div class="flex-1 min-w-0">
        <h2 class="text-xl md:text-2xl font-black text-slate-800 leading-tight truncate">
          {{ itemName }}
          <span class="text-sm md:text-lg font-bold text-slate-400 ml-1">{{ varietyName }}</span>
        </h2>

        <div class="mt-2 flex flex-col gap-1">
          <div class="flex items-center gap-2">
            <span class="text-3xl md:text-4xl font-black text-red-500 tracking-tighter whitespace-nowrap">
              {{ averagePrice.toLocaleString() }}<span class="text-[0.5em] ml-0.5 font-bold text-red-500">원</span>
            </span>
            <div class="flex flex-col leading-none border-l border-slate-100 pl-2">
              <span class="text-[8px] font-bold text-gray-400 mb-0.5 uppercase">금년 평균가</span>
              <span class="text-[10px] md:text-sm font-black text-slate-600 whitespace-nowrap">조회 기간 내</span>
            </div>
          </div>

          <div v-if="hasLastYearData" class="flex items-center gap-2 text-xs text-slate-500">
            <span class="text-[10px] font-semibold text-gray-400 uppercase">전년 평균가</span>
            <span class="font-bold text-slate-700">{{ lastAveragePrice.toLocaleString() }}원</span>
          </div>
        </div>
      </div>

      <div class="flex items-stretch gap-2 text-[10px] md:text-xs text-slate-600">
        <div
          class="flex flex-col items-center justify-center gap-1 bg-gray-50 px-3 py-2 md:px-4 md:py-3 rounded-xl border border-gray-100 shadow-inner min-w-[80px]"
        >
          <span class="text-[9px] font-bold text-gray-400 mb-0.5">금년</span>
          <div class="flex items-center gap-3">
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

        <div
          v-if="hasLastYearData"
          class="flex flex-col items-center justify-center gap-1 bg-gray-50 px-3 py-2 md:px-4 md:py-3 rounded-xl border border-gray-100 shadow-inner min-w-[80px]"
        >
          <span class="text-[9px] font-bold text-gray-400 mb-0.5">전년</span>
          <div class="flex items-center gap-3">
            <div class="flex flex-col items-center min-w-[40px]">
              <span class="text-[8px] text-gray-400 font-bold mb-0.5">최고</span>
              <span class="text-xs md:text-base font-black text-slate-700">{{ lastMaxPrice.toLocaleString() }}</span>
            </div>
            <div class="w-[1px] h-4 bg-gray-200"></div>
            <div class="flex flex-col items-center min-w-[40px]">
              <span class="text-[8px] text-gray-400 font-bold mb-0.5">최저</span>
              <span class="text-xs md:text-base font-black text-slate-700">{{ lastMinPrice.toLocaleString() }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  priceResult: Array,
  lastYearPrices: {
    type: Array,
    default: () => [],
  },
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

const validLastPrices = computed(() =>
  Array.isArray(props.lastYearPrices)
    ? props.lastYearPrices.filter((p) => p !== null && p !== undefined)
    : []
);

const averagePrice = computed(() => {
  if (!validPrices.value.length) return 0;
  return Math.floor(validPrices.value.reduce((a, b) => a + b, 0) / validPrices.value.length);
});

const maxPrice = computed(() => (validPrices.value.length ? Math.max(...validPrices.value) : 0));
const minPrice = computed(() => (validPrices.value.length ? Math.min(...validPrices.value) : 0));

const lastAveragePrice = computed(() => {
  if (!validLastPrices.value.length) return 0;
  return Math.floor(validLastPrices.value.reduce((a, b) => a + b, 0) / validLastPrices.value.length);
});

const lastMaxPrice = computed(() => (validLastPrices.value.length ? Math.max(...validLastPrices.value) : 0));
const lastMinPrice = computed(() => (validLastPrices.value.length ? Math.min(...validLastPrices.value) : 0));

const hasLastYearData = computed(() => validLastPrices.value.length > 0);
</script>
