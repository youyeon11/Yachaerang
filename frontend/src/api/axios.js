import axios from 'axios';
import { setupInterceptors } from './interceptors';

const config = {
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
};

export const publicClient = axios.create(config);
export const apiClient = axios.create(config);
export const refreshClient = axios.create(config);

export default apiClient;

setupInterceptors({ apiClient, refreshClient });