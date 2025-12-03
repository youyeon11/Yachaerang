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
      path: '/mypage',
      name: 'mypage',
      component: () => import('@/views/MyPageView.vue'),
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
      path: '/ai-chat',
      name: 'ai-chat',
      component: () => import('@/views/AIChat.vue'),
    },
  ],
});

export default router;
