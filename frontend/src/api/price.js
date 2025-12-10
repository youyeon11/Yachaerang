import apiClient from './axios';

// 품목 목록 조회
export function fetchItemsApi() {
  return apiClient.get('/api/v1/products/item');
}

// 하위 품목(품종) 목록 조회
export function fetchSubItemsApi(itemCode) {
  return apiClient.get(`/api/v1/products/${itemCode}/subItem`);
}

// 일별 가격
export function fetchDailyPricesApi(productCode, params) {
  return apiClient.get(`/api/v1/daily-prices/${productCode}`, { params });
}

// 주별 가격
export function fetchWeeklyPricesApi(productCode, params) {
  return apiClient.get(`/api/v1/weekly-prices/${productCode}`, { params });
}

// 월별 가격
export function fetchMonthlyPricesApi(productCode, params) {
  return apiClient.get(`/api/v1/monthly-prices/${productCode}`, { params });
}

// 연간 가격 (범위)
export function fetchYearlyPricesApi(productCode, params) {
  return apiClient.get(`/api/v1/yearly-prices/${productCode}`, { params });
}

// 연간 가격 (특정 연도 상세)
export function fetchYearlyPriceDetailApi(productCode, params) {
  return apiClient.get(`/api/v1/yearly-prices/${productCode}/detail`, { params });
}


