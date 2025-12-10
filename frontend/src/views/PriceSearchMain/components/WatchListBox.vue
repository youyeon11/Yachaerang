<template>
  <div class="box">
    <div class="header">
      <h4>관심 품목</h4>
      <button class="edit-btn" @click="$emit('edit')">수정</button>
    </div>

    <div class="list">
      <div class="item" v-for="(item, i) in items" :key="i">
        {{ item }}
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  items: Array,
});
</script>

<style scoped>
.box {
  width: 280px;
  border-radius: 16px;
  padding: 20px 18px;
  background: #ffffff;
  border: 1px solid #e5e5e5;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h4 {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}

.edit-btn {
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 6px;
  border: none;
  background: #f0f0f0;
  cursor: pointer;
  transition: 0.2s ease;
}

.edit-btn:hover {
  background: #e0e0e0;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.item {
  padding: 10px 12px;
  border-radius: 10px;
  background: #fafafa;
  border: 1px solid #ebebeb;
  font-size: 14px;
  line-height: 1.4;
  color: #444;
  transition: 0.18s ease;
}

.item:hover {
  background: #f3f3f3;
}
</style>
