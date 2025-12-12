<template>
  <div class="date-range" ref="wrapperRef">
    <!-- 시작 월 -->
    <div class="month-picker-wrapper">
      <div class="date-input clickable" @click="toggleMonthPicker('start')">
        <span class="date-icon">📅</span>
        <input :value="formatMonthDisplay(startModel)" class="date-field" placeholder="시작 월" readonly />
      </div>

      <div v-if="showMonthStartPicker" class="month-picker-popup">
        <div class="month-picker-header">
          <button type="button" class="month-nav-btn" @click="monthStartYear--">‹</button>
          <span class="month-picker-year">{{ monthStartYear }}년</span>
          <button type="button" class="month-nav-btn" @click="monthStartYear++">›</button>
        </div>

        <div class="month-grid">
          <button
            v-for="m in 12"
            :key="'start-' + m"
            type="button"
            class="month-btn"
            :class="{
              selected: isMonthSelected(startModel, monthStartYear, m),
              disabled: isMonthDisabled(monthStartYear, m),
            }"
            :disabled="isMonthDisabled(monthStartYear, m)"
            @click="selectMonth('start', monthStartYear, m)"
          >
            {{ m }}월
          </button>
        </div>
      </div>
    </div>

    <span class="date-separator">~</span>

    <!-- 종료 월 -->
    <div class="month-picker-wrapper">
      <div class="date-input clickable" @click="toggleMonthPicker('end')">
        <span class="date-icon">📅</span>
        <input :value="formatMonthDisplay(endModel)" class="date-field" placeholder="종료 월" readonly />
      </div>

      <div v-if="showMonthEndPicker" class="month-picker-popup">
        <div class="month-picker-header">
          <button type="button" class="month-nav-btn" @click="monthEndYear--">‹</button>
          <span class="month-picker-year">{{ monthEndYear }}년</span>
          <button type="button" class="month-nav-btn" @click="monthEndYear++">›</button>
        </div>

        <div class="month-grid">
          <button
            v-for="m in 12"
            :key="'end-' + m"
            type="button"
            class="month-btn"
            :class="{
              selected: isMonthSelected(endModel, monthEndYear, m),
              disabled: isMonthEndDisabled(monthEndYear, m),
            }"
            :disabled="isMonthEndDisabled(monthEndYear, m)"
            @click="selectMonth('end', monthEndYear, m)"
          >
            {{ m }}월
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';

const props = defineProps({
  startDate: {
    type: [Date, String, Object, null],
    default: null,
  },
  endDate: {
    type: [Date, String, Object, null],
    default: null,
  },
  yesterday: {
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

const showMonthStartPicker = ref(false);
const showMonthEndPicker = ref(false);
const monthStartYear = ref(new Date().getFullYear());
const monthEndYear = ref(new Date().getFullYear());

const wrapperRef = ref(null);

function toggleMonthPicker(type) {
  const isStart = type === 'start';

  const targetPicker = isStart ? showMonthStartPicker : showMonthEndPicker;
  const otherPicker = isStart ? showMonthEndPicker : showMonthStartPicker;

  const model = isStart ? startModel : endModel;
  const yearRef = isStart ? monthStartYear : monthEndYear;

  targetPicker.value = !targetPicker.value;
  otherPicker.value = false;

  if (targetPicker.value && model.value) {
    yearRef.value = new Date(model.value).getFullYear();
  }
}

function selectMonth(type, year, month) {
  const date = new Date(year, month - 1, 1);

  if (type === 'start') {
    startModel.value = date;
    showMonthStartPicker.value = false;

    if (endModel.value && endModel.value < startModel.value) {
      endModel.value = null;
    }
  } else {
    endModel.value = date;
    showMonthEndPicker.value = false;
  }
}

function isMonthSelected(selectedDate, year, month) {
  if (!selectedDate) return false;
  const d = new Date(selectedDate);
  return d.getFullYear() === year && d.getMonth() === month - 1;
}

function isMonthDisabled(year, month) {
  const target = new Date(year, month - 1, 1);
  const maxDate = new Date(props.yesterday.getFullYear(), props.yesterday.getMonth(), 1);
  return target > maxDate;
}

function isMonthEndDisabled(year, month) {
  if (isMonthDisabled(year, month)) return true;
  if (!startModel.value) return false;

  const target = new Date(year, month - 1, 1);
  return target < startModel.value;
}

function formatMonthDisplay(date) {
  if (!date) return '';
  const d = new Date(date);
  return `${d.getFullYear()}년 ${d.getMonth() + 1}월`;
}

function handleClickOutside(event) {
  if (!(event.target instanceof Element)) return;
  if (!wrapperRef.value?.contains(event.target)) {
    showMonthStartPicker.value = false;
    showMonthEndPicker.value = false;
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>
