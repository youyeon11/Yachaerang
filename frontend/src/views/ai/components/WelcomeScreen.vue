<template>
  <div class="welcome">
    <h1>AI 농업 도우미, 야치</h1>
    <p class="desc">농작물, 시세, 판매 전략에 대해 무엇이든 물어보세요</p>

    <div class="cards">
      <div class="card">
        <span class="emoji">📊</span>
        <span>실시간 시세 정보</span>
      </div>

      <div class="card">
        <span class="emoji">🤖</span>
        <span>AI 가격 예측</span>
      </div>

      <div class="card">
        <span class="emoji">🌱</span>
        <span>재배 관리 조언</span>
      </div>

      <div class="card">
        <span class="emoji">💼</span>
        <span>판매 전략 컨설팅</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.welcome {
  text-align: center;
  margin-top: 80px;
}

h1 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
}

.desc {
  font-size: 14px;
  color: #888;
  margin-bottom: 40px;
}

.cards {
  display: grid;
  grid-template-columns: repeat(2, 200px);
  justify-content: center;
  gap: 20px;
}

.card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 16px;
  padding: 28px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: 0.25s ease;
}

.card:hover {
  transform: translateY(-4px);
  border-color: #333;
}

.emoji {
  font-size: 26px;
}
</style>
