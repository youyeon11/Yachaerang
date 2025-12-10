import { ref } from 'vue';

export function useChat() {
  const messages = ref([]);

  function sendMessage(text) {
    messages.value.push({
      id: Date.now(),
      role: 'user',
      content: text,
    });

    setTimeout(() => {
      messages.value.push({
        id: Date.now() + 1,
        role: 'assistant',
        content: '안녕하세요! 무엇을 도와드릴까요?',
      });
    }, 400);
  }

  function resetChat() {
    messages.value = [];
  }

  return { messages, sendMessage, resetChat };
}
