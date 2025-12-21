<template>
  <button 
    class="rounded-lg border border-gray-200 bg-white p-4 shadow-sm transition-all hover:shadow-md w-full text-left cursor-pointer"
    type="button" 
    @click="$emit('select', item)"
  >
    <div class="flex items-start justify-between">
      <div class="flex-1">
        <h3 class="font-semibold text-gray-900">{{ item.itemName }}</h3>
        <p class="text-sm text-gray-600">{{ item.unit }}</p>
      </div>
      <div v-if="item.change !== undefined" class="flex items-center gap-1">
        <IconArrowUp v-if="item.change > 0" class="h-4 w-4 text-red-500" />
        <IconArrowDown v-else class="h-4 w-4 text-blue-500" />
        <span 
          :class="item.change > 0 ? 'text-red-500' : 'text-blue-500'" 
          class="text-sm font-medium"
        >
          {{ Math.abs(item.change) }}%
        </span>
      </div>
    </div>
    <div class="mt-3">
      <p class="text-2xl font-bold text-[#F44323]">
        ₩{{ (item.price ?? 0).toLocaleString() }}
      </p>
    </div>
  </button>
</template>

<script setup>
import IconArrowUp from '@/components/icons/IconArrowUp.vue'
import IconArrowDown from '@/components/icons/IconArrowDown.vue'

defineProps({
  item: {
    type: Object,
    required: true,
    validator: (value) => {
      return value.itemName && typeof value.price === 'number'
    },
  },
})

defineEmits(['select'])
</script>