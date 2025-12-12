<template>
  <section class="result-card">
    <div class="result-header">
      <div class="result-header-text">
        <h2>조회 결과</h2>
        <p class="result-desc">{{ summaryText }}</p>
      </div>

      <ViewModelToggle v-if="!isEmptyResult" v-model="viewMode" />
    </div>

    <div v-if="hasStats" class="result-stats">
      <div class="stat-card primary">
        <span class="stat-label">최근 가격</span>
        <span class="stat-value">{{ formatPrice(latestPrice) }}</span>
        <span class="stat-sub">마지막 일자의 가격</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">평균 가격</span>
        <span class="stat-value">{{ formatPrice(avgPrice) }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">최고 가격</span>
        <span class="stat-value">{{ formatPrice(maxPrice) }}</span>
      </div>
      <div class="stat-card">
        <span class="stat-label">최저 가격</span>
        <span class="stat-value">{{ formatPrice(minPrice) }}</span>
      </div>
    </div>

    <div class="result-body">
      <div v-if="isEmptyResult" class="empty-result">조회 결과가 없습니다.</div>

      <ResultTable v-else-if="viewMode === 'table'" :rows="formattedRows" />
      <ResultChart v-else :rows="formattedRows" />
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue';
import ViewModelToggle from './ViewModelTogle.vue';
import ResultTable from './ResultTable.vue';
import ResultChart from './ResultChart.vue';

const props = defineProps({
  rows: {
    type: Array,
    default: () => [],
  },
  itemLabel: String,
  varietyLabel: String,
  periodType: String, // 'year' | 'month' | 'week' | 'day'
  startDate: String,
  endDate: String,
});

const viewMode = ref('table');

const numericPrices = computed(() =>
  (props.rows || [])
    .map((r) => (r.priceLabel != null ? Number(r.priceLabel) : null))
    .filter((v) => typeof v === 'number' && !Number.isNaN(v))
);

const hasStats = computed(() => numericPrices.value.length > 0);

const minPrice = computed(() => (hasStats.value ? Math.min(...numericPrices.value) : null));

const maxPrice = computed(() => (hasStats.value ? Math.max(...numericPrices.value) : null));

const avgPrice = computed(() => {
  if (!hasStats.value) return null;
  const sum = numericPrices.value.reduce((acc, v) => acc + v, 0);
  return Math.round(sum / numericPrices.value.length);
});

const latestPrice = computed(() => (hasStats.value ? numericPrices.value[numericPrices.value.length - 1] : null));

const formatPrice = (value) => {
  if (value == null || Number.isNaN(value)) return '-';
  return `${Number(value).toLocaleString()}원`;
};
const formattedRows = computed(() =>
  (props.rows || []).map((row) => ({
    ...row,
    priceRaw: Number(row.priceLabel), // 👉 차트용 (숫자)
    priceLabel: formatPrice(row.priceLabel), // 👉 테이블용 (문자열)
  }))
);
const periodTextMap = {
  year: '연간',
  month: '월간',
  week: '주간',
  day: '일간',
};

const summaryText = computed(() => {
  const period = periodTextMap[props.periodType] ?? '';
  const item = props.itemLabel ?? '';
  const variety = props.varietyLabel ?? '';
  const range = props.startDate && props.endDate ? `${props.startDate} ~ ${props.endDate}` : '';

  if (!item && !variety && !period && !range) {
    return `선택한 조건으로 조회한 결과입니다.`;
  }

  return `${item} ${variety} · ${period} ${range} 기준 데이터입니다.`;
});
const isEmptyResult = computed(() => {
  return !props.rows || props.rows.length === 0;
});
</script>

<style scoped>
.result-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}

.result-header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.result-desc {
  font-size: 13px;
  color: #666;
}

.result-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 14px;
}

.stat-card {
  border-radius: 12px;
  background: #fafafa;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.stat-card.primary {
  background: linear-gradient(135deg, #ffe4e4, #fff5f5);
}

.stat-label {
  font-size: 11px;
  color: #888;
}

.stat-value {
  font-size: 15px;
  font-weight: 600;
  color: #222;
}

.stat-sub {
  font-size: 11px;
  color: #999;
}

.result-body {
  margin-top: 4px;
}

@media (max-width: 768px) {
  .result-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .result-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
