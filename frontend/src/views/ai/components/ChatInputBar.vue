<template>
  <div class="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
    <div class="flex items-center gap-2">
      <button
        v-if="showReset && messages && messages.length > 0"
        type="button"
        class="hidden sm:inline-flex items-center gap-1 rounded-md border border-gray-300 px-3 py-2 text-sm text-gray-700 transition-colors hover:bg-gray-50"
        @click="emit('reset')"
      >
        <span class="icon">↺</span>
        <span class="label">대화 종료</span>
      </button>

      <input
        v-model="text"
        type="text"
        :placeholder="isLoading ? '야치가 답변을 만드는 중이에요...' : '궁금한 점을 야치에게 질문해주세요!'"
        :disabled="isLoading"
        @keyup.enter="send"
        class="flex-1 rounded-lg border border-gray-300 px-4 py-2 text-sm focus:border-[#F44323] focus:outline-none focus:ring-2 focus:ring-[#F44323]/20 disabled:bg-gray-100"
      />

      <button
        type="button"
        class="flex items-center justify-center rounded-lg bg-[#F44323] px-4 py-2 text-white transition-colors hover:bg-[#d63a1f] disabled:cursor-default disabled:opacity-60"
        :disabled="isLoading"
        @click="send"
      >
        <IconSend v-if="!isLoading" class="h-4 w-4" />
        <span v-else class="spinner"></span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import IconSend from '@/components/icons/IconSend.vue';

const props = defineProps({
  messages: {
    type: Array,
    default: () => [],
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
  showReset: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(['send', 'reset']);

const text = ref('');

function send() {
  const message = text.value.trim();
  if (!message) return;
  emit('send', message);
  text.value = '';
}

// 새 대화로 리셋되면 입력창도 비워주기
watch(
  () => props.messages && props.messages.length,
  (newLen, oldLen) => {
    if (oldLen > 1 && newLen === 1) {
      text.value = '';
    }
  }
);
</script>

<style scoped>
.spinner {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-top-color: #ffffff;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
