<template>
  <div class="app-root">
    <!-- 왼쪽 세로 NavBar -->
    <NavBar class="nav-bar" />
    <!-- NavBar 오른쪽 전체 영역 -->
    <div class="right-area">
      <main class="main-area">
        <RouterView />
      </main>
      <!-- 아래쪽에 전체 폭 footer -->
      <Footer />
    </div>
  </div>
</template>

<script setup>
import Footer from '@/components/layout/Footer.vue';
import NavBar from '@/components/layout/NavBar.vue';
</script>

<style>
.app-root {
  min-height: 100vh;
  display: flex;
}

/* NavBar는 position: fixed이므로 flex에서 제외됨 */
.nav-bar {
  /* fixed navbar는 flex에 참여하지 않음 */
}

/* NavBar 오른쪽 전체 영역 - margin-left로 navbar 공간 확보 */
.right-area {
  flex: 1;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  margin-left: 80px; /* NavBar width (80px)와 동일하게 */
}

/* 가운데 내용이 남는 높이 전부 차지 */
.main-area {
  flex: 1;
}
</style>
