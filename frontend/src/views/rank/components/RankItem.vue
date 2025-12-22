<template>
  <div
    @click="$emit('click')"
    class="item-card p-6 rounded-2xl flex flex-col justify-between h-[220px] cursor-pointer animate-fadeIn bg-white border border-gray-100 hover:translate-y-[-4px] hover:shadow-lg hover:border-gray-400 transition-all"
  >
    <div class="flex justify-between items-start">
      <span class="text-xs font-bold text-gray-600 bg-gray-100 px-2 py-1 rounded">{{ rank }}위</span>
      <span class="text-sm font-bold" :class="priceColorClass">{{ arrow }} {{ formattedRate }}</span>
    </div>
    <div class="min-w-0">
      <h3 class="text-xl font-bold text-gray-800 mb-1 truncate">{{ item.productName || item.itemName }}</h3>
      <p class="text-sm text-gray-400 font-medium">{{ item.unit || "단위 정보 없음" }}</p>
    </div>
    <div class="text-right border-t border-red-50 pt-4">
      <span class="text-2xl font-black text-gray-900">{{ formattedPrice }}</span>
      <span class="text-sm font-bold text-gray-400 ml-0.5">원</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

const props = defineProps({
  item: Object,
  rank: Number,
});

defineEmits(["click"]);

const formattedPrice = computed(() => (props.item.price || props.item.dpr1 ? Number(props.item.price || props.item.dpr1).toLocaleString() : "0"));

const formattedRate = computed(() => {
  const rate = props.item.priceChangeRate;
  return rate === undefined || rate === null ? "0%" : `${Math.abs(rate)}%`;
});

const arrow = computed(() => {
  const rate = props.item.priceChangeRate;
  if (!rate || rate === 0) return "-";
  return rate > 0 ? "▲" : "▼";
});

const priceColorClass = computed(() => {
  const rate = props.item.priceChangeRate;
  if (!rate || rate === 0) return "text-gray-400";
  return rate > 0 ? "text-red-500" : "text-blue-500";
});
</script>
