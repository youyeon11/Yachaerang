<template>
  <Teleport to="body">
    <Transition name="fade">
      <div v-if="isLoading" class="loading-overlay">
        <!-- 블러 배경 -->
        <div class="blur-background"></div>
        
        <!-- 스피너 컨테이너 -->
        <div class="spinner-container">
          <!-- 메인 스피너 -->
          <div class="spinner">
            <div class="spinner-ring spinner-ring-red"></div>
            <div class="spinner-ring spinner-ring-yellow"></div>
            <div class="spinner-dot"></div>
          </div>
          
          <!-- 로딩 텍스트 (선택사항) -->
          <p v-if="showText" class="loading-text">{{ text }}</p>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
defineProps({
  isLoading: {
    type: Boolean,
    default: false
  },
  showText: {
    type: Boolean,
    default: true
  },
  text: {
    type: String,
    default: '로딩 중...'
  }
});
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.blur-background {
  position: absolute;
  inset: 0;
  background-color: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.spinner-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

/* 스피너 */
.spinner {
  position: relative;
  width: 80px;
  height: 80px;
}

.spinner-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 4px solid transparent;
}

.spinner-ring-red {
  border-top-color: #E53935;
  border-right-color: #E53935;
  animation: spin 1.2s linear infinite;
}

.spinner-ring-yellow {
  inset: 8px;
  border-bottom-color: #FECC21;
  border-left-color: #FECC21;
  animation: spin 1.2s linear infinite reverse;
}

.spinner-dot {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, #E53935, #FECC21);
  border-radius: 50%;
  animation: pulse 1.2s ease-in-out infinite;
}

/* 로딩 텍스트 */
.loading-text {
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
  color: #E53935;
  animation: textPulse 1.5s ease-in-out infinite;
}

/* 애니메이션 */
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 1;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.3);
    opacity: 0.7;
  }
}

@keyframes textPulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 트랜지션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>