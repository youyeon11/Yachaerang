<template>
  <div class="main-search page-container">
    <SearchTabs :activeTab="activeTab" @change="activeTab = $event" />

    <h2 class="section-title">{{ sectionTitle }}</h2>

    <div class="layout">
      <PopularItemGrid :items="popularItems" />

      <WatchListBox :items="watchList" @edit="editWatchList" @click="$router.push('/mypage/items')" />
    </div>

    <MoveToDetailButton @click="$router.push('/search')" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import SearchTabs from './components/SearchTab.vue';
import PopularItemGrid from './components/PopularItemGrid.vue';
import WatchListBox from './components/WatchListBox.vue';
import MoveToDetailButton from './components/MoveToDetailButton.vue';

import { useMainSearch } from './composables/useMainSearch';

const { activeTab, popularItems, watchList, editWatchList } = useMainSearch();

const sectionTitle = computed(() => {
  return activeTab.value === 'top' ? '어제 시세 기준 TOP 8' : '어제 시세 기준 BOTTOM 8';
});
</script>

<style scoped>
.section-title {
  font-size: 22px;
  margin: 32px 0 20px;
}

.layout {
  display: flex;
  gap: 32px;
}
</style>
