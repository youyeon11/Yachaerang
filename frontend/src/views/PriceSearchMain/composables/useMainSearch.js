import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import apiClient from '@/api/axios';

export function fetchLowPriceRank() {
  return apiClient.get('/api/v1/daily-prices/rank/low-prices');
}
export function fetchHighPriceRank() {
  return apiClient.get('/api/v1/daily-prices/rank/high-prices');
}

export function useMainSearch() {
  const router = useRouter();

  const activeTab = ref('top'); // top = 높은가격, bottom = 낮은가격

  // 🔥 낮은 가격 / 높은 가격 API 결과 저장할 ref
  const topItems = ref([]); // 높은 가격 순
  const bottomItems = ref([]); // 낮은 가격 순

  // 내가 즐겨찾기한 감시 리스트
  const watchList = ref([
    '양파(10kg(그물망 프)) - 중품',
    '당근(1kg) - 상품',
    '배추(3kg) - 중품',
    '파(1kg) - 고급',
    '감자(20kg) - 대',
  ]);

  // top / bottom 탭 전환 시 자동으로 노출되는 카드 리스트
  const popularItems = computed(() => (activeTab.value === 'top' ? topItems.value : bottomItems.value));

  async function loadRanks() {
    const { data: highData } = await fetchHighPriceRank();
    const { data: lowData } = await fetchLowPriceRank();

    topItems.value = highData.data;
    bottomItems.value = lowData.data;
  }

  onMounted(loadRanks);

  function goToDetail() {
    router.push('/price');
  }

  function editWatchList() {
    router.push('/mypage/items');
  }

  return {
    activeTab,
    popularItems,
    watchList,
    goToDetail,
    editWatchList,
  };
}
