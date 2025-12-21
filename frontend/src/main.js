import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router';
import VCalendar from 'v-calendar'
import 'v-calendar/style.css'
import './assets/main.css'
import { setupInterceptors, setLogoutHandler } from '@/api/interceptors';
import { useAuthStore } from '@/stores/auth';


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

// App 생성 및 플러그인 등록
const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VCalendar, {})

// 인터셉터 설정
setupInterceptors();

// 스토어의 logout을 인터셉터에 주입
const authStore = useAuthStore();
setLogoutHandler(() => authStore.logout());


// 마운트
app.mount('#app')