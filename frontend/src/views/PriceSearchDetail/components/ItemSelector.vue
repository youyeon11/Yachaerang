<template>
  <div class="row row-top">
    <div class="field">
      <span class="field-label">품목</span>
      <select v-model="itemModel" class="select">
        <option value="">선택</option>
        <option
          v-for="item in items"
          :key="item.value"
          :value="item.value"
        >
          {{ item.label }}
        </option>
      </select>
    </div>
    <div class="field">
      <span class="field-label">품종</span>
      <select
        v-model="varietyModel"
        class="select"
        :disabled="!itemModel"
      >
        <option value="">선택</option>
        <option
          v-for="variety in varieties"
          :key="variety.value"
          :value="variety.value"
        >
          {{ variety.label }}
        </option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  },
  varieties: {
    type: Array,
    default: () => [],
  },
  selectedItem: {
    type: String,
    default: '',
  },
  selectedVariety: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['update:selectedItem', 'update:selectedVariety']);

const itemModel = computed({
  get: () => props.selectedItem,
  set: (val) => emit('update:selectedItem', val),
});

const varietyModel = computed({
  get: () => props.selectedVariety,
  set: (val) => emit('update:selectedVariety', val),
});
</script>


