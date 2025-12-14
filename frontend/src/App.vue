<template>
  <div class="app-root">
    <NavBar class="nav-bar" />
    <div class="right-area">
      <main class="main-area" :class="{ 'no-scroll': isFullPageLayout }">
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

onMounted(() => {
  checkAuth();
});

const isFullPageLayout = computed(() => {
  return route.name === 'login' || route.name === 'signup' || route.name === 'ai-chat';
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
  height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
}
</style>
