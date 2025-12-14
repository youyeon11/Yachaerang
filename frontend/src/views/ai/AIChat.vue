<template>
  <div class="ai-chat">
    <WelcomeScreen v-if="messages.length === 0" @use-prompt="sendMessage" />
    <ChatMessageList v-else :messages="messages" :is-loading="isLoading" />

    <ChatInputBar :messages="messages" :is-loading="isLoading" @send="sendMessage" @reset="resetChat" />
  </div>
</template>

<script setup>
import { useChat } from './composables/useChat';
import { onMounted } from 'vue';

import WelcomeScreen from './components/WelcomeScreen.vue';
import ChatMessageList from './components/ChatMessageList.vue';
import ChatInputBar from './components/ChatInputBar.vue';

const { messages, sendMessage, resetChat, initSession, isLoading } = useChat();
onMounted(() => {
  initSession();
});
</script>

<style scoped>
.ai-chat {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  padding-bottom: 96px;
  box-sizing: border-box;
  position: relative;
}
</style>
