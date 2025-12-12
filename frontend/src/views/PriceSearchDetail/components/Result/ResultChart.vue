<template>
  <div class="chart-wrapper">
    <canvas ref="canvas"></canvas>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from 'vue';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

const props = defineProps({
  rows: { type: Array, default: () => [] },
});

const canvas = ref(null);
let chart = null;

const destroy = () => {
  if (chart) {
    chart.destroy();
    chart = null;
  }
};

const render = async () => {
  await nextTick();
  if (!canvas.value || !props.rows.length) return destroy();

  const labels = props.rows.map((r) => r.dateLabel);
  const data = props.rows.map((r) => (r.priceLabel != null ? Number(r.priceLabel) : null));
  const numeric = data.filter((v) => typeof v === 'number' && !Number.isNaN(v));

  if (!numeric.length) return destroy();

  let min = Math.min(...numeric);
  let max = Math.max(...numeric);
  const pad = (max - min) * 0.15 || 10;

  destroy();
  chart = new Chart(canvas.value.getContext('2d'), {
    type: 'bar',
    data: {
      labels,
      datasets: [
        {
          type: 'bar',
          label: '가격(원)',
          data,
          backgroundColor: 'rgba(229, 57, 53, 0.28)',
          borderColor: '#e53935',
          borderWidth: 1,
          borderRadius: 4,
        },
        {
          type: 'line',
          label: '가격 추이',
          data,
          borderColor: '#e53935',
          backgroundColor: 'transparent',
          tension: 0, // 직선 꺾은선
          pointRadius: 3,
          pointHoverRadius: 4,
          pointBackgroundColor: '#e53935',

          pointStyle: 'line',
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,

      plugins: {
        legend: {
          display: true,
          position: 'top',
          labels: {
            usePointStyle: true,
            padding: 16,
          },
        },
        tooltip: {
          callbacks: {
            label(context) {
              const value = context.parsed.y;
              if (value == null || Number.isNaN(value)) return null;
              return `${Number(value).toLocaleString()}원`;
            },
          },
        },
      },

      scales: {
        y: {
          min: Math.max(0, min - pad),
          max: max + pad,
          ticks: {
            callback: (v) => Number(v).toLocaleString(),
          },
        },
      },
    },
  });
};

watch(() => props.rows, render, { deep: true });
onMounted(render);
onBeforeUnmount(destroy);
</script>

<style scoped>
.chart-wrapper {
  position: relative;
  width: 100%;
  height: 320px;
}

@media (max-width: 768px) {
  .chart-wrapper {
    height: 260px;
  }
}
</style>
