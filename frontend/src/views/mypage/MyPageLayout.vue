<template>
  <div class="mypage-layout">
    <section class="mypage-main">
      <router-view />
    </section>
    <MyPageSidebar />
  </div>
</template>

<script setup>
import MyPageSidebar from '@/components/mypage/MyPageSidebar.vue';
</script>

<style scoped>
.mypage-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 260px;
  max-width: 1100px;
  margin: 0 auto;
  padding: 40px 24px;
  column-gap: 48px;
  box-sizing: border-box;
}

.mypage-main {
  display: flex;
  flex-direction: column;
}
</style>
