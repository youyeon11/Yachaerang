import axios from '@/api/axios';

export function getMyProfile() {
  return axios.get('/api/v1/members');
}

export function updateProfile(payload) {
  return axios.patch('/api/v1/members', payload);
}
