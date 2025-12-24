<template>
  <div class="h-72 md:h-96">
    <canvas ref="lineCanvas"></canvas>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import Chart from 'chart.js/auto';

const props = defineProps({
  labels: Array,
  thisPrices: Array,
  lastPrices: Array,
  maxIdx: Number,
  minIdx: Number,
  minPrices: Array,
  maxPrices: Array,
  periodType: {
    type: String,
    default: 'day',
  },
});

const lineCanvas = ref(null);
let lineChart = null;

const formatXAxisLabel = (label, index, labels) => {
  if (!label) return '';

  const periodType = props.periodType;
  const totalLabels = labels.length;

  // 데이터가 많을 때 간격 조절
  if (totalLabels > 20) {
    const step = Math.ceil(totalLabels / 10);
    if (index % step !== 0 && index !== totalLabels - 1) {
      return '';
    }
  } else if (totalLabels > 12) {
    const step = Math.ceil(totalLabels / 8);
    if (index % step !== 0 && index !== totalLabels - 1) {
      return '';
    }
  }

  if (periodType === 'day') {
    if (label.includes('-')) {
      const parts = label.split('-');
      if (parts.length >= 3) {
        return `${parts[1]}/${parts[2]}`;
      }
    }
    return label;
  } else if (periodType === 'week') {
    if (label.includes('~')) {
      const startDate = label.split('~')[0].trim();
      const parts = startDate.split('-');
      if (parts.length >= 3) {
        return `${parts[1]}/${parts[2]}`;
      }
    }
    return label;
  } else if (periodType === 'month') {
    if (label.includes('-')) {
      const month = label.split('-')[1];
      return `${parseInt(month)}월`;
    }
    return label;
  } else if (periodType === 'year') {
    return label;
  }

  return label;
};

// 툴팁 포맷팅 함수
const formatTooltipLabel = (context) => {
  const periodType = props.periodType;
  const dataIndex = context.dataIndex;
  const datasetIndex = context.datasetIndex;
  const label = props.labels[dataIndex];
  const value = context.parsed.y;

  if (value === null || value === undefined) {
    return '값 없음';
  }

  if (periodType === 'day') {
    if (datasetIndex === 0) {
      return `금년: ${value.toLocaleString()}원`;
    } else if (datasetIndex === 1) {
      return `전년: ${value.toLocaleString()}원`;
    }
  } else {
    // week/month/year 모드
    if (datasetIndex === 0) {
      return `최소: ${value.toLocaleString()}원`;
    } else if (datasetIndex === 1) {
      return `최대: ${value.toLocaleString()}원`;
    } else if (datasetIndex === 2) {
      return `평균: ${value.toLocaleString()}원`;
    } else if (datasetIndex === 3) {
      return `전년: ${value.toLocaleString()}원`;
    }
  }

  return `${value.toLocaleString()}원`;
};

