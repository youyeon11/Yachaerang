<template>
  <!-- 로그인 페이지는 전체 화면으로 표시 -->
  <RouterView v-if="isAuthPage" />

  <!-- 일반 페이지는 NavBar + Footer 레이아웃 -->
  <div v-else class="app-root">
    <NavBar class="nav-bar" />
    <div class="right-area">
      <main class="main-area">
        <RouterView />
      </main>
      <Footer />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import Footer from '@/components/layout/Footer.vue';
import NavBar from '@/components/layout/NavBar.vue';

const route = useRoute();
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
}
</style>
