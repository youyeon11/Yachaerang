<template>
  <div class="welcome">
    <div class="welcome-inner">
      <section class="hero">
        <p class="badge">AI 야치와 함께하는 스마트 농업</p>

        <h1 class="title">
          오늘 농사와<br />
          가격이 고민되시나요?
        </h1>

        <p class="desc">
          재배 관리, 출하 시기, 도매 시세까지
          <br />
          농업 데이터와 AI가 함께 답을 찾아드려요.
        </p>

        <div class="examples-grid">
          <button
            v-for="example in examples"
            :key="example.label"
            type="button"
            class="example-card"
            @click="usePrompt(example.prompt)"
          >
            <div class="example-emoji">{{ example.emoji }}</div>
            <div class="example-label">{{ example.label }}</div>
          </button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(['use-prompt']);

const examples = [
  {
    emoji: '📊',
    label: '실시간 시세 정보',
    prompt: '요즘 양파 도매 시세랑 최근 일주일 추세를 정리해서 알려줘',
  },
  {
    emoji: '🤖',
    label: 'AI 가격 예측',
    prompt: '다음 주 상추 도매 가격이 오를지 떨어질지 전망해 줘',
  },
  {
    emoji: '🌱',
    label: '재배 관리 조언',
    prompt: '노지 상추가 요즘 잎 끝이 누렇게 되는데 원인과 관리 방법 알려줘',
  },
  {
    emoji: '💼',
    label: '판매 전략 컨설팅',
    prompt: '다음 달에 출하 예정인 배추를 언제, 어디에 파는 게 유리할지 제안해 줘',
  },
];

const usePrompt = (text) => {
  emit('use-prompt', text);
};
</script>

<style scoped>
.welcome {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 0 40px;
}

.welcome-inner {
  width: 100%;
  max-width: 960px;
  display: flex;
  justify-content: center;
}

.hero {
  width: 100%;
  max-width: 520px;
  text-align: center;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 999px;
  background: #fff8d5;
  color: #9a6b00;
  font-size: 12px;
  margin-bottom: 12px;
}

.title {
  font-size: 30px;
  font-weight: 800;
  line-height: 1.3;
  margin-bottom: 12px;
}

.desc {
  font-size: 14px;
  color: #777;
  line-height: 1.6;
  margin-bottom: 20px;
}

.examples-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px 18px;
  margin-top: 20px;
  max-width: 360px;
}

.example-card {
  border-radius: 16px;
  border: 1px solid #e5e5e5;
  background: #ffffff;
  padding: 14px 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.1s ease, background 0.18s ease;
}

.example-card:hover {
  border-color: #fecc21;
  background: #fffef7;
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

.example-emoji {
  font-size: 24px;
}

.example-label {
  font-size: 13px;
  color: #444;
}

@media (max-width: 768px) {
  .welcome {
    padding: 24px 0 32px;
  }

  .welcome-inner {
    grid-template-columns: minmax(0, 1fr);
    gap: 24px;
  }

  .hero {
    text-align: left;
  }

  .title {
    font-size: 24px;
  }
}
</style>
