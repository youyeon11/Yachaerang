<template>
  <div class="mb-3">
    <p class="text-[13px] text-[#666] mb-2">아래 질문들로 바로 시작해 보세요</p>
    <div class="flex flex-wrap gap-2">
      <button
        v-for="(item, index) in items"
        :key="index"
        type="button"
        @click="$emit('pick', item)"
        class="px-3 py-1.5 text-[13px] border border-[#e0e0e0] rounded-full transition hover:bg-[#fff9d9] hover:border-[#fecc21]"
      >
        {{ item }}
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  items: { type: Array, required: true },
});
defineEmits(['pick']);
</script>
