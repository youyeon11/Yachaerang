<template>
  <div class="grid">
    <PopularItemCard v-for="item in items" :key="item.itemCode" :item="item" />
  </div>
</template>

<script setup>
import PopularItemCard from './PopularItemCard.vue';

defineProps({
  items: Array,
});
</script>

<style scoped>
.grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
</style>
