<template>
  <main class="flex-1 overflow-y-auto p-8 bg-gray-50">
    <div class="mx-auto max-w-4xl space-y-6">
      <!-- 헤더 -->
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-gray-900">나의 농장</h1>
        <p class="text-gray-600">등록된 농장 정보를 확인할 수 있어요</p>
      </div>

      <!-- 농장 미등록 상태 -->
      <div v-if="!farm" class="relative">
        <!-- 블러 처리된 배경 (플레이스홀더) -->
        <div class="blur-sm pointer-events-none select-none">
          <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
            <div class="mb-4">
              <h2 class="text-lg font-semibold text-gray-900">농장 정보</h2>
              <p class="text-sm text-gray-600">등록된 농장의 상세 정보입니다</p>
            </div>
            <div class="space-y-4">
              <div class="grid gap-4 md:grid-cols-2">
                <div class="space-y-1">
                  <label class="text-sm font-medium text-gray-500">인력</label>
                  <p class="text-base font-semibold text-gray-900">-명</p>
                </div>
                <div class="space-y-1">
                  <label class="text-sm font-medium text-gray-500">농장 위치</label>
                  <p class="text-base font-semibold text-gray-900">-</p>
                </div>
                <div class="space-y-1">
                  <label class="text-sm font-medium text-gray-500">주품목</label>
                  <p class="text-base font-semibold text-gray-900">-</p>
                </div>
                <div class="space-y-1">
                  <label class="text-sm font-medium text-gray-500">평지면적</label>
                  <p class="text-base font-semibold text-gray-900">- ㎡</p>
                </div>
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium text-gray-500">경작면적</label>
                <p class="text-base font-semibold text-gray-900">- ㎡</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 오버레이 -->
        <div class="absolute inset-0 flex flex-col items-center justify-center bg-white/60 rounded-lg">
          <p class="text-lg font-semibold text-gray-700 mb-4">농장 정보가 등록되지 않았습니다!</p>
          <button
            @click="$emit('click-register')"
            class="rounded-lg bg-[#F44323] px-6 py-3 font-medium text-white transition-colors hover:bg-[#d63a1f]"
          >
            농장 등록하기
          </button>
        </div>
      </div>
    </div>
    </main>
</template>

<script setup>
defineEmits(['click-register']);
</script>

<style scoped>
.pixel-icon-group {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2px;
}

@keyframes float {
  0%, 100% { 
    transform: translateY(0px);
  }
  50% { 
    transform: translateY(-10px);
  }
}

@keyframes grow {
  0%, 100% { 
    transform: scaleY(1);
  }
  50% { 
    transform: scaleY(1.15);
  }
}

.animate-grow {
  animation: grow 2s ease-in-out infinite;
}
</style>