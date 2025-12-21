<template>
  <div class="h-40 md:h-52">
    <canvas ref="barCanvas"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import Chart from 'chart.js/auto';

const props = defineProps({
  labels: Array,
  diffData: Array,
});

const barCanvas = ref(null);
let barChart = null;

const initChart = () => {
  if (!barCanvas.value) return;
  if (barChart) barChart.destroy();

  barChart = new Chart(barCanvas.value.getContext('2d'), {
    type: 'bar',
    data: {
      labels: props.labels,
      datasets: [
        {
          data: props.diffData.map((d) => d.diff),
          backgroundColor: props.diffData.map((d) => (d.diff >= 0 ? '#fda4af' : '#93c5fd')),
          hoverBackgroundColor: props.diffData.map((d) => (d.diff >= 0 ? '#f43f5e' : '#3b82f6')),
          borderRadius: 2,
          barPercentage: 0.8,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: 'rgba(30, 41, 59, 0.9)',
          titleColor: '#fff',
          bodyColor: '#fff',
          bodyFont: { size: 11, weight: '500' },
          padding: 12,
          displayColors: false,
          callbacks: {
            label: (ctx) => {
              const item = props.diffData[ctx.dataIndex];
              const prefix = item.diff >= 0 ? '▲' : '▼';
              const sign = item.diff >= 0 ? '+' : '';
              return [
                ` 실거래가: ${item.val.toLocaleString()}원`,
                ` 평균대비: ${prefix} ${Math.abs(Math.floor(item.diff)).toLocaleString()}원 (${sign}${item.pct.toFixed(
                  1
                )}%)`,
              ];
            },
          },
        },
      },
      scales: {
        x: { display: false },
        y: {
          suggestedMin: Math.min(...props.diffData.map((d) => d.diff)) * 1.5,
          suggestedMax: Math.max(...props.diffData.map((d) => d.diff)) * 1.5,
          grid: {
            color: (ctx) => (ctx.tick.value === 0 ? '#94a3b8' : 'transparent'),
            lineWidth: 2,
          },
          ticks: {
            display: true,
            values: [0],
            callback: (val) => (val === 0 ? 'AVG' : ''),
            font: { size: 9, weight: 'bold' },
            color: '#94a3b8',
          },
        },
      },
    },
  });
};

onMounted(initChart);
watch(() => props.diffData, initChart, { deep: true });
</script>
