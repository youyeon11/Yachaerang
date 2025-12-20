<template>
  <section class="section ai" ref="target" :class="{ show: isVisible }">
    <div class="content">

      <!-- 텍스트 영역 (left) -->
      <div class="text-area">
        <h2>
          원하는 품목의<br />
          가격 추이 대시보드
        </h2>
        <p>
          원하는 기한의 품목의 시세를<br />
          야채랑 통계보드로 한눈에 확인하세요
        </p>
        <button class="btn btn-red" @click="$router.push('/dashboard')">
          대시보드 바로가기
        </button>
      </div>
      <!-- 마키 이미지 영역 (rignht) -->
      <div class="marquee-container">
        <div class="marquee-track">
          <img 
            :src="imageSrc" 
            alt="대시보드 서비스 미리보기" 
            class="marquee-image" 
          />
          <img 
            :src="imageSrc" 
            alt="대시보드 서비스 미리보기" 
            class="marquee-image" 
          />
        </div>
        <div class="marquee-fade"></div>
      </div>

    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useSectionObserver } from '@/views/auth/composables/useSectionObserver';
import imageOrigin from '@/assets/Mockup.png';

const target = ref(null);
const { isVisible, observe } = useSectionObserver();
const imageSrc = imageOrigin

onMounted(() => {
  observe(target.value);
});
</script>

<style scoped>
.section.ai {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  width: 100%;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2.5rem;
  max-width: 1600px;
  width: 95%;
}

@media (min-width: 768px) {
  .content {
    flex-direction: row;
    gap: 4rem;
    width: 90%;
  }
}

@media (min-width: 1400px) {
  .content {
    max-width: 1800px;
    gap: 5rem;
  }
}

/* 텍스트 영역 */
.text-area {
  flex: 1;
  min-width: 280px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.75rem;
  text-align: left;
}

@media (min-width: 768px) {
  .text-area {
    min-width: 320px;
  }
}

@media (min-width: 1024px) {
  .text-area {
    min-width: 400px;
  }
}

@media (max-width: 767px) {
  .text-area {
    align-items: center;
    text-align: center;
  }
}

.text-area .label {
  color: #e53935 !important;
  font-weight: 600;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.text-area h2 {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

@media (min-width: 768px) {
  .text-area h2 {
    font-size: 2.25rem;
  }
}

@media (min-width: 1024px) {
  .text-area h2 {
    font-size: 2.5rem;
  }
}

.text-area p {
  color: #6b7280;
  line-height: 1.75;
  margin: 0;
  font-size: 1rem;
}

@media (min-width: 1024px) {
  .text-area p {
    font-size: 1.125rem;
  }
}

.btn-red {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background-color: #e53935;
  color: #fff6f6;
  font-weight: 600;
  border: none;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.2s;
}

@media (min-width: 1024px) {
  .btn-red {
    padding: 1rem 2rem;
    font-size: 1.1rem;
  }
}

.btn-red:hover {
  background-color: #e53935;
  transform: translateY(-2px);
}

/* 마키 컨테이너 */
.marquee-container {
  flex: 2;
  position: relative;
  width: 100%;
  overflow: hidden;
  border-radius: 1rem;
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  height: 16rem;
}

@media (min-width: 768px) {
  .marquee-container {
    height: 22rem;
    min-width: 400px;
  }
}

@media (min-width: 1024px) {
  .marquee-container {
    height: 26rem;
    min-width: 550px;
  }
}

@media (min-width: 1400px) {
  .marquee-container {
    height: 30rem;
    min-width: 700px;
  }
}

.marquee-track {
  display: flex;
  height: 100%;
  width: max-content;
  animation: marquee 20s linear infinite;
}

.marquee-image {
  height: 100%;
  width: auto;
  object-fit: cover;
}

.marquee-fade {
  position: absolute;
  inset: 0;
  pointer-events: none;
  box-shadow: 
    inset 30px 0 40px -10px white,
    inset -30px 0 40px -10px white;
}

/* 애니메이션 */
@keyframes marquee {
  0% {
    transform: translateX(0%);
  }
  100% {
    transform: translateX(-50%);
  }
}

/* 섹션 등장 애니메이션 */
.section.ai {
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.6s ease, transform 0.6s ease;
}

.section.ai.show {
  opacity: 1;
  transform: translateY(0);
}
</style>