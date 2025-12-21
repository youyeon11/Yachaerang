<template>
  <div
    class="flex flex-1 flex-col gap-[25px] overflow-y-auto py-[30px] px-[10%]"
    ref="chatBoxRef"
    @scroll="handleScroll"
  >
    <ChatMessageItem
      v-for="msg in messages"
      :key="msg.id"
      :msg="msg"
      :ai-profile="aiProfile"
      :user-profile="userProfile"
    />

    <ChatLoadingBubble v-if="isLoading" />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import ChatMessageItem from './ChatMessageItem.vue';
import ChatLoadingBubble from './ChatLoadingBubble.vue';

const props = defineProps({
  messages: { type: Array, required: true },
  aiProfile: { type: Object, required: true },
  userProfile: { type: Object, required: true },
  isLoading: { type: Boolean, required: true },
});

const emit = defineEmits(['scroll-state']);

const chatBoxRef = ref(null);

const scrollToBottom = (force = false) => {
  const el = chatBoxRef.value;
  if (!el) return;

  if (force) {
    el.scrollTop = el.scrollHeight;
    emit('scroll-state', { showNewButton: false, isUserScrolling: false });
    return;
  }
  el.scrollTop = el.scrollHeight;
};

const handleScroll = () => {
  const el = chatBoxRef.value;
  if (!el) return;

  const { scrollTop, scrollHeight, clientHeight } = el;
  const showNewButton = scrollHeight - scrollTop > clientHeight + 150;
  emit('scroll-state', { showNewButton, isUserScrolling: showNewButton });
};

defineExpose({ scrollToBottom });
</script>
