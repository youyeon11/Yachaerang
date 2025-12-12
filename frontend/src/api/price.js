import apiClient from './axios';

export function fetchItemsApi() {
  return apiClient.get('/api/v1/products/item');
}

export function fetchSubItemsApi(itemCode) {
  return apiClient.get(`/api/v1/products/${itemCode}/subItem`);
}

export function fetchDailyPricesApi(productCode, params) {
  return apiClient.get(`/api/v1/daily-prices/${productCode}`, { params });
}

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
// 가격 상위 랭킹
export function fetchHighPriceRank() {
  return apiClient.get('/api/v1/daily-prices/rank/high-price');
}

// 가격 하위 랭킹
export function fetchLowPriceRank() {
  return apiClient.get('/api/v1/daily-prices/rank/low-price');
}
