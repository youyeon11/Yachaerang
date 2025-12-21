// 공통 전년 동기간 계산 유틸
// - 입력: 'YYYY-MM-DD' 형태의 시작일/종료일 문자열
// - 출력: 같은 월/일에서 1년 뺀 전년 동기간 범위

/**
 * 주어진 Date 또는 날짜 문자열을 기준으로 연도만 diffYears 만큼 이동시킵니다.
 * 월/일은 유지하되, 윤년(2월 29일) 등의 예외는 그 달의 마지막 날로 보정합니다.
 *
 * @param {Date|string} dateInput
 * @param {number} diffYears 기본값 -1 (전년)
 * @returns {Date|null}
 */
export const shiftDateByYears = (dateInput, diffYears = -1) => {
  if (!dateInput) return null;

  const base = new Date(dateInput);
  if (Number.isNaN(base.getTime())) return null;

  const targetYear = base.getFullYear() + diffYears;
  const month = base.getMonth();
  const day = base.getDate();

  const shifted = new Date(targetYear, month, day);

  if (shifted.getMonth() !== month) {
    return new Date(targetYear, month + 1, 0);
  }

  return shifted;
};

const formatDate = (date) => {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
};

/**
 * 전년 동기간(1년 전) 날짜 범위를 계산합니다.
 *
 * @param {string} startDateStr 'YYYY-MM-DD'
 * @param {string} endDateStr 'YYYY-MM-DD'
 * @returns {{ startDate: string, endDate: string } | null}
 */
export const getLastYearDateRange = (startDateStr, endDateStr) => {
  if (!startDateStr || !endDateStr) return null;

  const shiftedStart = shiftDateByYears(startDateStr, -1);
  const shiftedEnd = shiftDateByYears(endDateStr, -1);

  if (!shiftedStart || !shiftedEnd) return null;

  return {
    startDate: formatDate(shiftedStart),
    endDate: formatDate(shiftedEnd),
  };
};
