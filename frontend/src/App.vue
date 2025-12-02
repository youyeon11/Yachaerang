<template>
  <div class="app-root">
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
import Footer from '@/components/layout/Footer.vue';
import NavBar from '@/components/layout/NavBar.vue';
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
