<template>
  <div class="h-72 md:h-96">
    <canvas ref="lineCanvas"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import Chart from 'chart.js/auto';

const props = defineProps({
  labels: Array,
  thisPrices: Array,
  lastPrices: Array,
  maxIdx: Number,
  minIdx: Number,
});

const lineCanvas = ref(null);
let lineChart = null;

const initChart = () => {
  if (!lineCanvas.value) return;
  if (lineChart) lineChart.destroy();

  lineChart = new Chart(lineCanvas.value.getContext('2d'), {
    type: 'line',
    data: {
      labels: props.labels,
      datasets: [
        {
          label: '금년',
          data: props.thisPrices,
          borderColor: '#475569',
          borderWidth: 2,
          pointRadius: props.thisPrices.map((_, i) => (i === props.maxIdx || i === props.minIdx ? 4 : 0)),
          pointBackgroundColor: props.thisPrices.map((_, i) =>
            i === props.maxIdx ? '#f43f5e' : i === props.minIdx ? '#3b82f6' : '#475569'
          ),
          pointBorderColor: '#fff',
          tension: 0,
          fill: false,
          zIndex: 10,
        },
        {
          label: '전년',
          data: props.lastPrices,
          borderColor: 'transparent',
          backgroundColor: 'rgba(226, 232, 240, 0.6)',
          pointRadius: 0,
          tension: 0,
          fill: true,
          zIndex: 1,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false }, ticks: { font: { size: 9 }, color: '#94a3b8' } },
        y: {
          grid: { color: '#f8fafc' },
          ticks: { font: { size: 10, family: 'monospace' }, color: '#cbd5e1', callback: (v) => v.toLocaleString() },
        },
      },
    },
  });
};

onMounted(initChart);
watch(() => [props.labels, props.thisPrices], initChart, { deep: true });
</script>
