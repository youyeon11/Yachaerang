import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: () => import('@/views/MainPage.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/SignupView.vue'),
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: () => import('@/views/MyPageView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/Dashboard.vue'),
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/views/SerachDashboard.vue'),
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('@/views/Article.vue'),
    },
    {
      path: '/articles/:id',
      name: 'article-detail',
      component: () => import('@/views/ArticleDetail.vue'),
    },
    {
      path: '/ai-chat',
      name: 'ai-chat',
      component: () => import('@/views/AIChat.vue'),
    },
  ],
});

// 인증이 필요한 라우트 목록
const authRequiredRoutes = ['mypage'];

// 인증이 필요한 라우트(마이페이지) 로그인 상태 확인
router.beforeEach((to, from, next) => {
  const isAuthRequired = to.meta.requiresAuth || authRequiredRoutes.includes(to.name);
  const accessToken = localStorage.getItem('accessToken');

  // 토큰 유효성 검사
  const hasValidToken =
    accessToken && accessToken.trim() !== '' && accessToken !== 'null' && accessToken !== 'undefined';

  // 인증이 필요한 라우트이고 로그인하지 않은 경우
  if (isAuthRequired && !hasValidToken) {
    next({ name: 'login' });
    return;
  }

  // 로그인 상태-> 로그인/회원가입 : 홈
  if ((to.name === 'login' || to.name === 'signup') && hasValidToken) {
    next({ name: 'main' });
    return;
  }

  next();
});

export default router;
