import apiClient from './axios';

// 챗봇 세션 시작 API
// POST /api/v1/chat
export function startChatSessionApi() {
  return apiClient.post('/api/v1/chat');
}

// 챗봇에 메시지 전송 API
// POST /api/v1/chat/{chatSessionId}/messages
export function sendChatMessageApi(chatSessionId, message) {
  return apiClient.post(`/api/v1/chat/${chatSessionId}/messages`, {
    message,
  });
}


