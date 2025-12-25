import { publicClient, apiClient } from './axios';

function getClient() {
  const token = localStorage.getItem('accessToken')
  return token ? apiClient : publicClient
}

export default getClient

export function fetchArticles(params) {
  return getClient().get('/api/v1/articles', { params })
}

export function fetchArticleDetail(articleId) {
  return getClient().get(`/api/v1/articles/${articleId}`)
}

export function searchArticles(params) {
  return getClient().get('/api/v1/articles/search', { params })
}

export function saveBookmark(articleId) {
  return apiClient.post('/api/v1/bookmarks', null, {
    params: { articleId }
  });
}

export function removeBookmark(articleId) {
  return apiClient.delete('/api/v1/bookmarks', {
    params: { articleId }
  });
}

export function fetchBookmarks() {
  return apiClient.get('/api/v1/bookmarks');
}

// 리액션 API
export function addReaction(articleId, reactionType) {
  return apiClient.post('/api/v1/articles/reactions', null, {
    params: { articleId, reactionType }
  });
}

export function removeReaction(articleId, reactionType) {
  return apiClient.delete('/api/v1/articles/reactions', {
    params: { articleId, reactionType }
  });
}

export function fetchReactionStatistics(articleId) {
  return getClient().get(`/api/v1/articles/reactions/${articleId}`);
}

export function fetchReactionMembers(articleId, reactionType) {
  return getClient().get(`/api/v1/articles/reactions/${articleId}/${reactionType}`);
}