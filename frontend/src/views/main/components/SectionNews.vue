<template>
  <section class="section news" ref="target" :class="{ show: isVisible }">
    <div class="content">
      <div class="img-area"></div>

      <div class="text-area">
        <h3>농업 소식</h3>
        <h2>한 눈에 쏘옥 볼 수 있는 농업 소식</h2>
        <p>
          간단하게 확인할 수 있는<br />
          야채랑 PICK 농업 소식
        </p>

        <button class="btn btn-yellow" @click="$router.push('/articles')">농업 소식 바로가기</button>
      </div>
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
  width: 650px;
  height: 380px;
}
.text-area h3 {
  color: #fecc21;
}
</style>
