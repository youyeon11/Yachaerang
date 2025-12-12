<template>
  <table class="result-table">
    <thead>
      <tr>
        <th>일자</th>
        <th>가격</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="row in rows" :key="row.dateLabel">
        <td>{{ row.dateLabel }}</td>
        <td>{{ row.priceLabel != null ? row.priceLabel.toLocaleString() : '-' }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
defineProps({
  rows: { type: Array, default: () => [] },
});
</script>
