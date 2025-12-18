<template>
  <div class="page-container price-search-page">
    <header class="page-header">
      <div class="page-header-left">
        <h1 class="page-title">세부 가격 검색</h1>
        <p class="page-subtitle">내가 원하는대로 검색하고 정렬할 수 있는 야채랑 농, 수산물 가격정보</p>
      </div>
    </header>

    <section class="search-card">
      <ItemSelector
        :items="itemOptions"
        :varieties="varietyOptions"
        v-model:selectedItem="selectedItem"
        v-model:selectedVariety="selectedVariety"
      />

      <div class="divider"></div>

      <div class="row row-bottom">
        <PeriodSelector :period-tabs="periodTabs" :period-type="periodType" @change="handlePeriodClick" />

        <div class="filters-col">
          <template v-if="periodType === 'day'">
            <DayPicker v-model:startDate="dayStartDate" v-model:endDate="dayEndDate" :max-date="yesterday" />
          </template>

          <template v-else-if="periodType === 'week'">
            <WeekPicker
              v-model:startDate="weekStartDate"
              v-model:endDate="weekEndDate"
              :last-week-sunday="lastWeekSunday"
            />
          </template>

          <template v-else-if="periodType === 'month'">
            <MonthPicker v-model:startDate="monthStartDate" v-model:endDate="monthEndDate" :yesterday="yesterday" />
          </template>

          <template v-else>
            <YearPicker
              :year-options="yearOptions"
              v-model:isYearDetail="isYearDetail"
              v-model:yearStart="yearStart"
              v-model:yearEnd="yearEnd"
              v-model:yearDetail="yearDetail"
              :show-detail-toggle="periodType === 'year'"
            />
          </template>
        </div>
      </div>

      <div class="actions actions-bottom">
        <button type="button" class="reset-btn" @click="resetFilters">
          <span class="reset-icon">⟳</span>
          <span>선택초기화</span>
        </button>
        <button type="button" class="favorite-btn" @click="handleAddFavorite">관심 품목 등록</button>
        <button type="button" class="search-btn" @click="handleSearch">검색하기</button>
      </div>
    </section>

    <PriceResult
      v-if="hasSearched"
      :rows="priceResult"
      :item-label="selectedItemLabel"
      :variety-label="selectedVarietyLabel"
      :period-type="periodType"
      :start-date="startDate"
      :end-date="endDate"
    />
  </div>
</template>

<script setup>
import ItemSelector from '@/views/PriceSearchDetail/components/ItemSelector.vue';
import PeriodSelector from '@/views/PriceSearchDetail/components/PeriodSelector.vue';

import DayPicker from '@/views/PriceSearchDetail/components/DateRangePicker/DayPicker.vue';
import WeekPicker from '@/views/PriceSearchDetail/components/DateRangePicker/WeekPicker.vue';
import MonthPicker from '@/views/PriceSearchDetail/components/DateRangePicker/MonthPicker.vue';
import YearPicker from '@/views/PriceSearchDetail/components/DateRangePicker/YearPicker.vue';
import PriceResult from '@/views/PriceSearchDetail/components/Result/PriceResult.vue';

