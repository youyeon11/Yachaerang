import apiClient from './axios';

// 로그인
export function loginApi(email, password) {
  return apiClient.post('/api/v1/auth/login', { email, password });
}

// 회원가입
export function signupApi(payload) {
  return apiClient.post('/api/v1/auth/signup', payload);
}

// 이메일 중복 확인
export function checkEmailDuplicateApi(email) {
  return apiClient.get('/api/v1/auth/check-email', {
    params: { email },
  });
}






