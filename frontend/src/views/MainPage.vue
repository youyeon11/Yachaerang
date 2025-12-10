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
import SectionIntro from '@/components/main/SectionIntro.vue';
import SectionAI from '@/components/main/SectionAI.vue';
import SectionDashboard from '@/components/main/SectionDashboard.vue';
import SectionNews from '@/components/main/SectionNews.vue';
import SectionMyFarm from '@/components/main/SectionMyFarm.vue';
</script>

<style scoped>
.main-wrapper {
  width: 100%;
  overflow-x: hidden;
}
</style>
