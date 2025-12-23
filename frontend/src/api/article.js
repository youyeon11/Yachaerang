import { publicClient } from './axios';

export function fetchArticles(params) {
  return publicClient.get('/api/v1/articles', { params });
}

export function fetchArticleDetail(articleId) {
  return publicClient.get(`/api/v1/articles/${articleId}`);
}

export function searchArticles(params) {
  return publicClient.get('/api/v1/articles/search', { params });
}
