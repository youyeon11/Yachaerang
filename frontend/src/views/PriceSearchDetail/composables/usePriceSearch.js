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

export function usePriceSearch() {
  const selectedItem = ref('');
  const selectedVariety = ref('');
  const itemOptions = ref([]);
  const varietyOptions = ref([]);
  const priceResult = ref([]);

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
      console.log('품목 응답:', res.data);
      const body = res.data;
      const list = Array.isArray(body) ? body : Array.isArray(body?.data) ? body.data : [];
      itemOptions.value = list.map((item) => ({
        value: item.itemCode ?? item.code ?? item.id,
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
      selectedVariety.value = '';
      return;
    }
    try {
      const res = await fetchSubItemsApi(itemCode);
      console.log('하위품목 응답:', res.data);
      const body = res.data;
      const list = Array.isArray(body) ? body : Array.isArray(body?.data) ? body.data : [];
      varietyOptions.value = list.map((sub) => ({
        value: sub.productCode ?? sub.code ?? sub.id,
        label: sub.subItemName ?? sub.name ?? sub.productName ?? '',
      }));
      selectedVariety.value = '';
    } catch (error) {
      console.error('하위품목 목록 조회 실패:', error);
      varietyOptions.value = [];
      selectedVariety.value = '';
    }
  };

  watch(selectedItem, (newItem) => {
    fetchSubItems(newItem);
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
  };

  const resetFilters = () => {
    selectedItem.value = '';
    selectedVariety.value = '';
    periodType.value = 'year';

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

  /* ================= 검색 ================= */

  const handleSearch = async () => {
    const productCode = selectedVariety.value;
    console.log('handleSearch 호출됨', {
      periodType: periodType.value,
      productCode,
    });

    if (!productCode) {
      console.warn('productCode 없음 (품종 미선택)');
      alert('품종을 먼저 선택해 주세요.');
      return;
    }

    try {
      if (periodType.value === 'day') {
        // 일별
        if (!dayStartDate.value || !dayEndDate.value) {
          alert('일별 조회: 시작일과 종료일을 모두 선택해 주세요.');
          return;
        }
        const startStr = formatDateToString(dayStartDate.value);
        const endStr = formatDateToString(dayEndDate.value);
        if (startStr > endStr) {
          alert('일별 조회: 시작일이 종료일보다 늦을 수 없습니다.');
          return;
        }
        const params = {
          startDate: startStr,
          endDate: endStr,
        };
        console.log('최종 요청 (일별)', { productCode, params });
        const { data } = await fetchDailyPricesApi(productCode, params);
        console.log('raw 응답 data', data);
        const list = extractPriceList(data);
        priceResult.value = list;
        console.log('조회 결과 리스트', list);
      } else if (periodType.value === 'week') {
        // 주간
        if (!weekStartDate.value || !weekEndDate.value) {
          alert('주간 조회: 시작 주와 종료 주를 모두 선택해 주세요.');
          return;
        }
        const startRange = getWeekRangeFromDate(weekStartDate.value);
        const endRange = getWeekRangeFromDate(weekEndDate.value);
        if (startRange.start > endRange.start) {
          alert('주간 조회: 시작 주가 종료 주보다 늦을 수 없습니다.');
          return;
        }
        const params = {
          startDate: startRange.start,
          endDate: endRange.end,
        };
        console.log('최종 요청 (주간)', { productCode, params });
        const { data } = await fetchWeeklyPricesApi(productCode, params);
        console.log('raw 응답 data', data);
        const list = extractPriceList(data);
        priceResult.value = list;
        console.log('조회 결과 리스트', list);
      } else if (periodType.value === 'month') {
        // 월간
        if (!monthStartDate.value || !monthEndDate.value) {
          alert('월간 조회: 시작 월과 종료 월을 모두 선택해 주세요.');
          return;
        }
        const startD = new Date(monthStartDate.value);
        const endD = new Date(monthEndDate.value);
        if (startD > endD) {
          alert('월간 조회: 시작 월이 종료 월보다 늦을 수 없습니다.');
          return;
        }
        const params = {
          startYear: startD.getFullYear(),
          startMonth: startD.getMonth() + 1,
          endYear: endD.getFullYear(),
          endMonth: endD.getMonth() + 1,
        };
        console.log('최종 요청 (월간)', { productCode, params });
        const { data } = await fetchMonthlyPricesApi(productCode, params);
        console.log('raw 응답 data', data);
        const list = extractPriceList(data);
        priceResult.value = list;
        console.log('조회 결과 리스트', list);
      } else if (periodType.value === 'year') {
        // 연간
        if (isYearDetail.value) {
          // 특정 연도 하나만
          if (!yearDetail.value) {
            alert('특정 연도 조회: 연도를 선택해 주세요.');
            return;
          }
          const y = Number(yearDetail.value);
          if (Number.isNaN(y) || y < minYear || y > maxYear) {
            alert(`특정 연도 조회: ${minYear} ~ ${maxYear} 사이의 연도만 가능합니다.`);
            return;
          }
          const params = { year: y };
          console.log('최종 요청 (연간-상세)', { productCode, params });
          const { data } = await fetchYearlyPriceDetailApi(productCode, params);
          console.log('raw 응답 data', data);
          const list = extractPriceList(data);
          priceResult.value = list;
          console.log('조회 결과 리스트', list);
        } else {
          // 연도 범위
          if (!yearStart.value || !yearEnd.value) {
            alert('연간 조회: 시작 연도와 종료 연도를 모두 선택해 주세요.');
            return;
          }
          const ys = Number(yearStart.value);
          const ye = Number(yearEnd.value);
          if (ys > ye) {
            alert('연간 조회: 시작 연도가 종료 연도보다 클 수 없습니다.');
            return;
          }
          if (ye > maxYear) {
            alert(`연간 조회: ${maxYear}년 이후는 선택할 수 없습니다.`);
            return;
          }
          const params = {
            startYear: ys,
            endYear: ye,
          };
          console.log('최종 요청 (연간-범위)', { productCode, params });
          const { data } = await fetchYearlyPricesApi(productCode, params);
          console.log('raw 응답 data', data);
          const list = extractPriceList(data);
          priceResult.value = list;
          console.log('조회 결과 리스트', list);
        }
      } else {
        alert('잘못된 기간 유형입니다.');
      }
    } catch (error) {
      console.error('가격 조회 실패', error);
      if (error.response) {
        console.error('응답 상태코드:', error.response.status);
        console.error('응답 바디:', error.response.data);
      }
      alert('가격 정보를 가져오는 중 오류가 발생했습니다.');
    }
  };

  return {
    // 상태
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

    // 메서드
    handlePeriodClick,
    resetFilters,
    handleSearch,
  };
}
