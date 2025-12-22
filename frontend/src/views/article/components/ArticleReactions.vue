<template>
  <div class="max-w-2xl mx-auto">
    <h3 class="text-center font-extrabold text-gray-900 text-xl mb-8">이 소식이 유익하셨나요?</h3>

    <div class="grid grid-cols-5 gap-3 mb-10">
      <div v-for="(emoji, type) in reactionIcons" :key="type" class="relative group">
        <div
          class="absolute bottom-full left-1/2 -translate-x-1/2 mb-3 w-40 bg-gray-900 text-white p-3 rounded-xl text-xs opacity-0 group-hover:opacity-100 transition-all pointer-events-none z-30 shadow-2xl"
        >
          <p class="font-bold border-b border-white/20 pb-1 mb-2">{{ reactionLabels[type] }}</p>
          <div v-if="getReactorsByType(type).length > 0">
            <div
              v-for="user in getReactorsByType(type)"
              :key="user.nickname"
              class="flex items-center gap-2 mb-1"
            >
              <img :src="user.profile" class="w-4 h-4 rounded-full border border-white/20" />
              <span>{{ user.nickname }}</span>
            </div>
          </div>
          <p v-else class="text-gray-400">첫 반응을 남겨보세요!</p>
          <div class="absolute top-full left-1/2 -translate-x-1/2 border-8 border-transparent border-t-gray-900"></div>
        </div>

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
          <span class="text-sm font-black text-gray-900">{{ reactions[type].count }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
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

const getReactorsByType = (type) => {
  return props.allReactors.filter((user) => user.type === type);
};

const onToggle = (type) => {
  emit('toggle-reaction', type);
};
</script>

<style scoped>
.group:hover .group-hover\:opacity-100 {
  transform: translate(-50%, -5px);
}
</style>


