<template>
  <div class="flex gap-2 border-b border-gray-200">
    <button
      class="px-4 py-2 text-sm font-medium transition-colors"
      :class="activeTab === 'top' 
        ? 'border-b-2 border-[#F44323] text-[#F44323]' 
        : 'text-gray-600 hover:text-gray-900'"
      @click="$emit('change', 'top')"
    >
      시세 기준 상위 보기
    </button>
    <button
      class="px-4 py-2 text-sm font-medium transition-colors"
      :class="activeTab === 'bottom' 
        ? 'border-b-2 border-[#F44323] text-[#F44323]' 
        : 'text-gray-600 hover:text-gray-900'"
      @click="$emit('change', 'bottom')"
    >
      시세 기준 하위 보기
    </button>
  </div>
</template>

<script setup>
defineProps({
  activeTab: String,
})

defineEmits(['change'])
</script>