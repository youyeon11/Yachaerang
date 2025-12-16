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
          <td>{{ farm.manpower }}명</td>
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
          <td>{{ farm.flatArea }} m²</td>
        </tr>
        <tr>
          <th>경작면적</th>
          <td>{{ farm.cultivatedArea }} m²</td>
        </tr>
      </tbody>
    </table>

    <section v-if="farm && (farm.grade || farm.comment)" class="farm-eval">
      <div class="farm-eval-header">
        <span class="farm-eval-title">농장 평가</span>
        <span v-if="farm.grade" class="farm-grade-badge">{{ farm.grade }}</span>
      </div>
      <p v-if="farm.comment" class="farm-comment">
        {{ farm.comment }}
      </p>
    </section>

    <div class="farm-actions">
      <button type="button" class="btn primary" @click="$emit('edit')">수정하기</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  farm: {
    type: Object,
    required: true,
  },
});

defineEmits(['edit']);
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

.farm-eval {
  margin-top: 20px;
  padding: 14px 16px;
  border-radius: 10px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
}

.farm-eval-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.farm-eval-title {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.farm-grade-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 2px 10px;
  border-radius: 999px;
  background: #fef3c7;
  color: #92400e;
  font-size: 12px;
  font-weight: 600;
}

.farm-comment {
  margin: 0;
  font-size: 13px;
  color: #4b5563;
  line-height: 1.5;
}

.farm-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.btn {
  padding: 10px 18px;
  border-radius: 6px;
  font-size: 14px;
  border: none;
  cursor: pointer;
}

.btn.primary {
  background: #fecc21;
}
</style>
