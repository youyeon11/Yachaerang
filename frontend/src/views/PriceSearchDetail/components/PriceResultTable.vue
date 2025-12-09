<template>
  <section class="result-card">
    <h2 class="result-title">조회 결과</h2>
    <table class="result-table">
      <thead>
        <tr>
          <th>일자</th>
          <th>가격</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="row in rows"
          :key="row.priceDate"
        >
          <td>{{ row.priceDate }}</td>
          <td>{{ row.price.toLocaleString() }}</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup>
defineProps({
  rows: {
    type: Array,
    default: () => [],
  },
});
</script>


