<template>
  <div class="ai-chat">
    <WelcomeScreen v-if="isInitialized && displayMessages.length === 1" @use-prompt="sendMessage" />

    <ChatMessageList v-else :messages="displayMessages" :is-loading="isLoading" />

    <ChatInputBar :messages="displayMessages" :is-loading="isLoading" @send="sendMessage" @reset="resetChat" />
  </div>
</template>

<script setup>
import { useChat } from './composables/useChat';
import { onMounted } from 'vue';

import WelcomeScreen from './components/WelcomeScreen.vue';
import ChatMessageList from './components/ChatMessageList.vue';
import ChatInputBar from './components/ChatInputBar.vue';

const { displayMessages, sendMessage, resetChat, initSession, isLoading, isInitialized } = useChat();

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
