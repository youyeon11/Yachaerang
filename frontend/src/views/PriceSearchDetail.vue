<template>
  <div class="page-container price-search-page">
    <header class="page-header">
      <div class="page-header-left">
        <h1 class="page-title">세부 가격 검색</h1>
        <p class="page-subtitle">내가 원하는대로 검색하고 정렬할 수 있는 야채랑 농, 수산물 가격정보</p>
      </div>
    </header>

    <section class="search-card">
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
          <select v-model="selectedVariety" class="select" :disabled="!selectedItem">
            <option value="">선택</option>
            <option v-for="variety in varietyOptions" :key="variety.value" :value="variety.value">
              {{ variety.label }}
            </option>
          </select>
        </div>
      </div>

      <div class="divider"></div>

      <div class="row row-bottom">
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

        <div class="row row-bottom-right">
          <div class="filters-col">
            <div class="date-range">
              <!-- 일별 -->
              <template v-if="periodType === 'day'">
                <VDatePicker
                  v-model="dayStartDate"
                  :max-date="yesterday"
                  locale="ko"
                  color="red"
                  :popover="{ visibility: 'click' }"
                >
                  <template #default="{ inputValue, togglePopover }">
                    <div class="date-input clickable" @click="togglePopover">
                      <span class="date-icon">📅</span>
                      <input :value="inputValue" class="date-field" placeholder="시작일" readonly />
                    </div>
                  </template>
                </VDatePicker>
                <span class="date-separator">~</span>
                <VDatePicker
                  v-model="dayEndDate"
                  :max-date="yesterday"
                  :min-date="dayStartDate"
                  locale="ko"
                  color="red"
                  :popover="{ visibility: 'click' }"
                >
                  <template #default="{ inputValue, togglePopover }">
                    <div class="date-input clickable" @click="togglePopover">
                      <span class="date-icon">📅</span>
                      <input :value="inputValue" class="date-field" placeholder="종료일" readonly />
                    </div>
                  </template>
                </VDatePicker>
              </template>

              <!-- 주별 -->
              <template v-else-if="periodType === 'week'">
                <VDatePicker
                  :attributes="weekStartAttributes"
                  :max-date="lastWeekSunday"
                  locale="ko"
                  color="red"
                  :popover="{ visibility: 'click' }"
                  @dayclick="onWeekStartClick"
                >
                  <template #default="{ togglePopover }">
                    <div
                      class="date-input clickable"
                      :class="{ 'week-selected': weekStartDate }"
                      @click="togglePopover"
                    >
                      <span class="date-icon">📅</span>
                      <input
                        :value="formatWeekDisplay(weekStartDate)"
                        class="date-field"
                        placeholder="시작 주"
                        readonly
                      />
                    </div>
                  </template>
                </VDatePicker>
                <span class="date-separator">~</span>
                <VDatePicker
                  :attributes="weekEndAttributes"
                  :max-date="lastWeekSunday"
                  :min-date="weekStartDate || null"
                  locale="ko"
                  color="red"
                  :popover="{ visibility: 'click' }"
                  @dayclick="onWeekEndClick"
                >
                  <template #default="{ togglePopover }">
                    <div class="date-input clickable" :class="{ 'week-selected': weekEndDate }" @click="togglePopover">
                      <span class="date-icon">📅</span>
                      <input
                        :value="formatWeekDisplay(weekEndDate)"
                        class="date-field"
                        placeholder="종료 주"
                        readonly
                      />
                    </div>
                  </template>
                </VDatePicker>
              </template>

              <!-- 월별 -->
              <template v-else-if="periodType === 'month'">
                <div class="month-picker-wrapper">
                  <div class="date-input clickable" @click="toggleMonthStartPicker">
                    <span class="date-icon">📅</span>
                    <input
                      :value="formatMonthDisplay(monthStartDate)"
                      class="date-field"
                      placeholder="시작 월"
                      readonly
                    />
                  </div>
                  <div v-if="showMonthStartPicker" class="month-picker-popup">
                    <div class="month-picker-header">
                      <button type="button" class="month-nav-btn" @click="monthStartYear--">‹</button>
                      <span class="month-picker-year">{{ monthStartYear }}년</span>
                      <button type="button" class="month-nav-btn" @click="monthStartYear++">›</button>
                    </div>
                    <div class="month-grid">
                      <button
                        v-for="m in 12"
                        :key="'start-' + m"
                        type="button"
                        class="month-btn"
                        :class="{
                          selected: isMonthSelected(monthStartDate, monthStartYear, m),
                          disabled: isMonthDisabled(monthStartYear, m),
                        }"
                        :disabled="isMonthDisabled(monthStartYear, m)"
                        @click="selectMonthStart(monthStartYear, m)"
                      >
                        {{ m }}월
                      </button>
                    </div>
                  </div>
                </div>

                <span class="date-separator">~</span>

                <div class="month-picker-wrapper">
                  <div class="date-input clickable" @click="toggleMonthEndPicker">
                    <span class="date-icon">📅</span>
                    <input
                      :value="formatMonthDisplay(monthEndDate)"
                      class="date-field"
                      placeholder="종료 월"
                      readonly
                    />
                  </div>
                  <div v-if="showMonthEndPicker" class="month-picker-popup">
                    <div class="month-picker-header">
                      <button type="button" class="month-nav-btn" @click="monthEndYear--">‹</button>
                      <span class="month-picker-year">{{ monthEndYear }}년</span>
                      <button type="button" class="month-nav-btn" @click="monthEndYear++">›</button>
                    </div>
                    <div class="month-grid">
                      <button
                        v-for="m in 12"
                        :key="'end-' + m"
                        type="button"
                        class="month-btn"
                        :class="{
                          selected: isMonthSelected(monthEndDate, monthEndYear, m),
                          disabled: isMonthEndDisabled(monthEndYear, m),
                        }"
                        :disabled="isMonthEndDisabled(monthEndYear, m)"
                        @click="selectMonthEnd(monthEndYear, m)"
                      >
                        {{ m }}월
                      </button>
                    </div>
                  </div>
                </div>
              </template>

              <!-- 연별 -->
              <template v-else>
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

            <div v-if="periodType === 'year'" class="year-detail-toggle">
              <label>
                <input type="checkbox" v-model="isYearDetail" />
                특정 연도만 조회
              </label>
            </div>
          </div>

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
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import api from '@/api/axios';

