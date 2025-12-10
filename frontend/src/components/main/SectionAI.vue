<template>
  <section class="section ai" ref="target" :class="{ show: isVisible }">
    <div class="content">
      <div class="img-area"></div>

      <div class="text-area">
        <h3>AI 야채랑</h3>
        <h2>야치는 짱입니다</h2>
        <p>
          시장 판매 시점 추천, 판매 전략 조언 및 <br />
          시장 분석 리포트를 쉽게 받을 수 있어요
        </p>

        <button class="btn btn-yellow" @click="$router.push('/ai-chat')">AI 야채랑 바로가기</button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useSectionObserver } from '@/composables/useSectionObserver';

const target = ref(null);
const { isVisible, observe } = useSectionObserver();

onMounted(() => {
  observe(target.value);
});
</script>

<style scoped>
.img-area {
  width: 540px;
  height: 320px;
}
.text-area h3 {
  color: #fecc21;
}
</style>
