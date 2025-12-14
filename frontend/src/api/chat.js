import apiClient from './axios';

export function startChatSessionApi() {
  return apiClient.post('/api/v1/chat');
}

export function sendChatMessageApi(chatSessionId, message) {
  return apiClient.post(`/api/v1/chat/${chatSessionId}/messages`, {
    message,
  });
}

export function endChatSessionApi(chatSessionId) {
  return apiClient.post(`/api/v1/chat/${chatSessionId}/end`);
}

export function fetchChatHistoryApi(chatSessionId) {
  return apiClient.get(`/api/v1/chat/${chatSessionId}/messages`);
}
