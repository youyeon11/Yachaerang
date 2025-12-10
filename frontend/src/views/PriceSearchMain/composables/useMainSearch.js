import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';

export function useMainSearch() {
  const router = useRouter();

  const activeTab = ref('top');

  const topItems = ref([
    { id: 1, name: '당근', price: 3013 },
    { id: 2, name: '배추', price: 2980 },
    { id: 3, name: '무', price: 2700 },
    { id: 4, name: '파프리카', price: 6500 },
    { id: 5, name: '양송이', price: 5200 },
    { id: 6, name: '오이', price: 4100 },
    { id: 7, name: '상추', price: 3600 },
    { id: 8, name: '대파', price: 3400 },
  ]);

  const bottomItems = ref([
    { id: 1, name: '감자', price: 500 },
    { id: 2, name: '무청', price: 600 },
    { id: 3, name: '깻잎', price: 700 },
    { id: 4, name: '고추잎', price: 800 },
    { id: 5, name: '미나리', price: 900 },
    { id: 6, name: '부추', price: 1000 },
    { id: 7, name: '열무', price: 1100 },
    { id: 8, name: '시금치', price: 1200 },
  ]);

  const popularItems = computed(() => (activeTab.value === 'top' ? topItems.value : bottomItems.value));

  function goToDetail() {
    router.push('/price');
  }

  function editWatchList() {}

  return {
    activeTab,
    popularItems,
    goToDetail,
    editWatchList,
  };
}
