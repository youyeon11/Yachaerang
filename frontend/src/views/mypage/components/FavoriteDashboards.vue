<template>
  <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
    <h2 class="mb-4 flex items-center gap-2 text-lg font-semibold text-gray-900">
      <IconHeart class="h-5 w-5 fill-[#F44323] text-[#F44323]" />
      좋아요한 대시보드
    </h2>
    
    <div v-if="loading" class="p-2 text-sm text-gray-400">
      불러오는 중...
    </div>
    
    <div v-else class="space-y-2">
      <div
        v-for="item in favorites"
        :key="item.favoriteId"
        class="cursor-pointer rounded-lg border border-gray-200 p-3 text-sm transition-colors hover:bg-gray-50"
        @click="goToDetail(item)"
      >
        <div class="font-medium">{{ item.displayName }}</div>
        <div class="text-xs text-gray-500">{{ item.periodLabel }}</div>
      </div>
      <div v-if="favorites.length === 0" class="p-2 text-sm text-gray-400">
        좋아요한 대시보드가 없습니다.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchFavorites } from '@/api/favorite';
import { useProductNameStore } from '@/stores/productNameStore';
import IconHeart from '@/components/icons/IconHeart.vue';

const router = useRouter();
const productNameStore = useProductNameStore();

const favorites = ref([]);
const loading = ref(false);

const mapPeriodLabel = (periodType) => {
  const labels = {
    DAILY: '일간',
    WEEKLY: '주간',
    MONTHLY: '월간',
    YEARLY: '연간',
  };
  return labels[periodType] || periodType || '';
};

const loadFavorites = async () => {
  loading.value = true;
  try {
    const { response } = await fetchFavorites();
    const list = Array.isArray(response?.data) ? response.data : [];
    const nameMap = await productNameStore.loadProductNames(list);

    favorites.value = list.map((fav) => {
      const fromMap = nameMap[fav.productCode] || {};
      const itemName = fav.itemName || fav.item?.itemName || fromMap.itemName || '';
      const varietyName = fav.subItemName || fav.productName || fromMap.varietyName || '';

      let displayName = '';
      if (itemName && varietyName) {
        displayName = `${itemName} - ${varietyName}`;
      } else {
        displayName = varietyName || itemName || fav.productCode || '';
      }

      return {
        favoriteId: fav.favoriteId,
        productCode: fav.productCode,
        periodType: fav.periodType,
        periodLabel: mapPeriodLabel(fav.periodType),
        displayName,
      };
    });
  } catch (error) {
    console.error('대시보드 즐겨찾기 조회 실패:', error);
    favorites.value = [];
  } finally {
    loading.value = false;
  }
};

const goToDetail = (item) => {
  router.push({
    path: '/search',
    query: {
      productCode: item.productCode,
      periodType: item.periodType,
    },
  });
};

onMounted(loadFavorites);
</script>