/* ================= 공통 상태: 품목 / 기간 ================= */

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

/* ================= 공통: 날짜 제한 / 연도 옵션 ================= */

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

// ISO 주차 계산 (표시용)
function getISOWeekYearAndNumber(date) {
  const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  const dayNum = d.getUTCDay() || 7;
  d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  const year = d.getUTCFullYear();
  const yearStartDate = new Date(Date.UTC(year, 0, 1));
  const week = Math.ceil(((d - yearStartDate) / 86400000 + 1) / 7);
  return { year, week };
}

// 해당 날짜가 속한 주의 월요일
function getMondayOfWeek(date) {
  const d = new Date(date);
  const dayOfWeek = d.getDay();
  const diff = d.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
  const monday = new Date(d);
  monday.setDate(diff);
  monday.setHours(0, 0, 0, 0);
  return monday;
}

// 주 범위 (월~일) - Date 범위 (v-calendar highlight용)
function getWeekRange(date) {
  if (!date) return null;
  const monday = getMondayOfWeek(date);
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() + 6);
  sunday.setHours(23, 59, 59, 999);
  return { start: monday, end: sunday };
}

// 시작 주 클릭
function onWeekStartClick(day) {
  const monday = getMondayOfWeek(day.date);
  weekStartDate.value = monday;

  // 시작 주를 뒤로 옮기면 종료 주 초기화
  if (weekEndDate.value && monday > getMondayOfWeek(weekEndDate.value)) {
    weekEndDate.value = null;
  }
}

// 종료 주 클릭
function onWeekEndClick(day) {
  const monday = getMondayOfWeek(day.date);

  // 시작 주 이전은 선택 불가
  if (weekStartDate.value && monday < getMondayOfWeek(weekStartDate.value)) {
    alert('종료 주는 시작 주 이후여야 합니다.');
    return;
  }

  weekEndDate.value = monday;
}

// v-calendar highlight attributes
const weekStartAttributes = computed(() => {
  if (!weekStartDate.value) return [];
  const range = getWeekRange(weekStartDate.value);
  if (!range) return [];
  return [
    {
      key: 'week-highlight-start',
      highlight: {
        start: { fillMode: 'solid', color: 'red' },
        base: { fillMode: 'light', color: 'red' },
        end: { fillMode: 'solid', color: 'red' },
      },
      dates: range,
    },
  ];
});

