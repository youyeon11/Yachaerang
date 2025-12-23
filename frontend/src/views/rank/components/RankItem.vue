<template>
  <div
    @click="$emit('click')"
    class="item-card p-6 rounded-2xl flex flex-col justify-between h-[220px] cursor-pointer animate-fadeIn bg-white border hover:translate-y-[-4px] hover:shadow-lg transition-all"
    :class="[
      rank === 1
        ? 'border-yellow-400 shadow-sm'
        : rank === 2
        ? 'border-gray-300'
        : rank === 3
        ? 'border-orange-300'
        : 'border-gray-100 hover:border-gray-400',
    ]"
  >
    <div class="flex justify-between items-start">
      <span class="text-xs font-bold px-2 py-1 rounded w-fit" :class="rankBadgeClass"> {{ rank }}위 </span>
      <span class="text-sm font-bold" :class="priceColorClass"> {{ arrow }} {{ formattedRate }} </span>
    </div>

    <div class="min-w-0">
      <h3 class="text-xl font-bold text-gray-800 mb-1 truncate">
        {{ item.productName || item.itemName }}
      </h3>
      <p class="text-sm text-gray-400 font-medium">{{ item.unit || '단위 정보 없음' }}</p>
    </div>

    <div class="text-right border-t border-red-50 pt-4">
      <span class="text-2xl font-black text-gray-900">{{ formattedPrice }}</span>
      <span class="text-sm font-bold text-gray-400 ml-0.5">원</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  item: Object,
  rank: Number,
});

defineEmits(['click']);

const rankBadgeClass = computed(() => {
  if (props.rank === 1) return 'bg-yellow-400 text-white';
  if (props.rank === 2) return 'bg-gray-300 text-white';
  if (props.rank === 3) return 'bg-orange-300 text-white';
  return 'bg-gray-100 text-gray-600';
});

const formattedPrice = computed(() =>
  props.item.price || props.item.dpr1 ? Number(props.item.price || props.item.dpr1).toLocaleString() : '0'
);

const formattedRate = computed(() => {
  const rate = props.item.priceChangeRate;
  return rate === undefined || rate === null ? '0%' : `${Math.abs(rate)}%`;
});

const arrow = computed(() => {
  const rate = props.item.priceChangeRate;
  if (!rate || rate === 0) return '-';
  return rate > 0 ? '▲' : '▼';
});

const priceColorClass = computed(() => {
  const rate = props.item.priceChangeRate;
  if (!rate || rate === 0) return 'text-gray-400';
  return rate > 0 ? 'text-red-500' : 'text-blue-500';
});
</script>
<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.animate-fadeIn {
  animation: fadeIn 0.3s ease-out forwards;
}
</style>
