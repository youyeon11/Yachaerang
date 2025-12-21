import { apiClient } from './axios';

export function getMyFarm() {
  return apiClient.get('/api/v1/farms');
}

export function createFarm(payload) {
  return apiClient.post('/api/v1/farms', payload);
}

export function updateFarm(payload) {
  return apiClient.patch('/api/v1/farms', payload);
}
