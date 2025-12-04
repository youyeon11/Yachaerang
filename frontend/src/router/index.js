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

// 네비게이션 가드: 인증이 필요한 라우트 접근 시 로그인 상태 확인
router.beforeEach((to, from, next) => {
  const isAuthRequired = to.meta.requiresAuth || authRequiredRoutes.includes(to.name);
  const accessToken = localStorage.getItem('accessToken');

  // 인증이 필요한 라우트이고 로그인하지 않은 경우
  if (isAuthRequired && !accessToken) {
    next({ name: 'login' });
    return;
  }

  // 로그인 상태에서 로그인/회원가입 페이지 접근 시 홈으로 리다이렉트
  if ((to.name === 'login' || to.name === 'signup') && accessToken) {
    next({ name: 'main' });
    return;
  }

  next();
});

export default router;
