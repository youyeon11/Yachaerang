<template>
  <div class="ai-chat">
    <WelcomeScreen v-if="messages.length === 0" />
    <ChatMessageList v-else :messages="messages" :is-loading="isLoading" />

    <ChatInputBar
      :messages="messages"
      :is-loading="isLoading"
      @send="sendMessage"
      @reset="resetChat"
    />
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount } from 'vue';
import { useChat } from './composables/useChat';

import WelcomeScreen from './components/WelcomeScreen.vue';
import ChatMessageList from './components/ChatMessageList.vue';
import ChatInputBar from './components/ChatInputBar.vue';

const { messages, sendMessage, resetChat, isLoading, sessionId } = useChat();

const handleBeforeUnload = () => {
  // 세션이 살아있으면 종료 시도
  if (sessionId.value) {
    resetChat();
  }
};

onMounted(() => {
  window.addEventListener('beforeunload', handleBeforeUnload);
});

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload);
});
</script>

<style scoped>
.ai-chat {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding-bottom: 96px; /* 하단 인풋 영역 만큼 여유 */
  box-sizing: border-box;
  position: relative;
}
</style>
