<template>
  <div class="tabs">
    <button :class="{ active: activeTab === 'top' }" @click="$emit('change', 'top')">시세 기준 상위 보기</button>

    <button :class="{ active: activeTab === 'bottom' }" @click="$emit('change', 'bottom')">시세 기준 하위 보기</button>
  </div>
</template>

<script setup>
defineProps({
  activeTab: String,
});
</script>

<style scoped>
.tabs {
  display: flex;
  gap: 8px;
}

button {
  padding: 10px 18px;
  background: #eee;
  border-radius: 8px;
  border: none;
  cursor: pointer;
}

button.active {
  background: #e53935;
  color: #fff;
}
</style>
