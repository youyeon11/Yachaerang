<template>
  <div class="flex flex-col h-screen overflow-hidden bg-[#f9f9f9]">
    <div class="relative flex flex-1 flex-col w-full h-full bg-white">
      <ChatHeader :title="aiProfile.name" @end-chat="endChat" />

      <ChatMessageList
        ref="messageListRef"
        :messages="displayMessages"
        :ai-profile="aiProfile"
        :user-profile="userProfile"
        :is-loading="isLoading"
        @scroll-state="onScrollState"
      />

      <ChatNewMessageButton v-if="showNewMsgBtn" @click="scrollToBottom(true)" />

      <footer class="bg-white border-t border-[#eee] py-5 px-[10%]">
        <ChatSuggestions v-if="showSuggestions" :items="suggestions" @pick="handleSuggestionClick" />

        <ChatInputBar
          v-model="userInput"
          :disabled="isLoading"
          :placeholder="isLoading ? '야치가 답변을 만드는 중이에요...' : '메시지를 입력하세요...'"
          @send="handleSend"
        />
      </footer>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue';
import 'github-markdown-css/github-markdown-light.css';

import ChatHeader from '@/views/ai/components/ChatHeader.vue';
import ChatMessageList from '@/views/ai/components/ChatMessageList.vue';
import ChatNewMessageButton from '@/views/ai/components/ChatNewMessageButton.vue';
import ChatSuggestions from '@/views/ai/components/ChatSuggestions.vue';
import ChatInputBar from '@/views/ai/components/ChatInputBar.vue';

import { useChat } from '@/views/ai/composables/useChat';
import { useUserProfile } from '@/views/ai/composables/useUserProfile';
import yachiAvatar from '@/assets/yachi.png';

const { displayMessages, sendMessage, resetChat, initSession, isLoading } = useChat();

const userInput = ref('');
const showNewMsgBtn = ref(false);
const isUserScrolling = ref(false);

const messageListRef = ref(null);

const aiProfile = reactive({
  name: 'AI 야치',
  avatarUrl: yachiAvatar,
});

const { userProfile, loadUserProfile } = useUserProfile();

const suggestions = [
  '방울토마토와 같이 심으면 서로 성장을 돕는 동반 식물에는 어떤 게 있어?',
  '진딧물이 생겼는데 약을 안 쓰고 없애는 방법이 있을까?',
  '최근 농산물 시장 동향을 요약해줘.',
  '깻잎 뒷면에 하얀 가루 같은 게 생겼어. 이거 병이야? 친환경적으로 해결할 수 있는 난황유 만드는 법 알려줘.',

  '감자를 수확할 때가 된 것 같은데, 잎이 어떤 상태일 때 캐야 가장 맛있어?',
];

const showSuggestions = computed(() => displayMessages.value && displayMessages.value.length === 1);

onMounted(async () => {
  await initSession();
  await loadUserProfile();
  scrollToBottom(true);
});

const handleSend = async () => {
  const text = userInput.value.trim();
  if (!text || isLoading.value) return;

  userInput.value = '';
  await sendMessage(text);

  if (!isUserScrolling.value) scrollToBottom();
};

const handleSuggestionClick = async (text) => {
  if (!text) return;
  userInput.value = text;
  await handleSend();
};

const endChat = async () => {
  if (confirm('대화를 종료하시겠습니까?')) {
    await resetChat();
    showNewMsgBtn.value = false;
    scrollToBottom(true);
  }
};

const scrollToBottom = (force = false) => {
  nextTick(() => {
    messageListRef.value?.scrollToBottom?.(force);
    showNewMsgBtn.value = false;
    isUserScrolling.value = false;
  });
};

const onScrollState = ({ showNewButton, isUserScrolling: scrolling }) => {
  showNewMsgBtn.value = showNewButton;
  isUserScrolling.value = scrolling;
};
</script>