const weekEndAttributes = computed(() => {
  if (!weekEndDate.value) return [];
  const range = getWeekRange(weekEndDate.value);
  if (!range) return [];
  return [
    {
      key: 'week-highlight-end',
      highlight: {
        start: { fillMode: 'solid', color: 'red' },
        base: { fillMode: 'light', color: 'red' },
        end: { fillMode: 'solid', color: 'red' },
      },
      dates: range,
    },
  ];
});

// "완전히 끝난 주"까지만 선택 가능하게 하는 기준 일요일
const weekday = today.getDay();
const daysSinceMonday = (weekday + 6) % 7;
const lastWeekSunday = new Date(today);
lastWeekSunday.setDate(today.getDate() - daysSinceMonday - 1);

/* ================= 월별 ================= */

const monthStartDate = ref(null);
const monthEndDate = ref(null);

const showMonthStartPicker = ref(false);
const showMonthEndPicker = ref(false);
const monthStartYear = ref(new Date().getFullYear());
const monthEndYear = ref(new Date().getFullYear());

function toggleMonthStartPicker() {
  showMonthStartPicker.value = !showMonthStartPicker.value;
  showMonthEndPicker.value = false;
  if (showMonthStartPicker.value && monthStartDate.value) {
    monthStartYear.value = new Date(monthStartDate.value).getFullYear();
  }
}

function toggleMonthEndPicker() {
  showMonthEndPicker.value = !showMonthEndPicker.value;
  showMonthStartPicker.value = false;
  if (showMonthEndPicker.value && monthEndDate.value) {
    monthEndYear.value = new Date(monthEndDate.value).getFullYear();
  }
}

function selectMonthStart(year, month) {
  monthStartDate.value = new Date(year, month - 1, 1);
  showMonthStartPicker.value = false;

  // 시작 월보다 이전인 종료 월은 초기화
  if (monthEndDate.value && monthEndDate.value < monthStartDate.value) {
    monthEndDate.value = null;
  }
}

function selectMonthEnd(year, month) {
  monthEndDate.value = new Date(year, month - 1, 1);
  showMonthEndPicker.value = false;
}

function isMonthSelected(selectedDate, year, month) {
  if (!selectedDate) return false;
  const d = new Date(selectedDate);
  return d.getFullYear() === year && d.getMonth() === month - 1;
}

// 미래 월 비활성화
function isMonthDisabled(year, month) {
  const target = new Date(year, month - 1, 1);
  const maxDateForMonth = new Date(yesterday.getFullYear(), yesterday.getMonth(), 1);
  return target > maxDateForMonth;
}

// 시작 월 이전의 종료 월 비활성화
function isMonthEndDisabled(year, month) {
  if (isMonthDisabled(year, month)) return true;
  if (monthStartDate.value) {
    const target = new Date(year, month - 1, 1);
    return target < monthStartDate.value;
  }
  return false;
}

// 외부 클릭 시 월 팝업 닫기
function handleClickOutside(event) {
  const target = event.target;
  if (!(target instanceof Element)) return;
  if (!target.closest('.month-picker-wrapper')) {
    showMonthStartPicker.value = false;
    showMonthEndPicker.value = false;
  }
}

/* ================= 연도별 ================= */

const isYearDetail = ref(false);
const yearStart = ref('');
const yearEnd = ref('');
const yearDetail = ref('');

/* ================= 공통 포맷터 / 변환 ================= */

