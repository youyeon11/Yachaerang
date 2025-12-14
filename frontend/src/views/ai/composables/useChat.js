import { ref } from 'vue';
import { startChatSessionApi, sendChatMessageApi, endChatSessionApi } from '@/api/chat';

const DEFAULT_ERROR_MESSAGE = '챗봇 서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';

let messageIdCounter = 0;
let ensureSessionPromise = null;
let activeRequestToken = 0;

export function useChat() {
  const messages = ref([]);
  const sessionId = ref(null);
  const isLoading = ref(false);

  const appendMessage = (role, content) => {
    messages.value.push({
      id: `${Date.now()}-${++messageIdCounter}`,
      role,
      content,
    });
  };

  const ensureSession = async () => {
    if (sessionId.value) return sessionId.value;
    if (ensureSessionPromise) return ensureSessionPromise;

    ensureSessionPromise = (async () => {
      const res = await startChatSessionApi();
      const id = res.data?.data;

      if (typeof id !== 'number') {
        throw new Error('챗봇 세션 ID를 가져오지 못했습니다.');
      }

      sessionId.value = id;
      return id;
    })().finally(() => {
      ensureSessionPromise = null;
    });

    return ensureSessionPromise;
  };

  const sendMessage = async (text) => {
    const content = text?.trim();
    if (!content) return;
    if (isLoading.value) return;

    appendMessage('user', content);
    const requestToken = ++activeRequestToken;

    try {
      isLoading.value = true;

      const currentSessionId = await ensureSession();
      const res = await sendChatMessageApi(currentSessionId, content);
      const data = res.data?.data;

      if (requestToken === activeRequestToken) {
        appendMessage('assistant', data?.response ?? '응답을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.');
      }

      if (typeof data?.chatSessionId === 'number') {
        sessionId.value = data.chatSessionId;
      }
    } catch (error) {
      console.error(error);
      if (requestToken === activeRequestToken) {
        appendMessage('assistant', DEFAULT_ERROR_MESSAGE);
      }
    } finally {
      isLoading.value = false;
    }
  };

  const resetChat = async () => {
    activeRequestToken++;
    if (sessionId.value) {
      try {
        await endChatSessionApi(sessionId.value);
      } catch (error) {
        console.error('채팅 종료 중 오류', error);
      }
    }

    messages.value = [];
    sessionId.value = null;
  };

  const endSessionOnUnload = () => {
    if (!sessionId.value) return;

    const accessToken = localStorage.getItem('accessToken');
    if (!accessToken) return;

    const url = `/api/v1/chat/${sessionId.value}/end`;

    try {
      fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json;charset=UTF-8',
          Authorization: `Bearer ${accessToken}`,
        },
        keepalive: true,
      }).catch(() => {});
    } catch {}
  };

  return {
    messages,
    sendMessage,
    resetChat,
    isLoading,
    sessionId,
    endSessionOnUnload,
  };
}
