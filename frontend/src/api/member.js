import axios from '@/api/axios';

export function getMyProfile() {
  return axios.get('/api/v1/members');
}

export function updateProfile(payload) {
  return axios.patch('/api/v1/members', payload);
}

export function uploadProfileImage(file) {
  const formData = new FormData();
  formData.append('file', file);

  return axios.post('/api/v1/members/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}
