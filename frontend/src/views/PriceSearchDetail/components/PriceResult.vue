<template>
  <section class="result-card">
    <div class="result-header">
      <h2>조회 결과</h2>
      <div class="result-view-toggle">
        <button type="button" class="toggle-btn" :class="{ active: viewMode === 'table' }" @click="viewMode = 'table'">
          📋 표로 보기
        </button>

        <button type="button" class="toggle-btn" :class="{ active: viewMode === 'chart' }" @click="viewMode = 'chart'">
          📊 그래프로 보기
        </button>
      </div>
    </div>

    <ResultTable v-if="viewMode === 'table'" :rows="rows" />
    <ResultChart v-else :rows="rows" />
  </section>
</template>

<script setup>
import { ref } from 'vue';
import ResultTable from './Result/ResultTable.vue';
import ResultChart from './Result/ResultChart.vue';
defineProps({
  rows: { type: Array, default: () => [] },
});

const viewMode = ref('table');
</script>
