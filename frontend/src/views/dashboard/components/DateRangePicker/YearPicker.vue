<template>
  <div class="date-range year-picker">
    <template v-if="!isYearDetailModel">
      <div class="year-range">
        <div class="date-input">
          <span class="date-icon">📅</span>
          <select v-model="yearStartModel" class="date-field">
            <option value="">시작 연도</option>
            <option
              v-for="y in yearOptions"
              :key="'ys-' + y"
              :value="y"
            >
              {{ y }}
            </option>
          </select>
        </div>
        <span class="date-separator">~</span>
        <div class="date-input">
          <span class="date-icon">📅</span>
          <select v-model="yearEndModel" class="date-field">
            <option value="">종료 연도</option>
            <option
              v-for="y in yearOptions"
              :key="'ye-' + y"
              :value="y"
            >
              {{ y }}
            </option>
          </select>
        </div>
      </div>
    </template>
    <template v-else>
      <div class="year-range">
        <div class="date-input">
          <span class="date-icon">📅</span>
          <select v-model="yearDetailModel" class="date-field">
            <option value="">연도 선택</option>
            <option
              v-for="y in yearOptions"
              :key="'yd-' + y"
              :value="y"
            >
              {{ y }}
            </option>
          </select>
        </div>
      </div>
    </template>

    <div class="year-detail-toggle" v-if="showDetailToggle">
      <label>
        <input type="checkbox" v-model="isYearDetailModel" />
        특정 연도만 조회
      </label>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  yearOptions: {
    type: Array,
    default: () => [],
  },
  isYearDetail: {
    type: Boolean,
    default: false,
  },
  yearStart: {
    type: String,
    default: '',
  },
  yearEnd: {
    type: String,
    default: '',
  },
  yearDetail: {
    type: String,
    default: '',
  },
  showDetailToggle: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits([
  'update:isYearDetail',
  'update:yearStart',
  'update:yearEnd',
  'update:yearDetail',
]);

const isYearDetailModel = computed({
  get: () => props.isYearDetail,
  set: (val) => emit('update:isYearDetail', val),
});

const yearStartModel = computed({
  get: () => props.yearStart,
  set: (val) => emit('update:yearStart', val),
});

const yearEndModel = computed({
  get: () => props.yearEnd,
  set: (val) => emit('update:yearEnd', val),
});

const yearDetailModel = computed({
  get: () => props.yearDetail,
  set: (val) => emit('update:yearDetail', val),
});
</script>


