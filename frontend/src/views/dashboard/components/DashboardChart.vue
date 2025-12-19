<template>
  <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
    <div class="mb-4">
      <h2 class="text-lg font-semibold text-gray-900">Price Trends</h2>
      <p class="text-sm text-gray-600">Combined line and bar chart showing price movements</p>
    </div>
    <div class="h-96">
      <canvas ref="chartCanvas"></canvas>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref, watch } from 'vue';
import Chart from 'chart.js/auto';

const props = defineProps({
  rows: {
    type: Array,
    default: () => [],
  },
});

const chartCanvas = ref(null);
let chartInstance = null;

const mockData = [
  { date: 'Jan', price: 4000 },
  { date: 'Feb', price: 3000 },
  { date: 'Mar', price: 5000 },
  { date: 'Apr', price: 4500 },
  { date: 'May', price: 6000 },
  { date: 'Jun', price: 5500 },
];

function getChartSource() {
  if (props.rows && props.rows.length > 0) {
    return props.rows.map((row) => ({
      label: row.dateLabel ?? row.priceDate ?? row.date ?? '',
      value: row.priceLabel ?? row.price ?? row.avgPrice ?? null,
    }));
  }

  return mockData.map((d) => ({
    label: d.date,
    value: d.price,
  }));
}

function renderChart() {
  if (!chartCanvas.value) return;

  const source = getChartSource();
  const labels = source.map((s) => s.label);
  const data = source.map((s) => s.value);

  if (chartInstance) {
    chartInstance.data.labels = labels;
    chartInstance.data.datasets.forEach((ds) => {
      ds.data = data;
    });
    chartInstance.update();
    return;
  }

  chartInstance = new Chart(chartCanvas.value, {
    type: 'bar',
    data: {
      labels,
      datasets: [
        {
          type: 'bar',
          label: 'Price (Bar)',
          data,
          backgroundColor: '#F44323',
          borderRadius: 4,
        },
        {
          type: 'line',
          label: 'Price (Line)',
          data,
          borderColor: '#FECC21',
          backgroundColor: '#FECC21',
          borderWidth: 3,
          pointRadius: 4,
          pointBackgroundColor: '#FECC21',
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Price (₩)',
          },
        },
      },
    },
  });
}

onMounted(() => {
  renderChart();
});

watch(
  () => props.rows,
  () => {
    renderChart();
  },
  { deep: true },
);

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }
});
</script>


