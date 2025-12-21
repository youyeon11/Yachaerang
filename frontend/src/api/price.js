import { publicClient } from './axios';

export function fetchItemsApi() {
  return publicClient.get('/api/v1/products/item');
}

export function fetchSubItemsApi(itemCode) {
  return publicClient.get(`/api/v1/products/${itemCode}/subItem`);
}

export function fetchDailyPricesApi(productCode, params) {
  return publicClient.get(`/api/v1/daily-prices/${productCode}`, { params });
}

export function fetchWeeklyPricesApi(productCode, params) {
  return publicClient.get(`/api/v1/weekly-prices/${productCode}`, { params });
}

// 월별 가격
export function fetchMonthlyPricesApi(productCode, params) {
  return publicClient.get(`/api/v1/monthly-prices/${productCode}`, { params });
}

// 연간 가격 (범위)
export function fetchYearlyPricesApi(productCode, params) {
  return publicClient.get(`/api/v1/yearly-prices/${productCode}`, { params });
}

// 연간 가격 (특정 연도 상세)
export function fetchYearlyPriceDetailApi(productCode, params) {
  return publicClient.get(`/api/v1/yearly-prices/${productCode}/detail`, { params });
}
// 가격 상위 랭킹
export function fetchHighPriceRank(size = 9) {
  return publicClient.get('/api/v1/daily-prices/rank/high-prices', {
    params: { size },
  });
}

// 가격 하위 랭킹
export function fetchLowPriceRank(size = 9) {
  return publicClient.get('/api/v1/daily-prices/rank/low-prices', {
    params: { size },
  });
}
