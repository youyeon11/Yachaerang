import apiClient from './axios';

export function addFavorite(payload) {
  return apiClient.post('/api/v1/favorites', payload);
}

export function fetchFavorites() {
  return apiClient.get('/api/v1/favorites');
}

export function removeFavorite(favoriteId) {
  return apiClient.delete(`/api/v1/favorites/${favoriteId}`);
}
