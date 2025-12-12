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
  const numeric = data.filter((v) => typeof v === 'number');

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
        { type: 'bar', data, backgroundColor: 'rgba(229,57,53,0.25)' },
        { type: 'line', data, borderColor: '#e53935', tension: 0.4 },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          min: Math.max(0, min - pad),
          max: max + pad,
          ticks: { callback: (v) => Number(v).toLocaleString() },
        },
      },
    },
  });
};

watch(() => props.rows, render, { deep: true });
onMounted(render);
onBeforeUnmount(destroy);
</script>
