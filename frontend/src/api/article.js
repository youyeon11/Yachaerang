import { publicClient, apiClient } from './axios';

export function fetchArticles(params) {
  return publicClient.get('/api/v1/articles', { params });
}

export function fetchArticleDetail(articleId) {
  return publicClient.get(`/api/v1/articles/${articleId}`);
}

export function searchArticles(params) {
  return publicClient.get('/api/v1/articles/search', { params });
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