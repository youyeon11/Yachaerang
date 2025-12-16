import apiClient from './axios';

export function fetchArticles(params) {
  return apiClient.get('/api/v1/articles', { params });
}

export function fetchArticleDetail(articleId) {
  return apiClient.get(`/api/v1/articles/${articleId}`);
}
