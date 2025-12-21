import { apiClient } from './axios';

// 로그인
export function loginApi(email, password) {
  return apiClient.post('/api/v1/auth/login', { email, password });
}

// 회원가입
export function signupApi(payload) {
  return apiClient.post('/api/v1/auth/signup', payload);
}

// 이메일 인증
export const sendEmailCodeApi = (payload) => {
  return apiClient.post('/api/v1/mails', payload);
};

export const verifyEmailCodeApi = (payload) => {
  return apiClient.post('/api/v1/mails/verify-code', payload);
};

// 비밀번호 찾기 (인증 코드 발송)
export const sendPasswordResetCodeApi = (payload) => {
  return apiClient.post('/api/v1/mails/password/send-code', payload);
};