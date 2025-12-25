<template>
  <div class="max-w-2xl mx-auto">
    <h3 class="text-center font-extrabold text-gray-900 text-xl mb-8">이 소식이 유익하셨나요?</h3>

    <div class="grid grid-cols-5 gap-3 mb-10">
      <div v-for="(emoji, type) in reactionIcons" :key="type" class="relative group w-full">
        <!-- Hover 툴팁 -->
        <div
          class="absolute bottom-full left-1/2 -translate-x-1/2 mb-3 w-36 bg-gray-900 text-white p-3 rounded-xl text-xs opacity-0 group-hover:opacity-100 transition-all duration-200 pointer-events-none z-30 shadow-2xl flex flex-col items-center"
        >
          <p class="w-full font-bold border-b border-white/20 pb-1 mb-2 text-center">{{ reactionLabels[type] }}</p>

          <div v-if="getReactorsByType(type).length > 0" class="w-full">
            <div
              v-for="user in getReactorsByType(type)"
              :key="user.nickname"
              class="flex items-center justify-center gap-2 mb-1"
            >
              <img
                :src="user.profile || '/default.png'"
                :alt="user.nickname"
                class="w-4 h-4 rounded-full border border-white/20 object-cover"
                @error="handleImageError"
              />
              <span class="truncate">{{ user.nickname }}</span>
            </div>
          </div>
          <p v-else class="text-gray-400 text-center">첫 반응을 남겨보세요!</p>

          <div
            class="absolute top-full left-1/2 -translate-x-1/2 w-0 h-0 border-l-[8px] border-l-transparent border-r-[8px] border-r-transparent border-t-[8px] border-t-gray-900"
          ></div>
        </div>

        <div class="w-full flex flex-col items-center">
          <button
            type="button"
            @click="onToggle(type)"
            class="w-full flex flex-col items-center justify-center p-3 rounded-2xl transition-all duration-200 border-2 bg-white"
            :class="[
              myReaction === type
                ? 'border-[#FECC21] shadow-md scale-105'
                : 'border-transparent shadow-sm hover:border-[#FECC21]/30 hover:shadow-xl hover:-translate-y-1',
            ]"
          >
            <span
              class="text-3xl mb-2 transition-transform duration-300 group-hover:scale-110"
              :class="{ 'scale-125': myReaction === type }"
            >
              {{ emoji }}
            </span>
            <span class="text-[10px] md:text-xs font-bold text-gray-500 mb-1">{{ reactionLabels[type] }}</span>
          </button>

          <button
            v-if="reactions[type]?.count > 0"
            type="button"
            @click.stop="showReactorModal(type)"
            class="mt-2 text-sm font-black text-gray-900 hover:text-[#FECC21] transition-colors cursor-pointer flex items-center gap-1"
          >
            <span>{{ reactions[type]?.count || 0 }}</span>
            <span class="text-xs text-gray-400">명</span>
          </button>
          <span v-else class="mt-2 text-sm font-black text-gray-300">0</span>
        </div>
      </div>
    </div>

    <!-- 리액션 사용자 모달 -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
          <div class="modal-container">
            <div class="modal-header">
              <h3 class="modal-title">
                <span class="text-2xl mr-2">{{ reactionIcons[selectedReactionType] }}</span>
                {{ reactionLabels[selectedReactionType] }} 리액션
              </h3>
              <button type="button" @click="closeModal" class="modal-close-btn">
                <IconX class="w-5 h-5" />
              </button>
            </div>

            <div class="modal-content">
              <div v-if="getReactorsByType(selectedReactionType).length > 0" class="reactor-list">
                <div v-for="user in getReactorsByType(selectedReactionType)" :key="user.nickname" class="reactor-item">
                  <img
                    :src="user.profile || '/default.png'"
                    :alt="user.nickname"
                    class="reactor-avatar"
                    @error="handleImageError"
                  />
                  <span class="reactor-name">{{ user.nickname }}</span>
                </div>
              </div>
              <div v-else class="empty-state">
                <p class="empty-text">아직 이 리액션을 누른 사람이 없습니다.</p>
                <p class="empty-subtext">첫 번째로 리액션을 남겨보세요!</p>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import IconX from '@/components/icons/IconX.vue';

const props = defineProps({
  reactionIcons: {
    type: Object,
    required: true,
  },
  reactionLabels: {
    type: Object,
    required: true,
  },
  reactions: {
    type: Object,
    required: true,
  },
  myReaction: {
    type: String,
    default: null,
  },
  allReactors: {
    type: Array,
    default: () => [],
  },
});

const emit = defineEmits(['toggle-reaction']);

const showModal = ref(false);
const selectedReactionType = ref(null);

const getReactorsByType = (type) => {
  return props.allReactors.filter((user) => user.type === type);
};

const onToggle = (type) => {
  emit('toggle-reaction', type);
};

const showReactorModal = (type) => {
  selectedReactionType.value = type;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedReactionType.value = null;
};

const handleImageError = (event) => {
  event.target.src = '/default.png';
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal-container {
  background: white;
  border-radius: 24px;
  padding: 0;
  max-width: 480px;
  width: calc(100% - 32px);
  max-height: 80vh;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  overflow-x: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-title {
  font-size: 20px;
  font-weight: 800;
  color: #1f2937;
  display: flex;
  align-items: center;
}

.modal-close-btn {
  padding: 8px;
  border-radius: 50%;
  background: #f5f5f5;
  color: #666;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close-btn:hover {
  background: #e5e5e5;
  transform: scale(1.1);
}

.modal-content {
  padding: 24px;
  overflow-y: auto;
  overflow-x: hidden;
  flex: 1;
  min-width: 0;
}

.reactor-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;
  min-width: 0;
}

.reactor-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  background: #f9fafb;
  transition: all 0.2s;
  min-width: 0;
  width: 100%;
}

.reactor-item:hover {
  background: #f3f4f6;
  transform: translateX(4px);
}

.reactor-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e5e7eb;
}

.reactor-name {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

.empty-state {
  text-align: center;
  padding: 48px 24px;
}

.empty-text {
  font-size: 16px;
  font-weight: 600;
  color: #6b7280;
  margin-bottom: 8px;
}

.empty-subtext {
  font-size: 14px;
  color: #9ca3af;
}

.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-active .modal-container,
.modal-leave-active .modal-container {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: scale(0.9) translateY(20px);
  opacity: 0;
}

.reactor-list::-webkit-scrollbar {
  width: 6px;
}

.reactor-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.reactor-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

.reactor-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
