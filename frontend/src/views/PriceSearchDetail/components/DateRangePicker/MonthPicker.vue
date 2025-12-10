<template>
  <div class="date-range">
    <div class="month-picker-wrapper" ref="wrapperRef">
      <div class="date-input clickable" @click="toggleMonthStartPicker">
        <span class="date-icon">📅</span>
        <input
          :value="formatMonthDisplay(startModel)"
          class="date-field"
          placeholder="시작 월"
          readonly
        />
      </div>
      <div
        v-if="showMonthStartPicker"
        class="month-picker-popup"
      >
        <div class="month-picker-header">
          <button
            type="button"
            class="month-nav-btn"
            @click="monthStartYear--"
          >
            ‹
          </button>
          <span class="month-picker-year">{{ monthStartYear }}년</span>
          <button
            type="button"
            class="month-nav-btn"
            @click="monthStartYear++"
          >
            ›
          </button>
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
            @click="selectMonthStart(monthStartYear, m)"
          >
            {{ m }}월
          </button>
        </div>
      </div>
    </div>

    <span class="date-separator">~</span>

    <div class="month-picker-wrapper">
      <div class="date-input clickable" @click="toggleMonthEndPicker">
        <span class="date-icon">📅</span>
        <input
          :value="formatMonthDisplay(endModel)"
          class="date-field"
          placeholder="종료 월"
          readonly
        />
      </div>
      <div
        v-if="showMonthEndPicker"
        class="month-picker-popup"
      >
        <div class="month-picker-header">
          <button
            type="button"
            class="month-nav-btn"
            @click="monthEndYear--"
          >
            ‹
          </button>
          <span class="month-picker-year">{{ monthEndYear }}년</span>
          <button
            type="button"
            class="month-nav-btn"
            @click="monthEndYear++"
          >
            ›
          </button>
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
            @click="selectMonthEnd(monthEndYear, m)"
          >
            {{ m }}월
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  computed, onMounted, onUnmounted, ref,
} from 'vue';

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

function toggleMonthStartPicker() {
  showMonthStartPicker.value = !showMonthStartPicker.value;
  showMonthEndPicker.value = false;
  if (showMonthStartPicker.value && startModel.value) {
    monthStartYear.value = new Date(startModel.value).getFullYear();
  }
}

function toggleMonthEndPicker() {
  showMonthEndPicker.value = !showMonthEndPicker.value;
  showMonthStartPicker.value = false;
  if (showMonthEndPicker.value && endModel.value) {
    monthEndYear.value = new Date(endModel.value).getFullYear();
  }
}

function selectMonthStart(year, month) {
  startModel.value = new Date(year, month - 1, 1);
  showMonthStartPicker.value = false;

  // 시작 월보다 이전인 종료 월은 초기화
  if (endModel.value && endModel.value < startModel.value) {
    endModel.value = null;
  }
}

function selectMonthEnd(year, month) {
  endModel.value = new Date(year, month - 1, 1);
  showMonthEndPicker.value = false;
}

function isMonthSelected(selectedDate, year, month) {
  if (!selectedDate) return false;
  const d = new Date(selectedDate);
  return d.getFullYear() === year && d.getMonth() === month - 1;
}

// 미래 월 비활성화
function isMonthDisabled(year, month) {
  const target = new Date(year, month - 1, 1);
  const maxDateForMonth = new Date(props.yesterday.getFullYear(), props.yesterday.getMonth(), 1);
  return target > maxDateForMonth;
}

// 시작 월 이전의 종료 월 비활성화
function isMonthEndDisabled(year, month) {
  if (isMonthDisabled(year, month)) return true;
  if (startModel.value) {
    const target = new Date(year, month - 1, 1);
    return target < startModel.value;
  }
  return false;
}

function handleClickOutside(event) {
  const target = event.target;
  if (!(target instanceof Element)) return;
  if (!wrapperRef.value) return;
  if (!wrapperRef.value.contains(target)) {
    showMonthStartPicker.value = false;
    showMonthEndPicker.value = false;
  }
}

function formatMonthDisplay(date) {
  if (!date) return '';
  const d = new Date(date);
  return `${d.getFullYear()}년 ${d.getMonth() + 1}월`;
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>


