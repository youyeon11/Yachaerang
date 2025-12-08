<template>
  <div class="farm-summary">
    <header class="mypage-header">
      <h1 class="mypage-title">나의 농장</h1>
      <p class="mypage-subtitle">등록된 농장 정보를 확인할 수 있어요.</p>
    </header>

    <table class="farm-table" v-if="farm">
      <tbody>
        <tr>
          <th>인력</th>
          <td>{{ farm.people }}명</td>
        </tr>
        <tr>
          <th>농장위치</th>
          <td>{{ farm.location }}</td>
        </tr>
        <tr>
          <th>주품목</th>
          <td>{{ farm.mainCrop }}</td>
        </tr>
        <tr>
          <th>평지면적</th>
          <td>{{ farm.flatArea }} km²</td>
        </tr>
        <tr>
          <th>경작면적</th>
          <td>{{ farm.cultivationArea }} km²</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
const props = defineProps({
  farm: {
    type: Object,
    required: true,
  },
});
</script>

<style scoped>
.mypage-header {
  margin-bottom: 20px;
}

.mypage-title {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 4px;
}

.mypage-subtitle {
  margin: 0;
  font-size: 13px;
  color: #888;
}

.farm-table {
  width: 100%;
  border-collapse: collapse;
  border-top: 1px solid #ddd;
  border-bottom: 1px solid #ddd;
  margin-top: 12px;
}

.farm-table th,
.farm-table td {
  padding: 10px 12px;
  font-size: 14px;
  border-top: 1px solid #eee;
}

.farm-table th {
  width: 140px;
  text-align: left;
  background: #fafafa;
}
</style>
