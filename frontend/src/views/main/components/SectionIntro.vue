<template>
  <section class="section intro" ref="target" :class="{ show: isVisible }">
    <h1>농산물 시장의 실시간 플랫폼</h1>
    <p>농업인의 가격 나침반, 야채량</p>

    <div class="cards">
      <div class="card-guide">Guide</div>
      <div class="card" @click="$router.push('/articles')">농업 소식</div>
      <div class="card" @click="$router.push('/ai-chat')">AI 야치</div>
      <div class="card" @click="$router.push('/searchMainpage')">실시간 시세가</div>
      <div class="card" @click="$router.push('/mypage/farm')">나의 농장</div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useSectionObserver } from '@/views/auth/composables/useSectionObserver';

const target = ref(null);
const { isVisible, observe } = useSectionObserver();

onMounted(() => {
  observe(target.value);
});
</script>
<style scoped>
.section {
  text-align: center;
}
.cards {
  margin-top: 50px;
  display: flex;
  justify-content: center;
  gap: 20px;
}
.card {
  width: 160px;
  height: 220px;
  background: #eaeaea;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}
.card-guide {
  width: 160px;
  height: 220px;
  background: #707070;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}
.card:hover {
  background: #67ba35;
  color: #fff;
  transform: translateY(-6px);
}
</style>
