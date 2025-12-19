<template>
  <div class="date-range">
    <VDatePicker
      :attributes="weekStartAttributes"
      :max-date="lastWeekSunday"
      locale="ko"
      color="red"
      :popover="{ visibility: 'click' }"
      @dayclick="onWeekStartClick"
    >
      <template #default="{ togglePopover }">
        <div class="date-input clickable" :class="{ 'week-selected': !!startModel }" @click="togglePopover">
          <span class="date-icon">📅</span>
          <input :value="formatWeekDisplay(startModel)" class="date-field" placeholder="시작 주" readonly />
        </div>
      </template>
    </VDatePicker>

    <span class="date-separator">~</span>

    <VDatePicker
      :attributes="weekEndAttributes"
      :max-date="lastWeekSunday"
      :min-date="startModel || null"
      locale="ko"
      color="red"
      :popover="{ visibility: 'click' }"
      @dayclick="onWeekEndClick"
    >
      <template #default="{ togglePopover }">
        <div class="date-input clickable" :class="{ 'week-selected': !!endModel }" @click="togglePopover">
          <span class="date-icon">📅</span>
          <input :value="formatWeekDisplay(endModel)" class="date-field" placeholder="종료 주" readonly />
        </div>
      </template>
    </VDatePicker>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  startDate: {
    type: [Date, String, Object, null],
    default: null,
  },
  endDate: {
    type: [Date, String, Object, null],
    default: null,
  },
  lastWeekSunday: {
    type: Date,
    required: true,
  },
});

const emit = defineEmits(['update:startDate', 'update:endDate']);

const startModel = computed({
  get: () => props.startDate,
  set: (val) => emit('update:startDate', val),
});

const endModel = computed({
  get: () => props.endDate,
  set: (val) => emit('update:endDate', val),
});

function getISOWeekYearAndNumber(date) {
  const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  const dayNum = d.getUTCDay() || 7;
  d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  const year = d.getUTCFullYear();
  const yearStartDate = new Date(Date.UTC(year, 0, 1));
  const week = Math.ceil(((d - yearStartDate) / 86400000 + 1) / 7);
  return { year, week };
}

function getMondayOfWeek(date) {
  const d = new Date(date);
  const dayOfWeek = d.getDay();
  const diff = d.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
  const monday = new Date(d);
  monday.setDate(diff);
  monday.setHours(0, 0, 0, 0);
  return monday;
}

function getWeekRange(date) {
  if (!date) return null;
  const monday = getMondayOfWeek(date);
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() + 6);
  sunday.setHours(23, 59, 59, 999);
  return { start: monday, end: sunday };
}

const weekStartAttributes = computed(() => {
  if (!startModel.value) return [];
  const range = getWeekRange(startModel.value);
  if (!range) return [];
  return [
    {
      key: 'week-highlight-start',
      highlight: {
        start: { fillMode: 'solid', color: 'red' },
        base: { fillMode: 'light', color: 'red' },
        end: { fillMode: 'solid', color: 'red' },
      },
      dates: range,
    },
  ];
});

const weekEndAttributes = computed(() => {
  if (!endModel.value) return [];
  const range = getWeekRange(endModel.value);
  if (!range) return [];
  return [
    {
      key: 'week-highlight-end',
      highlight: {
        start: { fillMode: 'solid', color: 'red' },
        base: { fillMode: 'light', color: 'red' },
        end: { fillMode: 'solid', color: 'red' },
      },
      dates: range,
    },
  ];
});

function formatWeekDisplay(date) {
  if (!date) return '';
  const { year, week } = getISOWeekYearAndNumber(new Date(date));
  return `${year}년 ${week}주차`;
}

function onWeekStartClick(day) {
  const monday = getMondayOfWeek(day.date);
  startModel.value = monday;

  if (endModel.value && monday > getMondayOfWeek(endModel.value)) {
    endModel.value = null;
  }
}

function onWeekEndClick(day) {
  const monday = getMondayOfWeek(day.date);

  if (startModel.value && monday < getMondayOfWeek(startModel.value)) {
    alert('종료 주는 시작 주 이후여야 합니다.');
    return;
  }

  endModel.value = monday;
}
</script>


