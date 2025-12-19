<template>
  <main class="flex-1 overflow-y-auto p-8 bg-gray-50">
    <div class="mx-auto max-w-7xl space-y-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight text-gray-900">Dashboard</h1>
          <p class="text-gray-600">Track agricultural product prices over time</p>
        </div>
      </div>

      <!-- Filters -->
      <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div class="mb-4">
          <h2 class="text-lg font-semibold text-gray-900">세부 가격 검색</h2>
          <p class="text-sm text-gray-600">품목, 품종, 기간을 선택해 가격 추이를 확인해 보세요.</p>
        </div>

        <section class="search-card !shadow-none !p-0 !bg-transparent">
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
            <button
              type="button"
              class="favorite-btn flex items-center gap-1"
              @click="
                async () => {
                  await handleAddFavorite();
                  isLiked = true;
                }
              "
            >
              <IconHeart class="h-4 w-4" :class="isLiked ? 'fill-[#F44323]' : 'fill-none'" />
              <span>관심 품목 등록</span>
            </button>
            <button type="button" class="search-btn" @click="handleSearch">검색하기</button>
          </div>
        </section>
      </div>

      <!-- Chart -->
      <DashboardChart :rows="priceResult" />
    </div>
  </main>
</template>

<script setup>
import { ref, toRefs } from 'vue';
import IconHeart from '../../components/icons/IconHeart.vue';

import { usePriceSearch } from '@/views/dashboard/composables/usePriceSearch';
import ItemSelector from '@/views/dashboard/components/ItemSelector.vue';
import PeriodSelector from '@/views/dashboard/components/PeriodSelector.vue';
import DayPicker from '@/views/dashboard/components/DateRangePicker/DayPicker.vue';
import WeekPicker from '@/views/dashboard/components/DateRangePicker/WeekPicker.vue';
import MonthPicker from '@/views/dashboard/components/DateRangePicker/MonthPicker.vue';
import YearPicker from '@/views/dashboard/components/DateRangePicker/YearPicker.vue';
import DashboardChart from '@/views/dashboard/components/DashboardChart.vue';

const isLiked = ref(false);

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
} = toRefs(search);

const { handlePeriodClick, resetFilters, handleSearch, handleAddFavorite } = search;
</script>

<style scoped>
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

::deep(.vc-popover-content) {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

::deep(.vc-red) {
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

::deep(.vc-highlight) {
  border-radius: 0 !important;
}

::deep(.vc-highlight-base-start) {
  border-radius: 50% 0 0 50% !important;
}

::deep(.vc-highlight-base-end) {
  border-radius: 0 50% 50% 0 !important;
}

::deep(.vc-highlight-bg-light) {
  background-color: rgba(229, 57, 53, 0.15) !important;
}

::deep(.vc-day-content:hover) {
  background-color: rgba(229, 57, 53, 0.25) !important;
}

@media (max-width: 768px) {
  .search-card {
    padding: 16px 16px 20px;
  }

  .row-top,
  .row-bottom {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
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
</style>
