<template>
  <section class="result-card">
    <div class="result-header">
      <h2 class="result-title">조회 결과</h2>

      <div v-if="rows && rows.length" class="result-view-toggle">
        <button type="button" class="toggle-btn" :class="{ active: viewMode === 'table' }" @click="handleViewTable">
          표로 보기
        </button>
        <button type="button" class="toggle-btn" :class="{ active: viewMode === 'chart' }" @click="handleViewChart">
          그래프로 보기
        </button>
      </div>
    </div>

    <div v-if="viewMode === 'table'">
      <table class="result-table">
        <thead>
          <tr>
            <th>일자</th>
            <th>가격</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in rows" :key="row.dateLabel">
            <td>{{ row.dateLabel }}</td>
            <td>{{ row.priceLabel != null ? row.priceLabel.toLocaleString() : '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="chart-wrapper">
      <canvas ref="chartCanvas"></canvas>
    </div>
  </section>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from 'vue';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

const props = defineProps({
  rows: { type: Array, default: () => [] },
});

const viewMode = ref('table');
const chartCanvas = ref(null);
let chartInstance = null;

// ----------------------
// destroyChart
// ----------------------
const destroyChart = () => {
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }
};

// ----------------------
// renderChart
// ----------------------
const renderChart = async () => {
  await nextTick();

  const labels = props.rows.map((row) => row.dateLabel);
  const data = props.rows.map((row) => (row.priceLabel != null ? Number(row.priceLabel) : null));

  if (!chartCanvas.value || !labels.length) {
    destroyChart();
    return;
  }

  const numericData = data.filter((v) => typeof v === 'number' && !Number.isNaN(v));
  if (!numericData.length) {
    destroyChart();
    return;
  }

  let min = Math.min(...numericData);
  let max = Math.max(...numericData);

  // 가격 차이가 너무 작은 경우 강제 확대
  const diff = max - min;
  if (diff < min * 0.05) {
    const expand = min * 0.15 || 10;
    min -= expand;
    max += expand;
  }

  const padding = (max - min) * 0.15;
  const yMin = Math.max(0, min - padding);
  const yMax = max + padding;

  const ctx = chartCanvas.value.getContext('2d');
  if (!ctx) return;

  destroyChart();

  chartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels,
      datasets: [
        {
          type: 'bar',
          label: '가격',
          data,
          backgroundColor: 'rgba(229,57,53,0.25)',
          borderColor: 'rgba(229,57,53,0.8)',
          borderWidth: 1,
          borderRadius: 6,
          hoverBackgroundColor: 'rgba(229,57,53,0.45)',
          yAxisID: 'y',
        },
        {
          type: 'line',
          label: '가격 추이',
          data,
          borderColor: '#e53935',
          backgroundColor: 'rgba(229,57,53,0.1)',
          borderWidth: 2,
          tension: 0.4,
          pointRadius: 3,
          pointHoverRadius: 5,
          pointBackgroundColor: '#e53935',
          yAxisID: 'y',
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: {
        legend: { position: 'top' },
        tooltip: {
          callbacks: {
            label(context) {
              const value = context.parsed.y;
              return `${context.dataset.label}: ${Number(value).toLocaleString()}원`;
            },
          },
        },
      },
      scales: {
        x: {
          ticks: { autoSkip: true, maxRotation: 0, font: { size: 11 } },
          grid: { display: false },
        },
        y: {
          min: yMin,
          max: yMax,
          ticks: {
            callback(value) {
              return Number(value).toLocaleString();
            },
            font: { size: 11 },
          },
          grid: { color: 'rgba(0,0,0,0.07)', lineWidth: 1 },
        },
      },
    },
  });
};

// ----------------------
// View Mode handlers
// ----------------------
const handleViewTable = () => {
  viewMode.value = 'table';
  destroyChart();
};

const handleViewChart = () => {
  viewMode.value = 'chart';
  renderChart();
};

// ----------------------
// Watch rows
// ----------------------
watch(
  () => props.rows,
  () => {
    if (viewMode.value === 'chart') renderChart();
  },
  { deep: true }
);

onMounted(() => {
  if (viewMode.value === 'chart') renderChart();
});

onBeforeUnmount(() => destroyChart());
</script>
