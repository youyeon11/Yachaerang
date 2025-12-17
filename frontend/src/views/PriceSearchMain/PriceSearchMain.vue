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
import { computed } from 'vue';
import SearchTabs from '@/views/PriceSearchMain/components/SearchTab.vue';
import PopularItemGrid from '@/views/PriceSearchMain/components/PopularItemGrid.vue';
import WatchListBox from '@/views/PriceSearchMain/components/WatchListBox.vue';
import MoveToDetailButton from '@/views/PriceSearchMain/components/MoveToDetailButton.vue';

import { useMainSearch } from '@/views/PriceSearchMain/composables/useMainSearch';

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
