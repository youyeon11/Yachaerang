<template>
  <!-- 모든 페이지에 NavBar + Footer 레이아웃 -->
  <div class="app-root">
    <NavBar class="nav-bar" />
    <div class="right-area">
      <main class="main-area" :class="{ 'no-scroll': isAuthPage }">
        <RouterView />
      </main>
      <Footer />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useAuth } from '@/stores/auth';
import Footer from '@/components/layout/Footer.vue';
import NavBar from '@/components/layout/NavBar.vue';

const route = useRoute();
const { checkAuth } = useAuth();

// 앱 시작 시 로그인 상태 확인 (페이지 새로고침 후에도 상태 유지)
onMounted(() => {
  checkAuth();
});

// 로그인/회원가입 페이지인지 확인
const isAuthPage = computed(() => {
  return route.name === 'login' || route.name === 'signup';
});
</script>

<style>
.app-root {
  min-height: 100vh;
  display: flex;
}

.right-area {
  flex: 1;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  margin-left: 60px;
}

.main-area {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
}

.main-area.no-scroll {
  overflow: hidden;
  height: calc(100vh - 60px); /* Footer 높이 제외, NavBar는 고정이므로 제외 */
  display: flex;
  flex-direction: column;
}
</style>
