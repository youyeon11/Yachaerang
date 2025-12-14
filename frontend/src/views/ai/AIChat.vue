<template>
  <div class="ai-chat">
    <WelcomeScreen v-if="messages.length === 0" />
    <ChatMessage v-else v-for="msg in messages" :key="msg.id" :message="msg" />

    <div v-if="isLoading" class="loading-indicator">
      야치가 답변을 만드는 중이에요...
    </div>

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
import ChatMessage from './components/ChatMessage.vue';
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
  padding-bottom: 80px;
  box-sizing: border-box;
  position: relative;
}

.loading-indicator {
  margin: 12px auto 0;
  padding: 8px 14px;
  border-radius: 999px;
  background: #f2f2f2;
  color: #777;
  font-size: 14px;
}
</style>
