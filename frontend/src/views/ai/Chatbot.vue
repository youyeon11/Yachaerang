<template>
  <main class="flex flex-1 flex-col p-8 bg-gray-50">
    <div class="mx-auto flex w-full max-w-4xl flex-1 flex-col space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight text-gray-900">Agricultural Assistant</h1>
          <p class="text-gray-600">Get personalized farming advice and market insights</p>
        </div>
        <button
          @click="handleNewChat"
          class="flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-4 py-2 font-medium text-gray-700 transition-colors hover:bg-gray-100 hover:border-gray-400 hover:text-gray-900"
        >
          <IconRefresh class="h-4 w-4" />
          New Chat
        </button>
      </div>

      <!-- Chat Messages -->
      <div class="flex-1 rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div class="h-[500px] overflow-y-auto pr-4">
          <div class="space-y-4">
            <WelcomeScreen v-if="isInitialized && displayMessages.length === 1" @use-prompt="sendMessage" />

            <ChatMessageList v-else :messages="displayMessages" :is-loading="isLoading" />
          </div>
        </div>
      </div>

      <ChatInputBar :messages="displayMessages" :is-loading="isLoading" :show-reset="false" @send="handleSend" />
    </div>
  </main>
</template>

<script setup>
import { onMounted } from 'vue';
import IconRefresh from '@/components/icons/IconRefresh.vue';

import { useChat } from './composables/useChat';
import WelcomeScreen from './components/WelcomeScreen.vue';
import ChatMessageList from './components/ChatMessageList.vue';
import ChatInputBar from './components/ChatInputBar.vue';

const { displayMessages, sendMessage, resetChat, initSession, isLoading, isInitialized } = useChat();

const handleSend = async (message) => {
  if (isLoading.value) return;
  const trimmed = (message ?? '').trim();
  if (!trimmed) return;

  await sendMessage(trimmed);
};

const handleNewChat = () => {
  resetChat();
};

onMounted(() => {
  initSession();
});
</script>
