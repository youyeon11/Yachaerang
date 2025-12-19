<template>
  <main class="flex flex-1 flex-col gap-6 overflow-y-auto p-8 bg-gray-50">
    <!-- Header -->
    <div>
      <h1 class="text-3xl font-bold tracking-tight text-gray-900">시세 랭킹</h1>
      <p class="text-gray-600">어제 시세 기준 상위/하위 9개 품목</p>
    </div>

    <!-- Tabs -->
    <div class="flex gap-2 border-b border-gray-200">
      <SearchTabs :activeTab="activeTab" @change="activeTab = $event" />
    </div>

    <!-- Grid + WatchList (수평 배치) -->
    <div class="flex flex-1 gap-6">
      <!-- Left: Product Grid -->
      <div class="flex-1">
        <!-- Loading State -->
        <div v-if="isLoading" class="flex items-center justify-center py-12">
          <div class="text-gray-500">불러오는 중...</div>
        </div>

        <!-- Empty State -->
        <div v-else-if="popularItems.length === 0" class="flex items-center justify-center py-12">
          <p class="text-gray-500">데이터가 없습니다.</p>
        </div>

        <!-- Product Grid -->
        <div v-else class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          <ItemCard
            v-for="(product, idx) in popularItems"
            :key="product.productCode ?? idx"
            :item="formatRankItem(product)"
            @select="goRankDetail(product)"
          />
        </div>
      </div>

      <!-- Right: Watch List -->
      <WatchListBox 
        :items="watchList" 
        @select="goFavoriteDetail" 
        @edit="editWatchList" 
      />
    </div>
  </main>
</template>

<script setup>
import { computed } from 'vue'

import SearchTabs from '@/views/rank/components/SearchTab.vue'
import ItemCard from '@/views/rank/components/ItemCard.vue'
import WatchListBox from '@/views/rank/components/WatchListBox.vue'

import { rank } from '@/views/rank/composables/rank'

const {
  activeTab,
  popularItems,
  watchList,
  editWatchList,
  goFavoriteDetail,
  goRankDetail,
} = rank()

// 로딩 상태 (필요시 rank.js에서 추가 가능)
const isLoading = computed(() => popularItems.value === null)

// rank API 응답을 ItemCard 형식으로 변환
const formatRankItem = (item) => {
  return {
    itemName: item.itemName ?? item.productName ?? item.name ?? '품목명 없음',
    unit: item.unit ?? item.unitName ?? '',
    price: item.price ?? item.currentPrice ?? item.todayPrice ?? 0,
    change: item.changeRate ?? item.change ?? item.priceChangeRate ?? undefined,
  }
}
</script>