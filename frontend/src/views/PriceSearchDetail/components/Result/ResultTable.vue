<template>
  <div class="table-wrapper">
    <table class="result-table">
      <thead>
        <tr>
          <th>일자</th>
          <th class="th-right">가격(원)</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in rows" :key="row.dateLabel">
          <td class="td-date">{{ row.dateLabel }}</td>
          <td class="td-price">
            <span class="price-text">
              {{ row.priceLabel != null ? row.priceLabel.toLocaleString() : '-' }}
            </span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
defineProps({
  rows: { type: Array, default: () => [] },
});
</script>

<style scoped>
.table-wrapper {
  width: 100%;
  overflow-x: auto;
}

.result-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

th,
td {
  padding: 10px 8px;
  border-bottom: 1px solid #f0f0f0;
}

thead th {
  font-size: 12px;
  font-weight: 600;
  color: #777;
  background-color: #fafafa;
}

tbody tr:nth-child(even) {
  background-color: #fcfcfc;
}

.th-right {
  text-align: right;
}

.td-date {
  white-space: nowrap;
}

.td-price {
  text-align: right;
  font-variant-numeric: tabular-nums;
}

.price-text {
  font-weight: 500;
  color: #222;
}
</style>
