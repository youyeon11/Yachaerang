<template>
  <div class="card">
    <h3>{{ item.itemName }}</h3>
    <small>{{ item.unit }}</small>
    <p class="price">{{ (item.price ?? 0).toLocaleString() }}원</p>
  </div>
</template>

<script setup>
defineProps({
  item: {
    type: Object,
    required: true,
    validator: (value) => {
      return value.itemName && typeof value.price === 'number';
    },
  },
});
</script>

<style scoped>
.card {
  padding: 20px;
  border-radius: 16px;
  border: 1px solid #eee;
  background: #fff;
}
.price {
  margin-top: 4px;
  font-size: 20px;
  font-weight: 700;
}
.card:hover {
  background: #e53935;
  color: #fff;
  border-color: #e53935;
  transform: translateY(-6px);
}
</style>
