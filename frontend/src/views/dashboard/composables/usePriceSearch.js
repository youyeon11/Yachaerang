import { ref, computed, onMounted, watch } from 'vue';
import {
  fetchItemsApi,
  fetchSubItemsApi,
  fetchDailyPricesApi,
  fetchWeeklyPricesApi,
  fetchMonthlyPricesApi,
  fetchYearlyPricesApi,
  fetchYearlyPriceDetailApi,
} from '@/api/price';
import { addFavorite } from '@/api/favorite';
import { useToastStore } from '@/stores/toast';

export function usePriceSearch() {
  const selectedItem = ref('');
  const selectedVariety = ref('');
  const itemOptions = ref([]);
  const varietyOptions = ref([]);
  const priceResult = ref([]);
  const hasSearched = ref(false);
  const suppressVarietyReset = ref(false);

  const periodType = ref('year');
  const periodTabs = [
    { value: 'year', label: '연간' },
    { value: 'month', label: '월간' },
    { value: 'week', label: '주간' },
    { value: 'day', label: '일간' },
  ];

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);

  const maxYear = yesterday.getFullYear();
  const minYear = 2000;

  const yearOptions = computed(() => {
    const years = [];
    for (let y = maxYear; y >= minYear; y -= 1) {
      years.push(String(y));
    }
    return years;
  });

  /* ================= 일별 ================= */

  const dayStartDate = ref(null);
  const dayEndDate = ref(null);

  /* ================= 주별 ================= */

  const weekStartDate = ref(null);
  const weekEndDate = ref(null);

  // "완전히 끝난 주"까지만 선택 가능하게 하는 기준 일요일
  const weekday = today.getDay();
  const daysSinceMonday = (weekday + 6) % 7;
  const lastWeekSunday = new Date(today);
  lastWeekSunday.setDate(today.getDate() - daysSinceMonday - 1);

  /* ================= 월별 ================= */

  const monthStartDate = ref(null);
  const monthEndDate = ref(null);

  /* ================= 연도별 ================= */

  const isYearDetail = ref(false);
  const yearStart = ref('');
  const yearEnd = ref('');
  const yearDetail = ref('');

  /* ================= 공통 포맷터 / 변환 ================= */

  // 날짜 → 'YYYY-MM-DD'
  const formatDateToString = (date) => {
    if (!date) return '';
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // Date 기준 주(월~일) → 문자열 범위
  const getWeekRangeFromDate = (date) => {
    if (!date) return null;
    const d = new Date(date);
    const dayOfWeek = d.getDay();
    const diff = d.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
    const monday = new Date(d.setDate(diff));
    const sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);
    return {
      start: formatDateToString(monday),
      end: formatDateToString(sunday),
    };
  };

  /* ================= API: 품목 / 품종 ================= */

  const fetchItems = async () => {
    try {
      const res = await fetchItemsApi();
      const body = res.data;
      const list = Array.isArray(body) ? body : Array.isArray(body?.data) ? body.data : [];
      itemOptions.value = list.map((item) => ({
        value: String(item.itemCode ?? item.code ?? item.id),
        label: item.itemName ?? item.name ?? '',
      }));
    } catch (error) {
      console.error('품목 목록 조회 실패:', error);
      itemOptions.value = [];
    }
  };

  const fetchSubItems = async (itemCode) => {
    if (!itemCode) {
      varietyOptions.value = [];
      if (!suppressVarietyReset.value) {
        selectedVariety.value = '';
      }
      return;
    }
    try {
      const res = await fetchSubItemsApi(itemCode);
      const body = res.data;
      const list = Array.isArray(body) ? body : Array.isArray(body?.data) ? body.data : [];
      varietyOptions.value = list.map((sub) => ({
        value: String(sub.productCode ?? sub.code ?? sub.id),
        label: sub.subItemName ?? sub.name ?? sub.productName ?? '',
      }));
      if (!suppressVarietyReset.value) {
        selectedVariety.value = '';
      }
    } catch (error) {
      console.error('하위품목 목록 조회 실패:', error);
      varietyOptions.value = [];
      if (!suppressVarietyReset.value) {
        selectedVariety.value = '';
      }
    }
  };

  watch(selectedItem, (newItem) => {
    if (suppressVarietyReset.value) return;
    hasSearched.value = false;
    fetchSubItems(newItem);
  });

  watch(selectedVariety, () => {
    if (suppressVarietyReset.value) return;
    hasSearched.value = false;
  });

  onMounted(() => {
    fetchItems();
  });

  /* ================= 필터 / 초기화 ================= */

  const handlePeriodClick = (type) => {
    periodType.value = type;

    // 기간 변경 시 날짜 관련 상태 모두 초기화
    dayStartDate.value = null;
    dayEndDate.value = null;
    weekStartDate.value = null;
    weekEndDate.value = null;
    monthStartDate.value = null;
    monthEndDate.value = null;
    yearStart.value = '';
    yearEnd.value = '';
    yearDetail.value = '';
    isYearDetail.value = false;
    priceResult.value = [];
    hasSearched.value = false;
  };

  /* ================= 즐겨찾기 등록 ================= */

  const periodTypeMap = {
    day: 'DAILY',
    week: 'WEEKLY',
    month: 'MONTHLY',
    year: 'YEARLY',
  };

  const mapPeriodTypeToFavorite = (type) => periodTypeMap[type] ?? null;

  const handleAddFavorite = async () => {
    const accessToken = localStorage.getItem('accessToken');
    const hasValidToken =
      accessToken && accessToken.trim() !== '' && accessToken !== 'null' && accessToken !== 'undefined';

    const toastStore = useToastStore();

    if (!hasValidToken) {
      toastStore.show('관심 품목 등록은 로그인 후 이용할 수 있어요', 'info');
      return;
    }
    const productCode = selectedVariety.value;
    const mappedPeriod = mapPeriodTypeToFavorite(periodType.value);

    if (!productCode) {
      toastStore.show('올바른 품종을 선택해 주세요', 'info');
      return;
    }

    if (!mappedPeriod) {
      toastStore.show('등록할 기간 유형이 올바르지 않습니다', 'error');
      return;
    }

    try {
      await addFavorite({
        productCode,
        periodType: mappedPeriod,
      });
      toastStore.show('관심 품목으로 등록되었습니다', 'success');
    } catch (error) {
      console.error('즐겨찾기 등록 실패:', error);
      if (error.response?.status === 401) {
        toastStore.show('로그인이 만료되었습니다. 다시 로그인해주세요', 'info');
        return;
      }
      toastStore.show('관심 품목 등록 중 오류가 발생했습니다', 'error');
    }
  };

  /* ================= 응답 파싱 ================= */

  const extractPriceList = (raw) => {
    if (!raw) return [];
    if (Array.isArray(raw)) return raw;
    if (raw && typeof raw === 'object' && Array.isArray(raw.data)) {
      return raw.data;
    }
    return [];
  };

  /* ================= 일자 변환 함수 ================= */
  const normalizeResult = (rawList, type) => {
    return rawList.map((item) => {
      if (type === 'day') {
        return {
          dateLabel: item.priceDate || item.date || '',
          priceLabel: item.price ?? item.avgPrice ?? null,
        };
      }

      if (type === 'week') {
        return {
          dateLabel: `${item.startDate} ~ ${item.endDate}`,
          priceLabel: item.avgPrice ?? null,
        };
      }

      if (type === 'month') {
        return {
          dateLabel: `${item.priceYear}-${String(item.priceMonth).padStart(2, '0')}`,
          priceLabel: item.avgPrice ?? null,
        };
      }

      // year(연별)
      return {
        dateLabel: `${item.priceYear}`,
        priceLabel: item.avgPrice ?? null,
      };
    });
  };

  /* ================= 검색 ================= */

  const handleSearch = async () => {
    const productCode = selectedVariety.value;
    const toastStore = useToastStore();

    if (!productCode) {
      toastStore.show('품종을 선택해 주세요', 'info');
      return;
    }

    try {
      if (periodType.value === 'day') {
        // 일별
        if (!dayStartDate.value || !dayEndDate.value) {
          toastStore.show('시작일과 종료일을 모두 선택해 주세요', 'info');
          return;
        }

        const startStr = formatDateToString(dayStartDate.value);
        const endStr = formatDateToString(dayEndDate.value);

        if (startStr > endStr) {
          toastStore.show('시작일이 종료일보다 늦을 수 없습니다', 'info');
          return;
        }

        const { data } = await fetchDailyPricesApi(productCode, {
          startDate: startStr,
          endDate: endStr,
        });

        priceResult.value = normalizeResult(extractPriceList(data), 'day');
      } else if (periodType.value === 'week') {
        // 주간
        if (!weekStartDate.value || !weekEndDate.value) {
          toastStore.show('시작 주와 종료 주를 모두 선택해 주세요', 'info');
          return;
        }

        const startRange = getWeekRangeFromDate(weekStartDate.value);
        const endRange = getWeekRangeFromDate(weekEndDate.value);

        if (startRange.start > endRange.start) {
          toastStore.show('시작 주가 종료 주보다 늦을 수 없습니다', 'info');
          return;
        }

        const { data } = await fetchWeeklyPricesApi(productCode, {
          startDate: startRange.start,
          endDate: endRange.end,
        });

        priceResult.value = normalizeResult(extractPriceList(data), 'week');
      } else if (periodType.value === 'month') {
        // 월간
        if (!monthStartDate.value || !monthEndDate.value) {
          toastStore.show('시작 월과 종료 월을 모두 선택해 주세요', 'info');
          return;
        }

        const startD = new Date(monthStartDate.value);
        const endD = new Date(monthEndDate.value);

        if (startD > endD) {
          toastStore.show('시작 월이 종료 월보다 늦을 수 없습니다', 'info');
          return;
        }

        const { data } = await fetchMonthlyPricesApi(productCode, {
          startYear: startD.getFullYear(),
          startMonth: startD.getMonth() + 1,
          endYear: endD.getFullYear(),
          endMonth: endD.getMonth() + 1,
        });

        priceResult.value = normalizeResult(extractPriceList(data), 'month');
      } else if (periodType.value === 'year') {
        // 연간
        if (isYearDetail.value) {
          if (!yearDetail.value) {
            toastStore.show('조회할 연도를 선택해 주세요', 'info');
            return;
          }

          const y = Number(yearDetail.value);
          if (Number.isNaN(y) || y < minYear || y > maxYear) {
            toastStore.show(`${minYear}년부터 ${maxYear}년 사이만 선택할 수 있습니다`, 'info');
            return;
          }

          const { data } = await fetchYearlyPriceDetailApi(productCode, { year: y });
          priceResult.value = extractPriceList(data);
        } else {
          if (!yearStart.value || !yearEnd.value) {
            toastStore.show('시작 연도와 종료 연도를 모두 선택해 주세요', 'info');
            return;
          }

          const ys = Number(yearStart.value);
          const ye = Number(yearEnd.value);

          if (ys > ye) {
            toastStore.show('시작 연도가 종료 연도보다 클 수 없습니다', 'info');
            return;
          }

          if (ye > maxYear) {
            toastStore.show(`${maxYear}년 이후는 선택할 수 없습니다`, 'info');
            return;
          }

          const { data } = await fetchYearlyPricesApi(productCode, {
            startYear: ys,
            endYear: ye,
          });

          priceResult.value = normalizeResult(extractPriceList(data), 'year');
        }
      } else {
        toastStore.show('잘못된 기간 유형입니다', 'error');
        return;
      }

      hasSearched.value = true;
    } catch (error) {
      console.error('가격 조회 실패', error);

      if (error.response?.status === 401) {
        toastStore.show('로그인이 필요합니다', 'info');
        return;
      }

      toastStore.show('가격 정보를 가져오는 중 오류가 발생했습니다', 'error');
    }
  };

  const resetFilters = () => {
    selectedItem.value = '';
    selectedVariety.value = '';
    periodType.value = 'year';
    hasSearched.value = false;

    dayStartDate.value = null;
    dayEndDate.value = null;
    weekStartDate.value = null;
    weekEndDate.value = null;
    monthStartDate.value = null;
    monthEndDate.value = null;
    yearStart.value = '';
    yearEnd.value = '';
    yearDetail.value = '';
    isYearDetail.value = false;
    varietyOptions.value = [];
    priceResult.value = [];
    hasSearched.value = false;
  };

  return {
    selectedItem,
    selectedVariety,
    itemOptions,
    varietyOptions,
    priceResult,
    periodType,
    periodTabs,
    yesterday,
    lastWeekSunday,
    yearOptions,
    dayStartDate,
    dayEndDate,
    weekStartDate,
    weekEndDate,
    monthStartDate,
    monthEndDate,
    isYearDetail,
    yearStart,
    yearEnd,
    yearDetail,
    hasSearched,

    handlePeriodClick,
    resetFilters,
    handleSearch,
    handleAddFavorite,
  };
}
