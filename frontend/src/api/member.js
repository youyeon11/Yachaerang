import apiClient from '@/api/axios';

export function getMyProfile() {
  return apiClient.get('/api/v1/members');
}

export function updateProfile(payload) {
  return apiClient.patch('/api/v1/members', payload);
}

export function uploadProfileImage(file) {
  const formData = new FormData();
  formData.append('file', file);

  return apiClient.post('/api/v1/members/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}
