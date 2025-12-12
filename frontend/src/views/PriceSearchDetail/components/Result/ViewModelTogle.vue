<template>
  <div class="result-view-toggle">
    <button
      type="button"
      class="toggle-btn"
      :class="{ active: modelValue === 'table' }"
      @click="$emit('update:modelValue', 'table')"
    >
      📋 표로 보기
    </button>

    <button
      type="button"
      class="toggle-btn"
      :class="{ active: modelValue === 'chart' }"
      @click="$emit('update:modelValue', 'chart')"
    >
      📊 그래프로 보기
    </button>
  </div>
</template>

<script setup>
defineProps({
  modelValue: { type: String, required: true },
});
defineEmits(['update:modelValue']);
</script>
