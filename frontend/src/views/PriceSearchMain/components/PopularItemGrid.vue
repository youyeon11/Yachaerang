<template>
  <div class="grid">
    <PopularItemCard
      v-for="item in items"
      :key="item.itemCode ?? item.productCode ?? item.id"
      :item="item"
      @select="$emit('select', item)"
    />
  </div>
</template>

<script setup>
import PopularItemCard from '@/views/PriceSearchMain/components/PopularItemCard.vue';

defineProps({
  items: {
    type: Array,
    default: () => [],
  },
});

defineEmits(['select']);
</script>

<style scoped>
.grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
</style>
