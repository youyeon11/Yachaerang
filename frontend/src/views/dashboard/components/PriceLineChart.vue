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

// X축 라벨 포맷팅 함수
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
    // 일간: 2025-12-24 -> 12/24
    if (label.includes('-')) {
      const parts = label.split('-');
      if (parts.length >= 3) {
        return `${parts[1]}/${parts[2]}`;
      }
    }
    return label;
  } else if (periodType === 'week') {
    // 주간: 2025-12-01 ~ 2025-12-07 -> 12/01
    if (label.includes('~')) {
      const startDate = label.split('~')[0].trim();
      const parts = startDate.split('-');
      if (parts.length >= 3) {
        return `${parts[1]}/${parts[2]}`;
      }
    }
    return label;
  } else if (periodType === 'month') {
    // 월간: 2025-12 -> 12월
    if (label.includes('-')) {
      const month = label.split('-')[1];
      return `${parseInt(month)}월`;
    }
    return label;
  } else if (periodType === 'year') {
    // 연간: 2025 -> 2025
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
      // 금년 데이터
      return `금년: ${value.toLocaleString()}원`;
    } else if (datasetIndex === 1) {
      // 전년 데이터
      return `전년: ${value.toLocaleString()}원`;
    }
  } else {
    // week/month/year 모드
    if (datasetIndex === 0) {
      // 최대값
      return `최대: ${value.toLocaleString()}원`;
    } else if (datasetIndex === 1) {
      // 최소값
      return `최소: ${value.toLocaleString()}원`;
    } else if (datasetIndex === 2) {
      // 평균
      return `평균: ${value.toLocaleString()}원`;
    } else if (datasetIndex === 3) {
      // 전년
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
    // day 모드: 기존 방식 유지
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
    
    // 최소값 라인 (같은 색 계열, 먼저 그림)
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

    // 최대값 라인 (같은 색 계열, fill로 최소값부터 최대값까지 영역 채움)
    datasets.push({
      label: '최대',
      data: maxPrices,
      borderColor: 'rgba(148, 163, 184, 0.4)',
      backgroundColor: 'rgba(148, 163, 184, 0.12)',
      borderWidth: 1,
      pointRadius: 0,
      tension: 0,
      fill: '-1', // 이전 데이터셋(최소값)부터 채움
      spanGaps: true,
      zIndex: 1,
    });

    // 평균 라인 (가장 눈에 띄게)
    datasets.push({
      label: '평균',
      data: props.thisPrices,
      borderColor: '#475569',
      borderWidth: 3,
      pointRadius: 4,
      pointBackgroundColor: '#475569',
      pointBorderColor: '#fff',
      pointBorderWidth: 2,
      pointHoverRadius: 6,
      tension: 0,
      fill: false,
      spanGaps: true,
      zIndex: 10,
    });

    // 전년 데이터 (있는 경우)
    if (props.lastPrices && props.lastPrices.some(p => p !== null && p !== undefined)) {
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

  // 최대/최소값 라벨을 그리는 커스텀 플러그인
  const maxMinLabelPlugin = {
    id: 'maxMinLabel',
    afterDraw: (chart) => {
      const ctx = chart.ctx;
      const periodType = props.periodType;
      
      if (periodType === 'day') {
        // day 모드: 최대/최소값 라벨 표시
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
            
            // 배경 박스
            const textWidth = ctx.measureText(label).width;
            ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
            ctx.fillRect(x - textWidth / 2 - 6, y - 14, textWidth + 12, 18);
            
            // 텍스트
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
            
            // 배경 박스
            const textWidth = ctx.measureText(label).width;
            ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
            ctx.fillRect(x - textWidth / 2 - 6, y - 4, textWidth + 12, 18);
            
            // 텍스트
            ctx.fillStyle = '#3b82f6';
            ctx.fillText(label, x, y);
            ctx.restore();
          }
        }
      } else {
        // week/month/year 모드: 최대/최소값 범위 표시 (선택적으로 특정 포인트에 표시)
        // 여기서는 평균 라인에 최대/최소 범위 정보를 표시하지 않고, 
        // 툴팁에서만 표시하도록 함 (너무 복잡해지지 않도록)
      }
    }
  };

  lineChart = new Chart(lineCanvas.value.getContext('2d'), {
    type: 'line',
    data: {
      labels: props.labels,
      datasets,
    },
    plugins: [maxMinLabelPlugin],
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: { mode: 'index', intersect: false },
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
              // week/month/year 모드에서 범위 정보 추가 표시
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
            callback: function(value, index) {
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

onMounted(initChart);
watch(() => [props.labels, props.thisPrices, props.lastPrices, props.minPrices, props.maxPrices, props.periodType], initChart, { deep: true });
</script>
