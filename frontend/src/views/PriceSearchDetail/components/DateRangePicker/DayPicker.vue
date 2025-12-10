<template>
  <div class="date-range">
    <VDatePicker
      v-model="startModel"
      :max-date="maxDate"
      locale="ko"
      color="red"
      :popover="{ visibility: 'click' }"
    >
      <template #default="{ inputValue, togglePopover }">
        <div class="date-input clickable" @click="togglePopover">
          <span class="date-icon">📅</span>
          <input
            :value="inputValue"
            class="date-field"
            placeholder="시작일"
            readonly
          />
        </div>
      </template>
    </VDatePicker>

    <span class="date-separator">~</span>

    <VDatePicker
      v-model="endModel"
      :max-date="maxDate"
      :min-date="startModel"
      locale="ko"
      color="red"
      :popover="{ visibility: 'click' }"
    >
      <template #default="{ inputValue, togglePopover }">
        <div class="date-input clickable" @click="togglePopover">
          <span class="date-icon">📅</span>
          <input
            :value="inputValue"
            class="date-field"
            placeholder="종료일"
            readonly
          />
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
  maxDate: {
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
</script>


