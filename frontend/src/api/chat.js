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

// 챗봇 세션 종료 API
// POST /api/v1/chat/{chatSessionId}/end
export function endChatSessionApi(chatSessionId) {
  return apiClient.post(`/api/v1/chat/${chatSessionId}/end`);
}

// 챗봇 대화 히스토리 조회 API
// GET /api/v1/chat/{chatSessionId}/messages
export function fetchChatHistoryApi(chatSessionId) {
  return apiClient.get(`/api/v1/chat/${chatSessionId}/messages`);
}

