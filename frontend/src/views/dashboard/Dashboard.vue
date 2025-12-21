<template>
  <main class="flex flex-1 flex-col gap-4 overflow-y-auto p-4 md:p-6 bg-gray-50 font-sans">
    <div class="flex justify-between items-end mb-1 gap-4">
      <div class="min-w-0">
        <h1 class="text-xl md:text-2xl font-bold tracking-tight text-gray-900">세부 가격 검색</h1>

        <p class="text-gray-500 text-[10px] md:text-xs">전국 주요 시장 상세 시세 분석 데이터</p>
      </div>

      <FavoriteButton @click="handleAddFavorite" />
    </div>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <div class="col-span-12 lg:col-span-3 lg:col-start-10 order-1 lg:order-2 flex flex-col gap-4">
        <DashboardFilter
          v-model:selectedItem="selectedItem"
          v-model:selectedVariety="selectedVariety"
          v-model:dayStartDate="dayStartDate"
          v-model:dayEndDate="dayEndDate"
          v-model:weekStartDate="weekStartDate"
          v-model:weekEndDate="weekEndDate"
          v-model:monthStartDate="monthStartDate"
          v-model:monthEndDate="monthEndDate"
          v-model:yearStart="yearStart"
          v-model:yearEnd="yearEnd"
          :yearOptions="yearOptions"
          :itemOptions="itemOptions"
          :varietyOptions="varietyOptions"
          :periodTabs="periodTabs"
          :currentPeriod="periodType"
          :selectedItemName="selectedItemLabel"
          @updatePeriod="handlePeriodClick"
          @search="handleSearch"
        />

        <RecentViewedItems :items="recentItems" @select="handleRecentSelect" @clear="clearRecentSearches" />
      </div>

      <div class="col-span-12 lg:col-span-9 order-2 lg:order-1 flex flex-col gap-4">
        <DashboardSummary
          :priceResult="priceResult"
          :itemName="selectedItemLabel"
          :varietyName="selectedVarietyLabel"
          :hasSearched="hasSearched"
          :periodType="periodType"
          :dateRangeLabel="currentDateRangeLabel"
        />

        <div
          v-if="!hasSearched"
          class="bg-white p-20 rounded-xl border border-gray-200 text-center text-gray-400 shadow-sm"
        >
          <p class="animate-pulse">데이터를 조회 중입니다...</p>
        </div>

        <EmptyResult v-else-if="hasSearched && (!priceResult || priceResult.length === 0)" @reset="resetFilters" />

        <template v-else>
          <ResultGraph :chartData="formattedChartData" />
          <ResultTable
            :paginatedData="paginatedData"
            :totalPages="totalPages"
            :currentPage="currentPage"
            @updatePage="(p) => (currentPage = p)"
          />
        </template>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, computed, watch, onMounted, nextTick } from 'vue';
import { usePriceSearch } from '@/views/dashboard/composables/usePriceSearch';
import DashboardFilter from '@/views/dashboard/components/DashboardFilter.vue';
import DashboardSummary from '@/views/dashboard/components/DashboardSummary.vue';
import ResultGraph from '@/views/dashboard/components/ResultGraph.vue';
import ResultTable from '@/views/dashboard/components/ResultTable.vue';
import FavoriteButton from '@/views/dashboard/components/FavoriteButton.vue';
import EmptyResult from '@/views/dashboard/components/EmptyResult.vue';
import RecentViewedItems from '@/views/dashboard/components/RecentViewedItems.vue';

const {
  selectedItem,
  selectedVariety,
  itemOptions,
  varietyOptions,
  priceResult,
  lastYearPrices,
  periodType,
  periodTabs,
  yearOptions,
  dayStartDate,
  dayEndDate,
  weekStartDate,
  weekEndDate,
  monthStartDate,
  monthEndDate,
  yearStart,
  yearEnd,
  hasSearched,
  recentItems,
  handlePeriodClick,
  handleSearch,
  handleAddFavorite,
  applyRecentItem,
  clearRecentSearches,
} = usePriceSearch();

const selectedItemLabel = computed(() => {
  if (!selectedItem.value) return '품목 선택';
  const target = itemOptions.value.find((opt) => opt.value === selectedItem.value);
  return target ? target.label : '품목 선택';
});

const selectedVarietyLabel = computed(() => {
  if (!selectedVariety.value) return '품종 선택';
  const target = varietyOptions.value.find((o) => o.value === selectedVariety.value);
  const label = target ? target.label : '품종 선택';
  return label.replace(/-/g, ', ');
});

const currentDateRangeLabel = computed(() => {
  if (periodType.value === 'day') {
    return `${dayStartDate.value || ''} ~ ${dayEndDate.value || ''}`;
  } else if (periodType.value === 'week') {
    return `${weekStartDate.value || ''} ~ ${weekEndDate.value || ''}`;
  } else if (periodType.value === 'month') {
    return `${monthStartDate.value || ''} ~ ${monthEndDate.value || ''}`;
  } else if (periodType.value === 'year') {
    return `${yearStart.value || ''}년 ~ ${yearEnd.value || ''}년`;
  }
  return '';
});