const initChart = () => {
  if (!lineCanvas.value) return;
  if (lineChart) lineChart.destroy();

  const isDayMode = props.periodType === 'day';
  const datasets = [];

  if (isDayMode) {
    // day
    datasets.push(
      {
        label: '금년',
        data: props.thisPrices,
        borderColor: '#475569',
        borderWidth: 3,
        pointRadius: props.thisPrices.map((_, i) => (i === props.maxIdx || i === props.minIdx ? 7 : 0)),
        pointBackgroundColor: props.thisPrices.map((_, i) =>
          i === props.maxIdx ? '#f43f5e' : i === props.minIdx ? '#3b82f6' : '#475569'
        ),
        pointBorderColor: '#fff',
        pointBorderWidth: 3,
        pointHoverRadius: props.thisPrices.map((_, i) => (i === props.maxIdx || i === props.minIdx ? 9 : 6)),
        tension: 0,
        fill: false,
        spanGaps: true,
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
        spanGaps: true,
        zIndex: 1,
      }
    );
  } else {
    // week/month/year 모드: min~max 밴드 + avg 라인
    const minPrices = props.minPrices || [];
    const maxPrices = props.maxPrices || [];

    datasets.push({
      label: '최소',
      data: minPrices,
      borderColor: 'rgba(148, 163, 184, 0.4)',
      backgroundColor: 'rgba(148, 163, 184, 0.12)',
      borderWidth: 1,
      pointRadius: 0,
      tension: 0,
      fill: false,
      spanGaps: true,
      zIndex: 1,
    });

    datasets.push({
      label: '최대',
      data: maxPrices,
      borderColor: 'rgba(148, 163, 184, 0.4)',
      backgroundColor: 'rgba(148, 163, 184, 0.12)',
      borderWidth: 1,
      pointRadius: 0,
      tension: 0,
      fill: '-1',
      spanGaps: true,
      zIndex: 1,
    });

    const validAvgPrices = props.thisPrices.filter((p) => p !== null && p !== undefined && typeof p === 'number');
    const maxAvgVal = validAvgPrices.length > 0 ? Math.max(...validAvgPrices) : null;
    const minAvgVal = validAvgPrices.length > 0 ? Math.min(...validAvgPrices) : null;
    const maxAvgIdx = maxAvgVal !== null ? props.thisPrices.indexOf(maxAvgVal) : -1;
    const minAvgIdx = minAvgVal !== null ? props.thisPrices.indexOf(minAvgVal) : -1;

    datasets.push({
      label: '평균',
      data: props.thisPrices,
      borderColor: '#475569',
      borderWidth: 3,
      pointRadius: props.thisPrices.map((_, i) => (i === maxAvgIdx || i === minAvgIdx ? 7 : 4)),
      pointBackgroundColor: props.thisPrices.map((_, i) => {
        if (i === maxAvgIdx) return '#f43f5e';
        if (i === minAvgIdx) return '#3b82f6';
        return '#475569';
      }),
      pointBorderColor: '#fff',
      pointBorderWidth: props.thisPrices.map((_, i) => (i === maxAvgIdx || i === minAvgIdx ? 3 : 2)),
      pointHoverRadius: props.thisPrices.map((_, i) => (i === maxAvgIdx || i === minAvgIdx ? 9 : 6)),
      tension: 0,
      fill: false,
      spanGaps: true,
      zIndex: 10,
    });

    // 전년 데이터 (있는 경우)
    if (props.lastPrices && props.lastPrices.some((p) => p !== null && p !== undefined)) {
      datasets.push({
        label: '전년',
        data: props.lastPrices,
        borderColor: 'rgba(226, 232, 240, 0.8)',
        borderWidth: 2,
        borderDash: [5, 5],
        pointRadius: 0,
        tension: 0,
        fill: false,
        spanGaps: true,
        zIndex: 5,
      });
    }
  }

  const maxMinLabelPlugin = {
    id: 'maxMinLabel',
    z: 1,
    beforeTooltipDraw: (chart) => {
      const ctx = chart.ctx;
      const periodType = props.periodType;

      if (periodType === 'day') {
        // day 모드
        const dataset = chart.data.datasets[0];
        const meta = chart.getDatasetMeta(0);

        if (props.maxIdx >= 0 && props.maxIdx < meta.data.length) {
          const maxPoint = meta.data[props.maxIdx];
          const maxValue = props.thisPrices[props.maxIdx];

          if (maxPoint && maxValue !== null && maxValue !== undefined) {
            ctx.save();
            ctx.fillStyle = '#f43f5e';
            ctx.font = 'bold 11px sans-serif';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'bottom';

            const label = `최고: ${maxValue.toLocaleString()}원`;
            const x = maxPoint.x;
            const y = maxPoint.y - 12;

            const textWidth = ctx.measureText(label).width;
            ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
            ctx.fillRect(x - textWidth / 2 - 6, y - 14, textWidth + 12, 18);

            ctx.fillStyle = '#f43f5e';
            ctx.fillText(label, x, y);
            ctx.restore();
          }
        }

        if (props.minIdx >= 0 && props.minIdx < meta.data.length) {
          const minPoint = meta.data[props.minIdx];
          const minValue = props.thisPrices[props.minIdx];

          if (minPoint && minValue !== null && minValue !== undefined) {
            ctx.save();
            ctx.fillStyle = '#3b82f6';
            ctx.font = 'bold 11px sans-serif';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'top';

            const label = `최저: ${minValue.toLocaleString()}원`;
            const x = minPoint.x;
            const y = minPoint.y + 12;

            const textWidth = ctx.measureText(label).width;
            ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
            ctx.fillRect(x - textWidth / 2 - 6, y - 4, textWidth + 12, 18);

            ctx.fillStyle = '#3b82f6';
            ctx.fillText(label, x, y);
            ctx.restore();
          }
        }
      } else {
        const avgDatasetIndex = 2;
        const avgMeta = chart.getDatasetMeta(avgDatasetIndex);

        if (avgMeta && avgMeta.data && avgMeta.data.length > 0) {
          const validPrices = props.thisPrices.filter((p) => p !== null && p !== undefined && typeof p === 'number');

          if (validPrices.length > 0) {
            const maxVal = Math.max(...validPrices);
            const minVal = Math.min(...validPrices);
            const maxIdx = props.thisPrices.indexOf(maxVal);
            const minIdx = props.thisPrices.indexOf(minVal);

            if (maxIdx >= 0 && maxIdx < avgMeta.data.length) {
              const maxPoint = avgMeta.data[maxIdx];
              if (maxPoint && maxVal !== null && maxVal !== undefined) {
                ctx.save();
                ctx.fillStyle = '#f43f5e';
                ctx.font = 'bold 11px sans-serif';
                ctx.textAlign = 'center';
                ctx.textBaseline = 'bottom';

                const label = `최고: ${maxVal.toLocaleString()}원`;
                const x = maxPoint.x;
                const y = maxPoint.y - 12;

                const textWidth = ctx.measureText(label).width;
                ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
                ctx.fillRect(x - textWidth / 2 - 6, y - 14, textWidth + 12, 18);

                ctx.fillStyle = '#f43f5e';
                ctx.fillText(label, x, y);
                ctx.restore();
              }
            }

            if (minIdx >= 0 && minIdx < avgMeta.data.length) {
              const minPoint = avgMeta.data[minIdx];
              if (minPoint && minVal !== null && minVal !== undefined) {
                ctx.save();
                ctx.fillStyle = '#3b82f6';
                ctx.font = 'bold 11px sans-serif';
                ctx.textAlign = 'center';
                ctx.textBaseline = 'top';

                const label = `최저: ${minVal.toLocaleString()}원`;
                const x = minPoint.x;
                const y = minPoint.y + 12;

                const textWidth = ctx.measureText(label).width;
                ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
                ctx.fillRect(x - textWidth / 2 - 6, y - 4, textWidth + 12, 18);

                ctx.fillStyle = '#3b82f6';
                ctx.fillText(label, x, y);
                ctx.restore();
              }
            }
          }
        }
      }
    },
  };

  const crosshairPlugin = {
    id: 'crosshair',
    z: 10,

    afterDraw: (chart) => {
      if (chart.crosshairX === null || chart.crosshairX === undefined) return;

      const ctx = chart.ctx;
      const chartArea = chart.chartArea;

      ctx.save();
      ctx.strokeStyle = 'rgba(148, 163, 184, 0.5)';
      ctx.lineWidth = 1;
      ctx.setLineDash([5, 5]);
      ctx.beginPath();
      ctx.moveTo(chart.crosshairX, chartArea.top);
      ctx.lineTo(chart.crosshairX, chartArea.bottom);
      ctx.stroke();
      ctx.restore();
    },
  };

  lineChart = new Chart(lineCanvas.value.getContext('2d'), {
    type: 'line',
    data: {
      labels: props.labels,
      datasets,
    },
    plugins: [maxMinLabelPlugin, crosshairPlugin],
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        mode: 'index',
        intersect: false,
        axis: 'x',
      },
      onHover: (event, activeElements) => {
        const chart = event.chart;
        if (activeElements.length > 0) {
          chart.canvas.style.cursor = 'crosshair';
        } else {
          chart.canvas.style.cursor = 'default';
        }
      },
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: 'rgba(30, 41, 59, 0.95)',
          titleColor: '#fff',
          bodyColor: '#fff',
          titleFont: { size: 13, weight: 'bold' },
          bodyFont: { size: 12, weight: '500' },
          padding: 14,
          displayColors: false,
          cornerRadius: 8,
          titleSpacing: 6,
          bodySpacing: 4,
          z: 100,
          callbacks: {
            title: (context) => {
              const label = props.labels[context[0].dataIndex];
              const periodType = props.periodType;
              if (periodType === 'day') {
                return label ? `날짜: ${label}` : '';
              } else {
                return label ? `기간: ${label}` : '';
              }
            },
            label: (context) => {
              return formatTooltipLabel(context);
            },
            afterBody: (context) => {
              if (props.periodType !== 'day' && context.length > 0) {
                const dataIndex = context[0].dataIndex;
                const minPrice = props.minPrices?.[dataIndex];
                const maxPrice = props.maxPrices?.[dataIndex];

                if (minPrice !== null && minPrice !== undefined && maxPrice !== null && maxPrice !== undefined) {
                  return [`범위: ${minPrice.toLocaleString()} ~ ${maxPrice.toLocaleString()}원`];
                }
              }
              return [];
            },
          },
        },
      },
      scales: {
        x: {
          border: {
            display: true,
            color: '#cbd5e1',
            width: 2,
          },
          grid: {
            display: true,
            color: '#e2e8f0',
            lineWidth: 1,
            drawBorder: true,
          },
          ticks: {
            font: { size: 12, weight: '600' },
            color: '#64748b',
            padding: 12,
            maxRotation: 45,
            minRotation: 0,
            callback: function (value, index) {
              return formatXAxisLabel(this.getLabelForValue(value), index, props.labels);
            },
          },
        },
        y: {
          border: {
            display: true,
            color: '#cbd5e1',
            width: 2,
          },
          grid: {
            color: '#e2e8f0',
            lineWidth: 1,
            drawBorder: true,
          },
          ticks: {
            font: { size: 12, family: 'monospace', weight: '600' },
            color: '#64748b',
            padding: 10,
            callback: (v) => {
              if (v === null || v === undefined) return '';
              return v.toLocaleString();
            },
          },
        },
      },
    },
  });
};

onMounted(() => {
  initChart();
  if (lineCanvas.value) {
    lineCanvas.value.addEventListener('mousemove', (e) => {
      if (lineChart) {
        const rect = lineCanvas.value.getBoundingClientRect();
        const x = e.clientX - rect.left;
        lineChart.crosshairX = x;
        lineChart.update('none');
      }
    });
    lineCanvas.value.addEventListener('mouseleave', () => {
      if (lineChart) {
        lineChart.crosshairX = null;
        lineChart.update('none');
      }
    });
  }
});

watch(
  () => [props.labels, props.thisPrices, props.lastPrices, props.minPrices, props.maxPrices, props.periodType],
  initChart,
  { deep: true }
);
</script>
