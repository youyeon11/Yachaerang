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

<style scoped>
.result-view-toggle {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 3px;
  border-radius: 999px;
  background-color: #f5f5f7;
}

.toggle-btn {
  border: none;
  background: transparent;
  padding: 6px 14px;
  border-radius: 999px;
  font-size: 12px;
  cursor: pointer;
  color: #777;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  transition: background-color 0.2s, color 0.2s;
}

.toggle-btn.active {
  background-color: #e53935;
  color: #fff;
}

.toggle-btn:not(.active):hover {
  background-color: #e0e0e4;
}
</style>