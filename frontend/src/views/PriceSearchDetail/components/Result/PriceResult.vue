<template>
  <section class="result-card">
    <div class="result-header">
      <div>
        <h2>조회 결과</h2>
        <p class="result-desc">{{ summaryText }}</p>
      </div>

      <ViewModeToggle v-model="viewMode" />
    </div>

    <ResultTable v-if="viewMode === 'table'" :rows="rows" />
    <ResultChart v-else :rows="rows" />
  </section>
</template>

<script setup>
import { ref, computed } from 'vue';
import ViewModeToggle from './ViewModelTogle.vue';
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

  return `${item} ${variety}, ${period} ${range}의 결과입니다.`;
});
</script>

<style scoped>
.result-desc {
  font-size: 13px;
  color: #666;
  margin-top: 4px;
}
</style>
