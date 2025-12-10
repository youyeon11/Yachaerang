<template>
  <div class="main-wrapper">
    <SectionIntro />
    <SectionAI />
    <SectionDashboard />
    <SectionNews />
    <SectionMyFarm />
  </div>
</template>

<script setup>
import SectionIntro from '@/views/main/components/SectionIntro.vue';
import SectionAI from '@/views/main/components/SectionAI.vue';
import SectionDashboard from '@/views/main/components/SectionDashboard.vue';
import SectionNews from '@/views/main/components/SectionNews.vue';
import SectionMyFarm from '@/views/main/components/SectionMyFarm.vue';
</script>

<style scoped>
.main-wrapper {
  width: 100%;
  overflow-x: hidden;
}
</style>
