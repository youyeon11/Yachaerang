<template>
  <div class="bg-white p-6 md:p-8 rounded-2xl shadow-sm border border-gray-100 space-y-12 font-sans">
    <div class="space-y-5">
      <div class="flex flex-col md:flex-row md:items-end justify-between gap-3">
        <div>
          <h3 class="text-base md:text-lg font-black text-slate-800 flex items-center gap-2">
            <span class="w-1.5 h-4 bg-slate-900 rounded-full"></span>
            시세 변동 추이
          </h3>
          <p class="text-[13px] text-slate-500 mt-1 font-medium">전년 동기 대비 금년 실거래가의 흐름을 비교합니다.</p>
        </div>

        <div class="flex items-center gap-5 text-xs font-bold bg-slate-50 px-3 py-2 rounded-xl border border-slate-100">
          <div class="flex items-center gap-2 text-slate-600"><span class="w-4 h-0.5 bg-slate-500"></span> 금년</div>
          <div class="flex items-center gap-2 text-slate-400">
            <span class="w-4 h-2 bg-slate-200 rounded-sm"></span> 전년
          </div>
          <div class="flex items-center gap-3 ml-2 border-l border-slate-200 pl-3">
            <span class="text-rose-500">● 최고</span>
            <span class="text-blue-500">● 최저</span>
          </div>
        </div>
      </div>
      <div class="h-72 md:h-96"><canvas ref="lineCanvas"></canvas></div>
    </div>

    <div class="pt-10 border-t border-slate-50 space-y-5">
      <div class="flex flex-col md:flex-row md:items-end justify-between gap-3">
        <div>
          <h3 class="text-base md:text-lg font-black text-slate-800 flex items-center gap-2">
            <span class="w-1.5 h-4 bg-rose-400 rounded-full"></span>
            평균 대비 등락 현황
          </h3>
          <p class="text-[13px] text-slate-500 mt-1 font-medium">
            조회 기간 전체 평균(<span class="font-mono font-bold text-slate-700 bg-slate-100 px-1 rounded"
              >{{ Math.floor(avgPrice).toLocaleString() }}원</span
            >) 대비 현황입니다.
          </p>
        </div>
        <div class="flex gap-4 text-xs font-black italic tracking-tight">
          <span class="text-rose-400">상승 (+)</span>
          <span class="text-blue-400">하락 (-)</span>
        </div>
      </div>
      <div class="h-40 md:h-52"><canvas ref="barCanvas"></canvas></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
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

const avgPrice = computed(() => {
  const validPrices = props.chartData.thisPrices.filter((p) => p !== null);
  return validPrices.length ? validPrices.reduce((a, b) => a + b, 0) / validPrices.length : 0;
});

const initCharts = () => {
  if (!lineCanvas.value || !barCanvas.value) return;
  if (lineChart) lineChart.destroy();
  if (barChart) barChart.destroy();

  const data = props.chartData;
  const currentAvg = avgPrice.value;

  const validThisPrices = data.thisPrices.filter((p) => p !== null);
  const maxVal = Math.max(...validThisPrices);
  const minVal = Math.min(...validThisPrices);
  const maxIdx = data.thisPrices.indexOf(maxVal);
  const minIdx = data.thisPrices.indexOf(minVal);

  const diffData = data.thisPrices.map((val) => {
    if (val === null) return { diff: 0, pct: 0, val: 0 };
    return { diff: val - currentAvg, pct: ((val - currentAvg) / currentAvg) * 100, val };
  });

  lineChart = new Chart(lineCanvas.value.getContext('2d'), {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [
        {
          label: '금년',
          data: data.thisPrices,
          borderColor: '#475569',
          borderWidth: 2,
          pointRadius: data.thisPrices.map((_, i) => (i === maxIdx || i === minIdx ? 4 : 0)),
          pointBackgroundColor: data.thisPrices.map((_, i) =>
            i === maxIdx ? '#f43f5e' : i === minIdx ? '#3b82f6' : '#475569'
          ),
          pointBorderColor: '#fff',
          tension: 0,
          fill: false,
          zIndex: 10,
        },
        {
          label: '전년',
          data: data.lastPrices,
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

  barChart = new Chart(barCanvas.value.getContext('2d'), {
    type: 'bar',
    data: {
      labels: data.labels,
      datasets: [
        {
          data: diffData.map((d) => d.diff),
          backgroundColor: diffData.map((d) => (d.diff >= 0 ? '#fda4af' : '#93c5fd')),
          hoverBackgroundColor: diffData.map((d) => (d.diff >= 0 ? '#f43f5e' : '#3b82f6')),
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
              const item = diffData[ctx.dataIndex];
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
          suggestedMin: Math.min(...diffData.map((d) => d.diff)) * 1.5,
          suggestedMax: Math.max(...diffData.map((d) => d.diff)) * 1.5,
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

onMounted(initCharts);
watch(() => props.chartData, initCharts, { deep: true });
</script>
