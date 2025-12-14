<template>
  <div ref="messagesEl" class="messages">
    <ChatMessage v-for="msg in messages" :key="msg.id" :message="msg" />

    <!-- 로딩 중일 때 하단에 표시되는 상태 메시지 -->
    <div v-if="isLoading" class="loading-bubble">
      야치가 답변을 만드는 중이에요...
    </div>
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
const scrollContainer = ref(null);

const scrollToBottom = async () => {
  await nextTick();

  const target = scrollContainer.value;
  if (target) {
    target.scrollTop = target.scrollHeight;
  }

  // 혹시 상위 레이아웃이 다른 스크롤 컨테이너를 사용 중일 경우를 대비해
  if (typeof window !== 'undefined' && typeof document !== 'undefined') {
    const doc = document.documentElement || document.body;
    window.scrollTo({
      top: doc.scrollHeight,
      behavior: 'smooth',
    });
  }
};

onMounted(() => {
  // 실제 스크롤이 발생하는 컨테이너(.main-area)가 있으면 거기를 스크롤 대상으로 사용
  scrollContainer.value = document.querySelector('.main-area') || messagesEl.value;
  scrollToBottom();
});

watch(
  () => [props.messages.length, props.isLoading],
  () => {
    scrollToBottom();
  }
);
</script>

<style scoped>
.messages {
  flex: 1;
  padding: 20px 0 32px;
}

.loading-bubble {
  max-width: 90%;
  margin: 8px auto 0;
  padding: 8px 14px;
  border-radius: 999px;
  background: #f2f2f2;
  color: #777;
  font-size: 14px;
  text-align: center;
}
</style>
