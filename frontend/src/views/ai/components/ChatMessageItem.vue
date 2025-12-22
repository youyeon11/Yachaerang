<template>
  <div :class="['flex gap-3 max-w-[80%]', isAssistant ? 'self-start' : 'self-end']">
    <div v-if="isAssistant" class="flex items-center justify-center w-[35px] h-[35px] rounded-lg text-white font-bold text-sm shrink-0 bg-[#10a37f]">
      <img v-if="aiProfile.avatarUrl" :src="aiProfile.avatarUrl" alt="AI 프로필" class="w-full h-full object-cover rounded-lg" />
      <span v-else>{{ (aiProfile.name || "A")[0] }}</span>
    </div>

    <div class="flex flex-col">
      <div class="px-1 text-xs text-[#888] mb-1" :class="isAssistant ? 'text-left' : 'text-right'">
        {{ isAssistant ? aiProfile.name : userProfile.name }}
      </div>

      <div class="px-[18px] py-[14px] rounded-xl text-[15px] leading-[1.6]" :class="isAssistant ? 'bg-[#f1f3f5] text-[#212121]' : 'bg-[#fecc21] text-[#212121]'">
        <div class="markdown-body" v-html="render(msg.content)"></div>
      </div>
    </div>

    <div v-if="!isAssistant" class="flex items-center justify-center w-[35px] h-[35px] rounded-lg text-white font-bold text-sm shrink-0 bg-[#5436da]">
      <img v-if="userProfile.avatarUrl" :src="userProfile.avatarUrl" alt="내 프로필" class="w-full h-full object-cover rounded-lg" />
      <span v-else>{{ userInitial }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useMarkdown } from "@/views/ai/composables/useMarkdown";

const props = defineProps({
  msg: { type: Object, required: true },
  aiProfile: { type: Object, required: true },
  userProfile: { type: Object, required: true },
});

const isAssistant = computed(() => props.msg.role === "assistant");
const userInitial = computed(() => (props.userProfile?.name?.[0] ? props.userProfile.name[0] : ""));

const { render } = useMarkdown();
</script>