// 날짜 → 'YYYY-MM-DD'
function formatDateToString(date) {
  if (!date) return '';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

// Date 기준 주(월~일) → 문자열 범위
function getWeekRangeFromDate(date) {
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
}

function formatWeekDisplay(date) {
  if (!date) return '';
  const { year, week } = getISOWeekYearAndNumber(new Date(date));
  return `${year}년 ${week}주차`;
}

function formatMonthDisplay(date) {
  if (!date) return '';
  const d = new Date(date);
  return `${d.getFullYear()}년 ${d.getMonth() + 1}월`;
}

/* ================= API: 품목 / 품종 ================= */

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

/* ================= 라이프사이클 ================= */

onMounted(() => {
  fetchItems();
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
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

function extractPriceList(raw) {
  if (!raw) return [];
  if (Array.isArray(raw)) return raw;
  if (raw && typeof raw === 'object' && Array.isArray(raw.data)) {
    return raw.data;
  }
  return [];
}

/* ================= 검색 ================= */

const handleSearch = async () => {
  const productCode = selectedVariety.value;
  console.log('▶ handleSearch 호출됨', {
    periodType: periodType.value,
    productCode,
  });

  if (!productCode) {
    console.warn('productCode 없음 (품종 미선택)');
    alert('품종을 먼저 선택해 주세요.');
    return;
  }

  let url = '';
  let params = {};

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
      url = `/api/v1/daily-prices/${productCode}`;
      params = {
        startDate: startStr,
        endDate: endStr,
      };
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
      url = `/api/v1/weekly-prices/${productCode}`;
      params = {
        startDate: startRange.start,
        endDate: endRange.end,
      };
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
      url = `/api/v1/monthly-prices/${productCode}`;
      params = {
        startYear: startD.getFullYear(),
        startMonth: startD.getMonth() + 1,
        endYear: endD.getFullYear(),
        endMonth: endD.getMonth() + 1,
      };
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

    console.log('최종 요청 URL =', url + '?' + new URLSearchParams(params).toString());
    const { data } = await api.get(url, { params });
    console.log('raw 응답 data', data);
    const list = extractPriceList(data);
    priceResult.value = list;
    console.log('조회 결과 리스트', list);
  } catch (error) {
    console.error('가격 조회 실패', error);
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
  align-items: flex-start;
}

.row-bottom-right {
  align-items: flex-start;
}
.filters-col {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ✅ 날짜 입력 줄은 항상 한 줄 아래(여기서 한 줄로 유지) */
.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap; /* 줄바꿈 방지(원하시면 wrap으로 변경 가능) */
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
  white-space: nowrap; /* ← 줄바꿈 금지 */
  word-break: keep-all;
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
  min-width: 130px;
}

.date-input.clickable {
  cursor: pointer;
  transition: border-color 0.2s, background-color 0.2s;
}

.date-input.clickable:hover {
  border-color: #e53935;
  background-color: #fff5f5;
}

.date-icon {
  font-size: 14px;
}

.date-field {
  border: none;
  outline: none;
  font-size: 13px;
  background: transparent;
  cursor: inherit;
  width: 100%;
}

.date-separator {
  font-size: 14px;
  color: #999;
}

.week-selected {
  border-color: #e53935;
  background-color: #ffecec;
}

.week-selected .date-icon {
  color: #e53935;
}
.month-picker-wrapper {
  position: relative;
}

.month-picker-popup {
  position: absolute;
  top: 42px;
  left: 0;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 12px;
  min-width: 220px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 20;
}

.month-picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.month-picker-year {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.month-nav-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  border-radius: 999px;
  padding: 4px 8px;
  font-size: 16px;
  line-height: 1;
  color: #666;
  transition: background-color 0.2s, color 0.2s;
}

.month-nav-btn:hover {
  background-color: #f5f5f5;
  color: #333;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
}

.month-btn {
  border-radius: 999px;
  border: 1px solid transparent;
  background-color: #fafafa;
  padding: 6px 0;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.2s, border-color 0.2s, color 0.2s;
}

.month-btn:hover {
  background-color: #ffecec;
  border-color: #ffcdd2;
}

.month-btn.selected {
  background-color: #e53935;
  border-color: #e53935;
  color: #fff;
}

.month-btn.disabled {
  background-color: #f5f5f5;
  border-color: #eee;
  color: #ccc;
  cursor: not-allowed;
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

/* v-calendar 팝오버 스타일 커스텀 */
:deep(.vc-popover-content) {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

:deep(.vc-red) {
  --vc-accent-50: #ffebee;
  --vc-accent-100: #ffcdd2;
  --vc-accent-200: #ef9a9a;
  --vc-accent-300: #e57373;
  --vc-accent-400: #ef5350;
  --vc-accent-500: #e53935;
  --vc-accent-600: #e53935;
  --vc-accent-700: #d32f2f;
  --vc-accent-800: #c62828;
  --vc-accent-900: #b71c1c;
}
:deep(.vc-highlight) {
  border-radius: 0 !important;
}

:deep(.vc-highlight-base-start) {
  border-radius: 50% 0 0 50% !important;
}

:deep(.vc-highlight-base-end) {
  border-radius: 0 50% 50% 0 !important;
}

:deep(.vc-highlight-bg-light) {
  background-color: rgba(229, 57, 53, 0.15) !important;
}

:deep(.vc-day-content:hover) {
  background-color: rgba(229, 57, 53, 0.25) !important;
}

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
    flex-wrap: wrap;
  }

  .date-input {
    flex: 1;
    min-width: 120px;
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
