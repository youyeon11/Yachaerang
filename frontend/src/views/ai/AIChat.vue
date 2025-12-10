<template>
  <div class="ai-chat">
    <WelcomeScreen v-if="messages.length === 0" />
    <ChatMessage v-else v-for="msg in messages" :key="msg.id" :message="msg" />
    <ChatInputBar :messages="messages" @send="sendMessage" @reset="resetChat" />
  </div>
</template>

<script setup>
import { useChat } from './composables/useChat';

import WelcomeScreen from './components/WelcomeScreen.vue';
import ChatMessage from './components/ChatMessage.vue';
import ChatInputBar from './components/ChatInputBar.vue';

const { messages, sendMessage, resetChat } = useChat();
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
</style>