// --- Computed: 차트 데이터 포맷팅 ---
const formattedChartData = computed(() => {
  const labels = priceResult.value.map((item) => item.dateLabel);
  const thisPrices = priceResult.value.map((item) => item.priceLabel);

  let lastPrices;

  // 일간 조회 + 전년 데이터가 있는 경우: 실제 전년 동기간 값 사용
  if (periodType.value === 'day' && Array.isArray(lastYearPrices?.value) && lastYearPrices.value.length) {
    lastPrices = priceResult.value.map((_, idx) => {
      const val = lastYearPrices.value[idx];
      return typeof val === 'number' ? val : null;
    });
  } else {
    // 그 외의 경우(주/월/년 등)는 기존 랜덤 로직 유지
    lastPrices = priceResult.value.map((item, index) => {
      if (!item.priceLabel) return null;
      const randomFactor = 0.85 + (Math.sin(index) * 0.15 + 0.1);
      return Math.floor(item.priceLabel * randomFactor);
    });
  }

  return {
    labels,
    thisPrices,
    lastPrices,
  };
});

// --- Pagination: 테이블 데이터 처리 ---
const currentPage = ref(1);
const itemsPerPage = 5;
const totalPages = computed(() => {
  if (priceResult.value.length === 0) return 1;
  return Math.ceil(priceResult.value.length / itemsPerPage);
});
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;

  return priceResult.value.slice(start, end).map((item, index) => {
    const thisPrice = item.priceLabel || 0;

    const globalIndex = start + index;
    let lastPrice = 0;

    // 일간 + 전년 데이터가 있으면 실제 전년 값 사용
    if (periodType.value === 'day' && Array.isArray(lastYearPrices?.value) && lastYearPrices.value.length) {
      const val = lastYearPrices.value[globalIndex];
      lastPrice = typeof val === 'number' ? val : 0;
    } else {
      // 그 외는 기존 랜덤 전년값 유지
      const randomFactor = 0.85 + (Math.sin(globalIndex) * 0.15 + 0.1);
      lastPrice = thisPrice ? Math.floor(thisPrice * randomFactor) : 0;
    }

    const prevItem = priceResult.value[globalIndex + 1];
    const dailyDiff = prevItem ? thisPrice - prevItem.priceLabel : 0;

    const yoyDiff = lastPrice > 0 ? thisPrice - lastPrice : 0;

    return {
      date: item.dateLabel,
      thisPrice: thisPrice,
      lastPrice: lastPrice,
      dailyDiff: dailyDiff,
      yoyDiff: yoyDiff,
    };
  });
});

const STORAGE_KEY = 'price-search-state';

watch(
  () => ({
    selectedItem: selectedItem.value,
    selectedVariety: selectedVariety.value,
    periodType: periodType.value,
    dayStartDate: dayStartDate.value,
    dayEndDate: dayEndDate.value,
    weekStartDate: weekStartDate.value,
    weekEndDate: weekEndDate.value,
    monthStartDate: monthStartDate.value,
    monthEndDate: monthEndDate.value,
    yearStart: yearStart.value,
    yearEnd: yearEnd.value,
    priceResult: priceResult.value,
    hasSearched: hasSearched.value,
    currentPage: currentPage.value,
  }),

  (state) => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  },
  { deep: true }
);

onMounted(async () => {
  const saved = localStorage.getItem(STORAGE_KEY);

  if (saved) {
    const state = JSON.parse(saved);

    selectedItem.value = state.selectedItem || '';
    selectedVariety.value = state.selectedVariety || '';
    periodType.value = state.periodType || 'year';

    dayStartDate.value = state.dayStartDate ?? null;
    dayEndDate.value = state.dayEndDate ?? null;
    weekStartDate.value = state.weekStartDate ?? null;
    weekEndDate.value = state.weekEndDate ?? null;
    monthStartDate.value = state.monthStartDate ?? null;
    monthEndDate.value = state.monthEndDate ?? null;
    yearStart.value = state.yearStart ?? '';
    yearEnd.value = state.yearEnd ?? '';

    if (state.selectedItem && state.selectedVariety) {
      await handleSearch();
    }

    currentPage.value = state.currentPage || 1;
  } else {
    // 최초 접속 시 기본값 (계란)
    selectedItem.value = '9903';
    selectedVariety.value = 'KM-9903-23-71';
    periodType.value = 'day';
    dayStartDate.value = '2025-11-01';
    dayEndDate.value = '2025-12-20';

    await handleSearch();
  }
});

const handleRecentSelect = async (item) => {
  await applyRecentItem(item);
};
</script>
