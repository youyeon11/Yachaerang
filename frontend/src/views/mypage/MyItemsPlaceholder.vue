<template>
  <div class="items-page">
    <h1 class="title">나의 품목 관리</h1>
    <p class="desc">세부 가격 검색에서 등록한 관심 품목을 한눈에 볼 수 있어요.</p>

    <div v-if="loading" class="state-text">관심 품목을 불러오는 중입니다...</div>
    <div v-else-if="favorites.length === 0" class="state-text">등록된 관심 품목이 없습니다.</div>

    <div v-else class="cards">
      <div v-for="fav in favorites" :key="fav.favoriteId" class="card">
        <div class="card-main" @click="goToDetail(fav)">
          <div class="name">{{ fav.displayName }}</div>
          <div class="meta">
            <span class="badge">{{ fav.periodLabel }}</span>
          </div>
        </div>
        <button class="remove-btn" @click.stop="handleRemove(fav.favoriteId)">삭제</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchFavorites, removeFavorite } from '@/api/favorite';
import { useProductNameStore } from '@/stores/productNameStore';

const router = useRouter();
const productNameStore = useProductNameStore();

const favorites = ref([]);
const loading = ref(false);

const mapPeriodLabel = (periodType) => {
  switch (periodType) {
    case 'DAILY':
      return '일간';
    case 'WEEKLY':
      return '주간';
    case 'MONTHLY':
      return '월간';
    case 'YEARLY':
      return '연간';
    default:
      return periodType || '';
  }
};

const loadFavorites = async () => {
  loading.value = true;
  try {
    const { data } = await fetchFavorites();
    const list = Array.isArray(data?.data) ? data.data : [];

    const nameMap = await productNameStore.loadProductNames(list);

    favorites.value = list.map((fav) => {
      const fromMap = nameMap[fav.productCode] || {};

      const itemName = fav.itemName || fav.item?.itemName || fav.item?.name || fromMap.itemName || '';

      const varietyName =
        fav.subItemName || fav.productName || fav.varietyName || fav.product?.productName || fromMap.varietyName || '';

      const codeFallback = fav.productCode ?? '';

      let displayName = '';
      if (itemName && varietyName) {
        displayName = `${itemName} - ${varietyName}`;
      } else if (varietyName) {
        displayName = varietyName;
      } else if (itemName) {
        displayName = itemName;
      } else {
        displayName = codeFallback;
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
    console.error('마이페이지 관심 품목 조회 실패:', error);
    favorites.value = [];
  } finally {
    loading.value = false;
  }
};

const goToDetail = (fav) => {
  router.push({
    path: '/search',
    query: {
      productCode: fav.productCode,
      periodType: fav.periodType,
    },
  });
};

const handleRemove = async (favoriteId) => {
  if (!window.confirm('이 관심 품목을 삭제하시겠습니까?')) return;

  try {
    await removeFavorite(favoriteId);
    favorites.value = favorites.value.filter((f) => f.favoriteId !== favoriteId);
  } catch (error) {
    console.error('관심 품목 삭제 실패:', error);
    alert('관심 품목 삭제 중 오류가 발생했습니다.');
  }
};

onMounted(loadFavorites);
</script>

<style scoped>
.items-page {
  padding-top: 8px;
}

.title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 4px;
}

.desc {
  margin: 0 0 16px;
  font-size: 14px;
  color: #777;
}

.state-text {
  margin-top: 16px;
  font-size: 14px;
  color: #777;
}

.cards {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 10px;
  background: #fafafa;
  border: 1px solid #ebebeb;
}

.card-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  cursor: pointer;
}

.name {
  padding: 2px 8px;
  border-radius: 999px;
  background-color: #f3f4ff;
  color: #1f2937;
  font-size: 13px;
  font-weight: 600;
}

.meta {
  display: flex;
  gap: 6px;
  align-items: center;
  margin-top: 4px;
}

.badge {
  padding: 2px 8px;
  border-radius: 999px;
  background-color: #eef2ff;
  color: #4338ca;
  font-size: 11px;
}
</style>
