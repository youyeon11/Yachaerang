import axios from 'axios';
import { setupInterceptors } from './interceptors';

// Base configuration
const config = {
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
};


// 메인 API 요청
export const apiClient = axios.create(config);

// 리프레시 토큰 요청
export const refreshClient = axios.create(config);

// Initialize
setupInterceptors();