<template>
  <div class="price-search-page">
    <header class="page-header">
      <h1 class="page-title">세부 가격 검색</h1>
      <p class="page-subtitle">내가 원하는대로 검색하고 정렬할 수 있는 야채랑 농, 수산물 가격정보</p>
    </header>

    <section class="search-card">
      <!-- 1행: 품목 / 품종 -->
      <div class="row row-top">
        <div class="field">
          <span class="field-label">품목</span>
          <select v-model="selectedItem" class="select">
            <option value="">선택</option>
            <option v-for="item in itemOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>
        </div>

        <div class="field">
          <span class="field-label">품종</span>
          <!-- ✅ 품종 = productCode 선택 -->
          <select v-model="selectedVariety" class="select" :disabled="!selectedItem">
            <option value="">선택</option>
            <option v-for="variety in varietyOptions" :key="variety.value" :value="variety.value">
              {{ variety.label }}
            </option>
          </select>
        </div>
      </div>

      <div class="divider"></div>

      <!-- 2행: 기간 + 날짜 + 버튼 -->
      <div class="row row-bottom">
        <!-- 기간 탭 -->
        <div class="field period-field">
          <span class="field-label">기간</span>
          <div class="period-tabs">
            <button
              v-for="tab in periodTabs"
              :key="tab.value"
              type="button"
              class="period-tab"
              :class="{ active: periodType === tab.value }"
              @click="handlePeriodClick(tab.value)"
            >
              {{ tab.label }}
            </button>
          </div>
        </div>

        <!-- 날짜 + 버튼 -->
        <div class="row row-bottom-right">
          <!-- 날짜 선택 -->
          <div class="date-range">
            <!-- 일별 -->
            <template v-if="periodType === 'day'">
              <div class="date-input">
                <span class="date-icon">📅</span>
                <input v-model="dayStart" type="date" class="date-field" :max="maxDate" />
              </div>
              <span class="date-separator">~</span>
              <div class="date-input">
                <span class="date-icon">📅</span>
                <input v-model="dayEnd" type="date" class="date-field" :max="maxDate" />
              </div>
            </template>

            <!-- 주별 -->
            <template v-else-if="periodType === 'week'">
              <div class="date-input week-input" :class="{ 'week-selected': weekStart }">
                <span class="date-icon">📅</span>
                <input v-model="weekStart" type="week" class="date-field" :max="maxWeek" />
              </div>
              <span class="date-separator">~</span>
              <div class="date-input week-input" :class="{ 'week-selected': weekEnd }">
                <span class="date-icon">📅</span>
                <input v-model="weekEnd" type="week" class="date-field" :max="maxWeek" />
              </div>
            </template>

            <!-- 월별 -->
            <template v-else-if="periodType === 'month'">
              <div class="date-input">
                <span class="date-icon">📅</span>
                <input v-model="monthStart" type="month" class="date-field" :max="maxMonth" />
              </div>
              <span class="date-separator">~</span>
              <div class="date-input">
                <span class="date-icon">📅</span>
                <input v-model="monthEnd" type="month" class="date-field" :max="maxMonth" />
              </div>
            </template>

            <!-- 연도별 -->
            <template v-else>
              <!-- 연도 범위 -->
              <div class="year-range" v-if="!isYearDetail">
                <div class="date-input">
                  <span class="date-icon">📅</span>
                  <select v-model="yearStart" class="date-field">
                    <option value="">시작 연도</option>
                    <option v-for="y in yearOptions" :key="'ys-' + y" :value="y">
                      {{ y }}
                    </option>
                  </select>
                </div>
                <span class="date-separator">~</span>
                <div class="date-input">
                  <span class="date-icon">📅</span>
                  <select v-model="yearEnd" class="date-field">
                    <option value="">종료 연도</option>
                    <option v-for="y in yearOptions" :key="'ye-' + y" :value="y">
                      {{ y }}
                    </option>
                  </select>
                </div>
              </div>

              <!-- 특정 연도 하나 -->
              <div class="year-range" v-else>
                <div class="date-input">
                  <span class="date-icon">📅</span>
                  <select v-model="yearDetail" class="date-field">
                    <option value="">연도 선택</option>
                    <option v-for="y in yearOptions" :key="'yd-' + y" :value="y">
                      {{ y }}
                    </option>
                  </select>
                </div>
              </div>
            </template>
          </div>

          <!-- 연도별 옵션 -->
          <div v-if="periodType === 'year'" class="year-detail-toggle">
            <label>
              <input type="checkbox" v-model="isYearDetail" />
              특정 연도만 조회
            </label>
          </div>

          <!-- 버튼 -->
          <div class="actions">
            <button type="button" class="reset-btn" @click="resetFilters">
              <span class="reset-icon">⟳</span>
              <span>선택초기화</span>
            </button>
            <button type="button" class="search-btn" @click="handleSearch">검색하기</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 결과 테이블 (있을 때만) -->
    <section v-if="priceResult.length" class="result-card">
      <h2 class="result-title">조회 결과</h2>
      <table class="result-table">
        <thead>
          <tr>
            <th>일자</th>
            <th>가격</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in priceResult" :key="row.priceDate">
            <td>{{ row.priceDate }}</td>
            <td>{{ row.price.toLocaleString() }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import api from '@/api/axios';

/* ===== 기본 상태 ===== */
const selectedItem = ref('');
const selectedVariety = ref(''); // ✅ 여기에 productCode가 들어가게 할 거임

const itemOptions = ref([]);
const varietyOptions = ref([]);
const priceResult = ref([]);

const periodType = ref('year'); // 기본 연간

const periodTabs = [
  { value: 'year', label: '연간' },
  { value: 'month', label: '월간' },
  { value: 'week', label: '주간' },
  { value: 'day', label: '일간' },
];

// 일별
const dayStart = ref('');
const dayEnd = ref('');

// 주간
const weekStart = ref('');
const weekEnd = ref('');

// 월별
const monthStart = ref('');
const monthEnd = ref('');

// 연도별
const isYearDetail = ref(false);
const yearStart = ref('');
const yearEnd = ref('');
const yearDetail = ref('');

/* ===== 날짜 제한 (과거만) ===== */
const today = new Date();
today.setHours(0, 0, 0, 0);
const yesterday = new Date(today);
yesterday.setDate(yesterday.getDate() - 1);

// 일자 max
const maxDate = yesterday.toISOString().slice(0, 10);

// 월 max
const maxMonth = `${yesterday.getFullYear()}-${String(yesterday.getMonth() + 1).padStart(2, '0')}`;

// 연도 옵션
const maxYear = yesterday.getFullYear();
const minYear = 2000;

const yearOptions = computed(() => {
  const years = [];
  for (let y = maxYear; y >= minYear; y -= 1) {
    years.push(String(y));
  }
  return years;
});

/* ===== 주차 관련 ===== */
function getISOWeekYearAndNumber(date) {
  const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  const dayNum = d.getUTCDay() || 7;
  d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  const year = d.getUTCFullYear();
  const yearStartDate = new Date(Date.UTC(year, 0, 1));
  const week = Math.ceil(((d - yearStartDate) / 86400000 + 1) / 7);
  return { year, week };
}

// "완전히 끝난 주"까지만 선택 가능
const weekday = today.getDay(); // 0=일,1=월,...6=토
const daysSinceMonday = (weekday + 6) % 7;
const lastWeekSunday = new Date(today);
lastWeekSunday.setDate(today.getDate() - daysSinceMonday - 1);

const maxWeekObj = getISOWeekYearAndNumber(lastWeekSunday);
const maxWeek = `${maxWeekObj.year}-W${String(maxWeekObj.week).padStart(2, '0')}`;

// "YYYY-Www" → 그 주의 월요일~일요일
function getWeekRange(weekStr) {
  const [yearStr, weekPart] = weekStr.split('-W');
  const year = Number(yearStr);
  const week = Number(weekPart);

  const simple = new Date(Date.UTC(year, 0, 1 + (week - 1) * 7));
  const dow = simple.getUTCDay() || 7;
  const monday = new Date(simple);
  if (dow <= 4) {
    monday.setUTCDate(simple.getUTCDate() - (dow - 1));
  } else {
    monday.setUTCDate(simple.getUTCDate() + (8 - dow));
  }
  const sunday = new Date(monday);
  sunday.setUTCDate(monday.getUTCDate() + 6);

  return {
    start: monday.toISOString().slice(0, 10),
    end: sunday.toISOString().slice(0, 10),
  };
}

/* ===== 품목 / 품종 API ===== */
const fetchItems = async () => {
  try {
    const res = await api.get('/api/v1/products/item');
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
    const res = await api.get(`/api/v1/products/${itemCode}/subItem`);
    console.log('하위품목 응답:', res.data);

    const body = res.data;
    const list = Array.isArray(body) ? body : Array.isArray(body?.data) ? body.data : [];

    // ✅ 품종 value = productCode 로 통일
    varietyOptions.value = list.map((sub) => ({
      value: sub.productCode ?? sub.code ?? sub.id, // ★ productCode
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

/* ===== 기간 탭 / 리셋 ===== */
const handlePeriodClick = (type) => {
  periodType.value = type;

  dayStart.value = '';
  dayEnd.value = '';
  weekStart.value = '';
  weekEnd.value = '';
  monthStart.value = '';
  monthEnd.value = '';
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

  dayStart.value = '';
  dayEnd.value = '';
  weekStart.value = '';
  weekEnd.value = '';
  monthStart.value = '';
  monthEnd.value = '';
  yearStart.value = '';
  yearEnd.value = '';
  yearDetail.value = '';
  isYearDetail.value = false;
  varietyOptions.value = [];
  priceResult.value = [];
};

/* ===== 응답에서 data 배열 꺼내기 ===== */
/** 백엔드 응답이
 *  1) [ { priceDate, price }, ... ]
 *  2) { data: [ { priceDate, price }, ... ] }
 *  둘 중 하나라고 가정
 */
function extractPriceList(raw) {
  if (!raw) return [];

  if (Array.isArray(raw)) return raw;

  if (raw && typeof raw === 'object') {
    if (Array.isArray(raw.data)) return raw.data;
  }

  return [];
}

/* ===== 검색 ===== */
const handleSearch = async () => {
  // ✅ 무조건 품종(productCode) 기준으로 조회
  const productCode = selectedVariety.value;

  console.log('▶ handleSearch 호출됨', {
    periodType: periodType.value,
    productCode,
    dayStart: dayStart.value,
    dayEnd: dayEnd.value,
  });

  if (!productCode) {
    console.warn('⛔ productCode 없음 (품종 미선택)');
    alert('품종을 먼저 선택해 주세요.');
    return;
  }

  let url = '';
  let params = {};

  try {
    if (periodType.value === 'day') {
      // 일별
      if (!dayStart.value || !dayEnd.value) {
        alert('일별 조회: 시작일과 종료일을 모두 선택해 주세요.');
        return;
      }
      if (dayStart.value > dayEnd.value) {
        alert('일별 조회: 시작일이 종료일보다 늦을 수 없습니다.');
        return;
      }
      if (dayEnd.value > maxDate) {
        alert('일별 조회: 오늘과 미래 날짜는 선택할 수 없습니다.');
        return;
      }

      url = `/api/v1/daily-prices/${productCode}`;
      params = {
        startDate: dayStart.value,
        endDate: dayEnd.value,
      };
    } else if (periodType.value === 'week') {
      // 주간
      if (!weekStart.value || !weekEnd.value) {
        alert('주간 조회: 시작 주와 종료 주를 모두 선택해 주세요.');
        return;
      }
      if (weekStart.value > weekEnd.value) {
        alert('주간 조회: 시작 주가 종료 주보다 늦을 수 없습니다.');
        return;
      }
      if (weekEnd.value > maxWeek) {
        alert('주간 조회: 미래 주는 선택할 수 없습니다.');
        return;
      }

      const startRange = getWeekRange(weekStart.value);
      const endRange = getWeekRange(weekEnd.value);

      url = `/api/v1/weekly-prices/${productCode}`;
      params = {
        startDate: startRange.start,
        endDate: endRange.end,
      };
    } else if (periodType.value === 'month') {
      // 월별
      if (!monthStart.value || !monthEnd.value) {
        alert('월간 조회: 시작 월과 종료 월을 모두 선택해 주세요.');
        return;
      }
      if (monthStart.value > monthEnd.value) {
        alert('월간 조회: 시작 월이 종료 월보다 늦을 수 없습니다.');
        return;
      }
      if (monthEnd.value > maxMonth) {
        alert('월간 조회: 미래 월은 선택할 수 없습니다.');
        return;
      }

      const [sy, sm] = monthStart.value.split('-').map(Number);
      const [ey, em] = monthEnd.value.split('-').map(Number);

      url = `/api/v1/monthly-prices/${productCode}`;
      params = {
        startYear: sy,
        startMonth: sm,
        endYear: ey,
        endMonth: em,
      };
    } else if (periodType.value === 'year') {
      // 연도별
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

        url = `/api/v1/yearly-prices/${productCode}/detail`;
        params = { year: y };
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

        url = `/api/v1/yearly-prices/${productCode}`;
        params = {
          startYear: ys,
          endYear: ye,
        };
      }
    } else {
      alert('잘못된 기간 유형입니다.');
      return;
    }

    // 최종 URL 디버그용
    console.log('📡 최종 요청 URL =', url + '?' + new URLSearchParams(params).toString());

    const { data } = await api.get(url, { params });

    console.log('📦 raw 응답 data', data);

    const list = extractPriceList(data);
    priceResult.value = list;

    console.log('✅ 조회 결과 리스트', list);
  } catch (error) {
    console.error('❌ 가격 조회 실패', error);
    if (error.response) {
      console.error('응답 상태코드:', error.response.status);
      console.error('응답 바디:', error.response.data);
    }
    alert('가격 정보를 가져오는 중 오류가 발생했습니다.');
  }
};
</script>

<style scoped>
.price-search-page {
  padding: 40px 0;
  box-sizing: border-box;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px;
  color: #222;
}

.page-subtitle {
  margin: 0;
  font-size: 13px;
  color: #999;
}

.search-card {
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.04);
  padding: 24px 32px;
  max-width: 1000px;
  margin: 0 auto;
}

.row {
  display: flex;
  align-items: center;
}

.row-top {
  gap: 40px;
  margin-bottom: 16px;
}

.row-bottom {
  justify-content: space-between;
  margin-top: 16px;
}

.row-bottom-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.field {
  display: flex;
  align-items: center;
  gap: 12px;
}

.field-label {
  font-size: 13px;
  font-weight: 600;
  color: #555;
  min-width: 40px;
}

.select {
  min-width: 140px;
  padding: 6px 12px;
  font-size: 13px;
  border-radius: 999px;
  border: 1px solid #ddd;
  outline: none;
  background-color: #fff;
}

.select:focus {
  border-color: #fe7429;
}

.period-field {
  gap: 18px;
}

.period-tabs {
  display: inline-flex;
  padding: 3px;
  border-radius: 999px;
  background-color: #f5f5f7;
}

.period-tab {
  border: none;
  background: transparent;
  padding: 6px 18px;
  border-radius: 999px;
  font-size: 13px;
  cursor: pointer;
  color: #777;
}

.period-tab.active {
  background-color: #e53935;
  color: #fff;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-input {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #ddd;
  background-color: #fff;
}

.date-icon {
  font-size: 14px;
}

.date-field {
  border: none;
  outline: none;
  font-size: 13px;
  background: transparent;
}

.date-separator {
  font-size: 14px;
  color: #999;
}

.week-input.week-selected {
  border-color: #e53935;
  background-color: #ffecec;
}

.week-input.week-selected .date-icon {
  color: #e53935;
}

.year-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.year-detail-toggle {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.reset-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border-radius: 999px;
  border: 1px solid #ddd;
  background-color: #fff;
  padding: 8px 14px;
  font-size: 13px;
  cursor: pointer;
  color: #555;
}

.reset-btn:hover {
  background-color: #f5f5f5;
}

.reset-icon {
  font-size: 13px;
}

.search-btn {
  border: none;
  border-radius: 999px;
  padding: 9px 22px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  background-color: #e53935;
  color: #fff;
}

.search-btn:hover {
  opacity: 0.9;
}

.divider {
  height: 1px;
  background-color: #f0f0f0;
  margin: 4px 0;
}

/* 결과 테이블 */
.result-card {
  max-width: 1000px;
  margin: 24px auto 0;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.04);
  padding: 16px 24px 20px;
}

.result-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.result-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.result-table th,
.result-table td {
  border-bottom: 1px solid #eee;
  padding: 8px 4px;
}

.result-table th {
  text-align: left;
  color: #666;
  font-weight: 600;
}

.result-table td {
  color: #333;
}

/* 반응형 */
@media (max-width: 768px) {
  .price-search-page {
    padding: 20px 0;
  }

  .search-card {
    padding: 16px 16px 20px;
  }

  .row-top,
  .row-bottom {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .row-bottom-right {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    width: 100%;
  }

  .field {
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    gap: 12px;
    width: 100%;
  }

  .field-label {
    min-width: auto;
    white-space: nowrap;
  }

  .period-tabs {
    width: 100%;
  }

  .period-tab {
    flex: 1;
    text-align: center;
  }

  .date-range {
    width: 100%;
    justify-content: space-between;
  }

  .date-input {
    flex: 1;
  }

  .actions {
    width: 100%;
    justify-content: flex-end;
  }
}

.reset-btn,
.search-btn {
  word-break: keep-all;
  white-space: nowrap;
  writing-mode: horizontal-tb;

  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  padding: 0 24px;
  border-radius: 999px;
}

.actions {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}
</style>
