import { ref, computed } from 'vue';
import { startChatSessionApi, sendChatMessageApi, endChatSessionApi, fetchChatHistoryApi } from '@/api/chat';

const DEFAULT_ERROR_MESSAGE = '챗봇 서버와 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
const STORAGE_KEY = 'yachi_chat_session_id';
const WELCOME_MESSAGE = {
  id: 'welcome',
  role: 'assistant',
  content: '안녕하세요! 저는 AI 야치예요 🌱\n농사, 시세, 재배 관리 등 무엇이든 편하게 질문해주세요!',
};

let messageIdCounter = 0;
let ensureSessionPromise = null;
let activeRequestToken = 0;

export function useChat() {
  const messages = ref([]);
  const sessionId = ref(null);
  const isLoading = ref(false);
  const isInitialized = ref(false);

  const displayMessages = computed(() => {
    return [WELCOME_MESSAGE, ...messages.value];
  });

  const saveSessionId = (id) => {
    if (id) {
      localStorage.setItem(STORAGE_KEY, String(id));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  };

  const loadSessionId = () => {
    const stored = localStorage.getItem(STORAGE_KEY);
    return stored ? Number(stored) : null;
  };

  const appendMessage = (role, content) => {
    messages.value.push({
      id: `${Date.now()}-${++messageIdCounter}`,
      role,
      content,
    });
  };

  const initSession = async () => {
    if (isInitialized.value) return;

    const storedId = loadSessionId();

    if (storedId) {
      try {
        const res = await fetchChatHistoryApi(storedId);
        const history = res.data?.data ?? res.data ?? [];

        if (Array.isArray(history) && history.length > 0) {
          sessionId.value = storedId;
          messages.value = history.map((msg, idx) => ({
            id: msg.id ?? `${Date.now()}-${idx}`,
            role: msg.role ?? (msg.isUser ? 'user' : 'assistant'),
            content: msg.content ?? msg.message ?? '',
          }));
          isInitialized.value = true;
          return;
        }

        sessionId.value = storedId;
      } catch (error) {
        console.warn('기존 세션 복원 실패:', error);
        saveSessionId(null);
      }
    }

    isInitialized.value = true;
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
      saveSessionId(id);
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
        saveSessionId(data.chatSessionId);
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
    saveSessionId(null);
  };

  return {
    messages,
    displayMessages,
    sessionId,
    isLoading,
    isInitialized,
    initSession,
    sendMessage,
    resetChat,
  };
}
