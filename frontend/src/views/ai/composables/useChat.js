import { ref } from 'vue';
import axios from '@/api/axios';

export function useChat() {
  const messages = ref([]);
  const sessionId = ref(null);
  const isLoading = ref(false);

  // 채팅 세션 시작 (POST /api/v1/chat)
  async function startChat() {
    const res = await axios.post('/api/v1/chat');
    sessionId.value = res.data?.data; // 예: 2
  }

  // 메시지 전송 (POST /api/v1/chat/{sessionId}/messages)
  async function sendMessage(text) {
    const content = text?.trim();
    if (!content) return;

    // 세션 없으면 먼저 생성
    if (!sessionId.value) {
      await startChat();
    }

    // 사용자 메시지 표시
    messages.value.push({
      id: Date.now(),
      role: 'user',
      content,
    });

    try {
      isLoading.value = true;

      const res = await axios.post(`/api/v1/chat/${sessionId.value}/messages`, {
        message: content,
      });

      const data = res.data?.data;

      // 서버 응답 메시지 표시
      messages.value.push({
        id: Date.now() + 1,
        role: 'assistant', // ChatMessage.vue에서 이 값으로 스타일 분기
        content: data?.response ?? '응답을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.',
      });

      // 백엔드가 세션 ID를 응답에 포함해 줄 수 있으므로 동기화
      if (typeof data?.chatSessionId === 'number') {
        sessionId.value = data.chatSessionId;
      }
    } catch (error) {
      console.error(error);
      messages.value.push({
        id: Date.now() + 2,
        role: 'assistant',
        content: '챗봇 서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
      });
    } finally {
      isLoading.value = false;
    }
  }

  // 채팅 종료 (POST /api/v1/chat/{sessionId}/end) + 화면 리셋
  async function resetChat() {
    if (sessionId.value) {
      try {
        await axios.post(`/api/v1/chat/${sessionId.value}/end`);
      } catch (error) {
        console.error('채팅 종료 중 오류', error);
      }
    }

    messages.value = [];
    sessionId.value = null;
  }

  return {
    messages,
    sendMessage,
    resetChat,
    isLoading,
    sessionId,
  };
}
