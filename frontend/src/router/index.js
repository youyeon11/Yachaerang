import { createRouter, createWebHistory } from 'vue-router';
import { useToastStore } from '@/stores/toast';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: () => import('@/views/main/MainPage.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: () => import('@/views/auth/ForgotPasswordView.vue'),
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('@/views/auth/SignupView.vue'),
    },

    {
      path: '/searchMainpage',
      name: 'searchMainpage',
      component: () => import('@/views/PriceSearchMain/PriceSearchMain.vue'),
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/views/PriceSearchDetail/PriceSearchDetail.vue'),
    },
    {
      path: '/articles',
      name: 'articles',
      component: () => import('@/views/article/Article.vue'),
    },
    {
      path: '/articles/:id',
      name: 'article-detail',
      component: () => import('@/views/article/ArticleDetail.vue'),
    },
    {
      path: '/ai-chat',
      name: 'ai-chat',
      component: () => import('@/views/ai/AIChat.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/mypage',
      component: () => import('@/views/mypage/MyPageLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/mypage/farm' },

        { path: 'farm', name: 'MyFarm', component: () => import('@/views/mypage/MyFarmView.vue') },
        {
          path: 'profile',
          name: 'ProfilePasswordConfirm',
          component: () => import('@/views/mypage/ProfilePasswordConfirm.vue'),
        },
        { path: 'profile/edit', name: 'ProfileEdit', component: () => import('@/views/mypage/ProfileEdit.vue') },
        { path: 'password', name: 'PasswordChange', component: () => import('@/views/mypage/PasswordChange.vue') },
        { path: 'items', name: 'MyItems', component: () => import('@/views/mypage/MyItemsPlaceholder.vue') },
      ],
    },
  ],
});

router.beforeEach((to, from, next) => {
  const accessToken = localStorage.getItem('accessToken');
  const toastStore = useToastStore();

  const hasValidToken =
    accessToken && accessToken.trim() !== '' && accessToken !== 'null' && accessToken !== 'undefined';

  // 현재 라우트 체인(부모 포함) 중 하나라도 requiresAuth면 보호
  const isAuthRequired = to.matched.some((record) => record.meta.requiresAuth);

  // 1) 로그인 안 되어 있는데 보호 라우트 들어가면 -> 로그인으로
  if (isAuthRequired && !hasValidToken) {
    next({ name: 'login', query: { message: '로그인이 필요한 기능입니다' } });
    return;
  }

  // 2) 로그인 상태에서 로그인/회원가입 페이지 접근 -> 메인으로
  if ((to.name === 'login' || to.name === 'signup') && hasValidToken) {
    next({ name: 'main' });
    return;
  }

  next();
});

export default router;
