<template>
  <div ref="messagesEl" class="messages" @scroll="checkIsAtBottom">
    <ChatMessage v-for="msg in messages" :key="msg.id" :message="msg" />

    <div v-if="isLoading" class="loading-bubble">야치가 답변을 만드는 중이에요...</div>

    <div ref="bottomAnchor" class="scroll-anchor" />
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue';
import ChatMessage from './ChatMessage.vue';

const props = defineProps({
  messages: {
    type: Array,
    default: () => [],
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
});

const messagesEl = ref(null);
const bottomAnchor = ref(null);

const isUserAtBottom = ref(true);

const forceScroll = ref(false);

const checkIsAtBottom = () => {
  const el = messagesEl.value;
  if (!el) return;

  const threshold = 80;
  const distanceFromBottom = el.scrollHeight - el.scrollTop - el.clientHeight;

  isUserAtBottom.value = distanceFromBottom < threshold;
};

const scrollToBottom = async (force = false) => {
  await nextTick();

  if (!force && !isUserAtBottom.value) return;

  bottomAnchor.value?.scrollIntoView({
    behavior: 'smooth',
    block: 'end',
  });
};

watch(
  () => [props.messages.length, props.isLoading],
  () => {
    scrollToBottom(forceScroll.value);
    forceScroll.value = false;
  },
  { flush: 'post' }
);

onMounted(() => {
  scrollToBottom(true);
});
</script>

<style scoped>
.messages {
  flex: 1;
  padding: 20px 0 16px;
  overflow-y: auto;
}

.loading-bubble {
  max-width: 90%;
  margin: 8px auto 80px;
  padding: 8px 14px;
  border-radius: 999px;
  background: #f2f2f2;
  color: #777;
  font-size: 14px;
  text-align: center;
}

.scroll-anchor {
  height: 1px;
}
</style>