import { toRefs, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { usePriceSearch } from '@/views/PriceSearchDetail/composables/usePriceSearch';

const route = useRoute();
const search = usePriceSearch();

const {
  selectedItem,
  selectedVariety,
  itemOptions,
  varietyOptions,
  priceResult,
  periodType,
  periodTabs,
  yesterday,
  lastWeekSunday,
  yearOptions,
  dayStartDate,
  dayEndDate,
  weekStartDate,
  weekEndDate,
  monthStartDate,
  monthEndDate,
  isYearDetail,
  yearStart,
  yearEnd,
  yearDetail,
  hasSearched,
} = toRefs(search);

const { handlePeriodClick, resetFilters, handleSearch, handleAddFavorite, initializeFromFavorite, initializeFromRank } =
  search;

const selectedItemLabel = computed(() => itemOptions.value.find((i) => i.value === selectedItem.value)?.label ?? '');

const selectedVarietyLabel = computed(
  () => varietyOptions.value.find((v) => v.value === selectedVariety.value)?.label ?? ''
);

const startDate = computed(() => {
  if (periodType.value === 'day') return dayStartDate.value?.toISOString().slice(0, 10);
  if (periodType.value === 'week') return weekStartDate.value?.toISOString().slice(0, 10);
  if (periodType.value === 'month') return monthStartDate.value?.toISOString().slice(0, 7);
  if (periodType.value === 'year') return yearStart.value;
  return '';
});

const endDate = computed(() => {
  if (periodType.value === 'day') return dayEndDate.value?.toISOString().slice(0, 10);
  if (periodType.value === 'week') return weekEndDate.value?.toISOString().slice(0, 10);
  if (periodType.value === 'month') return monthEndDate.value?.toISOString().slice(0, 7);
  if (periodType.value === 'year') return yearEnd.value;
  return '';
});

onMounted(async () => {
  const productCode = route.query.productCode;
  const favoritePeriodType = route.query.periodType;
  const source = route.query.source;

  if (productCode && source === 'rank' && typeof initializeFromRank === 'function') {
    await initializeFromRank(String(productCode));
    await handleSearch();
  } else if (productCode && favoritePeriodType && typeof initializeFromFavorite === 'function') {
    await initializeFromFavorite(String(productCode), String(favoritePeriodType));
    await handleSearch();
  }
});
</script>

<style>
.price-search-page {
  padding: 40px 0;
  box-sizing: border-box;
}

.search-card {
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.04);
  padding: 24px 32px;
  max-width: 1000px;
  margin: 0 auto;
}

.row {
  display: flex;
  align-items: center;
}

.row-top {
  gap: 40px;
  margin-bottom: 16px;
}

.row-bottom {
  align-items: center;
  justify-content: space-between;
}
.filters-col {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field {
  display: flex;
  align-items: center;
  gap: 12px;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: #555;
  min-width: 40px;
}

.select {
  min-width: 140px;
  padding: 6px 12px;
  font-size: 13px;
  border-radius: 999px;
  border: 1px solid #ddd;
  outline: none;
  background-color: #fff;
}

.select:focus {
  border-color: #fe7429;
}

.period-field {
  gap: 18px;
}

.period-tabs {
  display: inline-flex;
  padding: 3px;
  border-radius: 999px;
  background-color: #f5f5f7;
}

.period-tab {
  border: none;
  background: transparent;
  padding: 6px 18px;
  border-radius: 999px;
  font-size: 13px;
  cursor: pointer;
  color: #777;
  white-space: nowrap;
  word-break: keep-all;
}

.period-tab.active {
  background-color: #e53935;
  color: #fff;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-input {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #ddd;
  background-color: #fff;
  min-width: 130px;
}

.date-input.clickable {
  cursor: pointer;
  transition: border-color 0.2s, background-color 0.2s;
}

.date-input.clickable:hover {
  border-color: #e53935;
  background-color: #fff5f5;
}

.date-icon {
  font-size: 14px;
}

.date-field {
  border: none;
  outline: none;
  font-size: 13px;
  background: transparent;
  cursor: inherit;
  width: 100%;
}

.date-separator {
  font-size: 14px;
  color: #999;
}

.week-selected {
  border-color: #e53935;
  background-color: #ffecec;
}

.week-selected .date-icon {
  color: #e53935;
}
.month-picker-wrapper {
  position: relative;
}

.month-picker-popup {
  position: absolute;
  top: 42px;
  left: 0;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 12px;
  min-width: 220px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 20;
}

.month-picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.month-picker-year {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.month-nav-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 999px;
  padding: 4px 8px;
  font-size: 16px;
  line-height: 1;
  color: #666;
  transition: background-color 0.2s, color 0.2s;
}

.month-nav-btn:hover {
  background-color: #f5f5f5;
  color: #333;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
}

.month-btn {
  border-radius: 999px;
  border: 1px solid transparent;
  background-color: #fafafa;
  padding: 6px 0;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.2s, border-color 0.2s, color 0.2s;
}

.month-btn:hover {
  background-color: #ffecec;
  border-color: #ffcdd2;
}

.month-btn.selected {
  background-color: #e53935;
  border-color: #e53935;
  color: #fff;
}

.month-btn.disabled {
  background-color: #f5f5f5;
  border-color: #eee;
  color: #ccc;
  cursor: not-allowed;
}

.year-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.year-detail-toggle {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.actions-bottom {
  margin-top: 12px;
  justify-content: flex-end;
}

.reset-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border-radius: 999px;
  border: 1px solid #ddd;
  background-color: #fff;
  padding: 8px 14px;
  font-size: 13px;
  cursor: pointer;
  color: #555;
}

.reset-btn:hover {
  background-color: #f5f5f5;
}

.reset-icon {
  font-size: 13px;
}

.favorite-btn {
  border: 1px solid #ffd54f;
  border-radius: 999px;
  padding: 9px 22px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  background-color: #ffecb3;
  color: #8d6e00;
}

.favorite-btn:hover {
  background-color: #ffe082;
}

.search-btn {
  border: none;
  border-radius: 999px;
  padding: 9px 22px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  background-color: #e53935;
  color: #fff;
}

.search-btn:hover {
  opacity: 0.9;
}

.divider {
  height: 1px;
  background-color: #f0f0f0;
  margin: 4px 0;
}

.result-card {
  max-width: 1000px;
  margin: 24px auto 0;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.04);
  padding: 16px 24px 20px;
}

