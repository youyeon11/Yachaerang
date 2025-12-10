<template>
  <section class="section dashboard" ref="target" :class="{ show: isVisible }">
    <div class="content">
      <div class="text-area">
        <h3>야채랑 통계보드</h3>
        <h2>
          원하는 가격에<br />
          농산물을 판매해보세요
        </h2>
        <p>
          농산물의 시세를<br />
          야채랑 통계보드로 한눈에 확인하세요
        </p>

        <button class="btn btn-red" @click="$router.push('/searchMainpage')">통계보드 바로가기</button>
      </div>

      <div class="img-area"></div>
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
.img-area {
  width: 640px;
  height: 360px;
}
.text-area {
  display: flex;
  flex-direction: column;
  align-items: flex-end; /* 오른쪽 정렬 */
  text-align: right; /* 글자 정렬 오른쪽 */
}

.text-area h3 {
  color: #e53935;
}
</style>
