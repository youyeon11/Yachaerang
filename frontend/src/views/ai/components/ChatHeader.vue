<template>
  <header class="border-b border-[#f0f0f0] bg-white w-full">
    <div class="w-full px-6 flex items-center justify-between py-[15px]">
      <div class="flex items-center gap-2.5">
        <span class="w-[10px] h-[10px] rounded-full bg-[#fecc21]" />
        <h2 class="text-base font-semibold text-[#222]">{{ title }}와 대화 중</h2>
      </div>

      <button
        type="button"
        @click="$emit('end-chat')"
        class="inline-flex items-center gap-1 px-3 py-1.5 text-[13px] font-semibold text-[#555] bg-white border border-[#dddddd] rounded-lg transition hover:bg-[#f5f5f5] hover:text-[#222] hover:border-[#cccccc] shrink-0"
      >
        <IconLogout class="w-[18px] h-[18px]" />
        대화 종료
      </button>
    </div>
  </header>
</template>
<script setup>
import IconLogout from "@/components/icons/IconLogout.vue";
defineProps({
  title: { type: String, required: true },
});
defineEmits(["end-chat"]);
</script>