.result-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.result-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.result-table th,
.result-table td {
  border-bottom: 1px solid #eee;
  padding: 8px 4px;
}

.result-table th {
  text-align: left;
  color: #666;
  font-weight: 600;
}

.result-table td {
  color: #333;
}

:deep(.vc-popover-content) {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

:deep(.vc-red) {
  --vc-accent-50: #ffebee;
  --vc-accent-100: #ffcdd2;
  --vc-accent-200: #ef9a9a;
  --vc-accent-300: #e57373;
  --vc-accent-400: #ef5350;
  --vc-accent-500: #e53935;
  --vc-accent-600: #e53935;
  --vc-accent-700: #d32f2f;
  --vc-accent-800: #c62828;
  --vc-accent-900: #b71c1c;
}
:deep(.vc-highlight) {
  border-radius: 0 !important;
}

:deep(.vc-highlight-base-start) {
  border-radius: 50% 0 0 50% !important;
}

:deep(.vc-highlight-base-end) {
  border-radius: 0 50% 50% 0 !important;
}

:deep(.vc-highlight-bg-light) {
  background-color: rgba(229, 57, 53, 0.15) !important;
}

:deep(.vc-day-content:hover) {
  background-color: rgba(229, 57, 53, 0.25) !important;
}

@media (max-width: 768px) {
  .price-search-page {
    padding: 20px 0;
  }

  .search-card {
    padding: 16px 16px 20px;
  }

  .row-top,
  .row-bottom {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .row-bottom-right {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    width: 100%;
  }

  .field {
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    gap: 12px;
    width: 100%;
  }

  .field-label {
    min-width: auto;
    white-space: nowrap;
  }

  .period-tabs {
    width: 100%;
  }

  .period-tab {
    flex: 1;
    text-align: center;
  }

  .date-range {
    width: 100%;
    justify-content: space-between;
    flex-wrap: wrap;
  }

  .date-input {
    flex: 1;
    min-width: 120px;
  }

  .actions {
    width: 100%;
    justify-content: flex-end;
  }
}

.reset-btn,
.search-btn {
  word-break: keep-all;
  white-space: nowrap;
  writing-mode: horizontal-tb;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  padding: 0 24px;
  border-radius: 999px;
}

.actions {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}
</style>
