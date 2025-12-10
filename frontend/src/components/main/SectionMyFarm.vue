<template>
  <section class="section myfarm" ref="target" :class="{ show: isVisible }">
    <div class="content">
      <div class="text-area">
        <h3>나의 농장</h3>
        <h2>편리하게 나의 농장을 관리하세요</h2>
        <p>
          내가 직접 만드는 온라인 농장<br />
          간단하게 관리하세요
        </p>

        <button class="btn btn-yellow" @click="$router.push('/mypage/farm')">나의농장 바로가기</button>
      </div>
      <div class="img-area"></div>
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
  width: 650px;
  height: 380px;
}
.text-area {
  display: flex;
  flex-direction: column;
  align-items: flex-end; /* 오른쪽 정렬 */
  text-align: right; /* 글자 정렬 오른쪽 */
}
.text-area h3 {
  color: #fecc21;
}
</style>
