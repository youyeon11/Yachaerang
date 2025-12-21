<template>
  <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
    <h2 class="mb-4 flex items-center gap-2 text-lg font-semibold text-gray-900">
      <IconNewspaper class="h-5 w-5 text-[#F44323]" />
      좋아요한 기사
    </h2>
    
    <div class="space-y-2">
      <div class="p-2 text-sm text-gray-400">
        좋아요한 기사가 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import IconNewspaper from '@/components/icons/IconNewspaper.vue';
</script>