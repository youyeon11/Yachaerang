import './assets/main.css';

import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import VCalendar from 'v-calendar';
import 'v-calendar/style.css';

// 앱 시작 전 localStorage 정리 (잘못된 토큰 제거)
const accessToken = localStorage.getItem('accessToken');
if (accessToken) {
  const trimmedToken = accessToken.trim();
  // 빈 문자열, 'null', 'undefined' 같은 잘못된 값이면 삭제
  if (trimmedToken === '' || trimmedToken === 'null' || trimmedToken === 'undefined') {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }
}

const app = createApp(App);
app.use(createPinia());
app.use(router);

app.use(VCalendar, {});

app.mount('#app');
