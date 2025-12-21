<script setup>
import IconArrowDown from '@/components/icons/IconArrowDown.vue';
defineEmits(['click']);
</script>

<template>
  <button
    type="button"
    @click="$emit('click')"
    class="absolute left-1/2 bottom-[130px] -translate-x-1/2 inline-flex items-center gap-2 px-5 py-2.5 rounded-[30px] bg-[#f4e6b8] border border-[#eedea1] shadow-[0_4px_12px_rgba(0,0,0,0.1)] cursor-pointer z-10 transition hover:bg-[#ffd84f] hover:border-[#e0b91d] hover:-translate-y-0.5"
  >
    <IconArrowDown class="w-[18px] h-[18px]" />
    새 메시지 확인
  </button>
</template>
