<template>
  <div class="flex flex-col gap-[25px] py-[30px]">
    <div class="max-w-[900px] w-full mx-auto px-4 flex flex-col gap-[25px]">
      <ChatMessageItem
        v-for="msg in messages"
        :key="msg.id"
        :msg="msg"
        :ai-profile="aiProfile"
        :user-profile="userProfile"
      />

      <ChatLoadingBubble v-if="isLoading" />
    </div>
  </div>
</template>

<script setup>
import ChatMessageItem from './ChatMessageItem.vue';
import ChatLoadingBubble from './ChatLoadingBubble.vue';

const props = defineProps({
  messages: { type: Array, required: true },
  aiProfile: { type: Object, required: true },
  userProfile: { type: Object, required: true },
  isLoading: { type: Boolean, required: true },
});
</script>
