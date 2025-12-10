<template>
  <div class="field period-field">
    <span class="field-label">기간</span>
    <div class="period-tabs">
      <button
        v-for="tab in periodTabs"
        :key="tab.value"
        type="button"
        class="period-tab"
        :class="{ active: periodType === tab.value }"
        @click="$emit('change', tab.value)"
      >
        {{ tab.label }}
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  periodTabs: {
    type: Array,
    default: () => [],
  },
  periodType: {
    type: String,
    default: 'year',
  },
});

defineEmits(['change']);
</script>


