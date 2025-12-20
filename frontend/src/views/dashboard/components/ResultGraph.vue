<template>
  <div class="bg-white p-5 md:p-8 rounded-xl shadow-sm border border-gray-200 space-y-6 font-sans">
    <div>
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-4 text-[10px] font-bold uppercase text-gray-400 tracking-tight">
          <div class="flex items-center gap-1.5"><span class="w-3 h-0.5 bg-red-500"></span> 금년 시세</div>
          <div class="flex items-center gap-1.5">
            <span class="w-3 h-0.5 bg-gray-500 border-dashed border-t"></span> 전년 시세
          </div>
        </div>
        <span class="text-[9px] font-black text-gray-300 uppercase tracking-widest italic">Price Trend</span>
      </div>
      <div class="h-64 md:h-80"><canvas ref="lineCanvas"></canvas></div>
    </div>

    <div class="pt-6 border-t border-gray-50">
      <div class="flex items-center justify-between mb-4">
        <p class="text-[10px] font-bold text-gray-400 uppercase tracking-tight">
          조회 기간 내 평균 대비 현황 (vs Average)
        </p>
        <div class="flex gap-3 text-[9px] font-bold">
          <span class="text-red-500">▲ 상승</span>
          <span class="text-slate-400">▼ 하락</span>
        </div>
      </div>
      <div class="h-24 md:h-32"><canvas ref="barCanvas"></canvas></div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue';
import Chart from 'chart.js/auto';

const props = defineProps({
  chartData: {
    type: Object,
    required: true,
    default: () => ({ labels: [], thisPrices: [], lastPrices: [] }),
  },
});

const lineCanvas = ref(null);
const barCanvas = ref(null);
let lineChart = null;
let barChart = null;

const initCharts = () => {
  if (!lineCanvas.value || !barCanvas.value) return;
  if (lineChart) lineChart.destroy();
  if (barChart) barChart.destroy();

  const data = props.chartData;

  const validPrices = data.thisPrices.filter((p) => p !== null && p !== undefined);
  const avgPrice = validPrices.length ? validPrices.reduce((a, b) => a + b, 0) / validPrices.length : 0;

  const diffFromAvg = data.thisPrices.map((val) => (val !== null ? val - avgPrice : 0));

  const maxVal = Math.max(...validPrices);
  const minVal = Math.min(...validPrices);
  const maxIdx = data.thisPrices.indexOf(maxVal);
  const minIdx = data.thisPrices.indexOf(minVal);

  lineChart = new Chart(lineCanvas.value.getContext('2d'), {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [
        {
          label: '금년',
          data: data.thisPrices,
          borderColor: '#ef4444',
          borderWidth: 2.5,
          pointRadius: data.thisPrices.map((_, i) => (i === maxIdx || i === minIdx ? 5 : 0)),
          pointBackgroundColor: data.thisPrices.map((_, i) => (i === maxIdx ? '#ef4444' : '#334155')),
          pointBorderColor: '#fff',
          pointBorderWidth: 2,
          tension: 0,
          fill: false,
        },
        {
          label: '전년',
          data: data.lastPrices,
          borderColor: '#adb6bd',
          borderWidth: 1.5,
          pointRadius: 0,
          borderDash: [5, 5],
          tension: 0,
          fill: false,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        x: { grid: { display: false }, ticks: { font: { size: 10, weight: '600' }, color: '#94a3b8' } },
        y: {
          grid: { color: '#f8fafc' },
          ticks: {
            font: { size: 10 },
            color: '#cbd5e1',
            callbacks: {
              title: (items) => items[0].label,
              label: (context) => {
                const label = context.dataset.label || '';
                const value = context.parsed.y.toLocaleString();
                return ` ${label}: ${value}원`;
              },
            },
          },
        },
      },
      hover: {
        mode: 'index',
        intersect: false,
      },
      elements: {
        point: {
          hoverRadius: 4,
        },
      },
    },
  });

  barChart = new Chart(barCanvas.value.getContext('2d'), {
    type: 'bar',
    data: {
      labels: data.labels,
      datasets: [
        {
          data: diffFromAvg,
          backgroundColor: diffFromAvg.map((val) => (val >= 0 ? 'rgba(239, 68, 68, 0.8)' : 'rgba(203, 213, 225, 0.8)')),
          borderRadius: 4,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            title: () => '',
            label: (context) => {
              const val = context.raw;
              const sign = val >= 0 ? '+' : '';
              return ` 평균 대비: ${sign}${Math.floor(val).toLocaleString()}원`;
            },
          },
        },
      },
      scales: {
        x: { display: false },
        y: {
          grid: {
            color: (context) => (context.tick.value === 0 ? '#e2e8f0' : 'transparent'),
            lineWidth: 2,
          },
          ticks: { display: false },
        },
      },
    },
  });
};

onMounted(initCharts);
watch(() => props.chartData, initCharts, { deep: true });
</script>
