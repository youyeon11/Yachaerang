<template>
  <div class="farm-empty">
    <header class="mypage-header">
      <h1 class="mypage-title">나의 농장</h1>
      <p class="mypage-subtitle">간편하게 관리하는 나만의 농장</p>
    </header>

    <div class="farm-notice">
      <div class="notice-left">
        <span class="notice-text">나의 농장이 등록되지 않았습니다.</span>
      </div>
      <button class="notice-button" @click="$emit('click-register')">등록하기</button>
    </div>

    <div class="farm-empty-center">
      <p class="empty-title">나의 농장, 첫 시작을 도와드릴게요!</p>
      <p class="empty-desc">농장 정보를 간단히 알려주세요.</p>
    </div>
  </div>
</template>

<script setup></script>

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

.farm-notice {
  margin-top: 16px;
  padding: 10px 16px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.notice-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #555;
}

.notice-button {
  padding: 6px 18px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
}

.farm-empty-center {
  text-align: center;
  padding: 120px 0 80px;
}

.empty-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 6px;
}

.empty-desc {
  font-size: 14px;
  margin: 0;
}
</style>
