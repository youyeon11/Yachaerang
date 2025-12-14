<template>
  <div ref="messagesEl" class="messages">
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

const scrollToBottom = async () => {
  await nextTick();

  const anchor = bottomAnchor.value;
  if (!anchor || typeof anchor.scrollIntoView !== 'function') return;

  anchor.scrollIntoView({ behavior: 'smooth', block: 'end' });
};

onMounted(() => {
  scrollToBottom();
});

watch(
  () => [props.messages.length, props.isLoading],
  () => {
    scrollToBottom();
  },
  { flush: 'post' }
);
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
