<template>
  <div
    v-if="totalPages > 1"
    class="flex items-center justify-center gap-2"
    :class="wrapperClass"
  >
    <!-- 이전 버튼 -->
    <button
      type="button"
      @click="changePage(currentPage - 1)"
      :disabled="currentPage === 1"
      class="p-2 rounded-xl border border-gray-300 bg-white hover:bg-gray-50 hover:border-gray-400 disabled:opacity-30 disabled:hover:bg-white disabled:hover:border-gray-300 transition-all group"
      v-if="prevNextType === 'icon'"
    >
      <IconChevronLeft class="w-5 h-5 text-gray-500 group-hover:text-gray-700" />
    </button>
    <button
      type="button"
      @click="changePage(currentPage - 1)"
      :disabled="currentPage === 1"
      class="w-12 h-9 flex items-center justify-center rounded-md border border-gray-300 bg-white text-gray-600 text-sm font-bold transition-all hover:bg-gray-50 hover:border-gray-400 disabled:opacity-30 disabled:hover:bg-white disabled:hover:border-gray-300"
      v-else
    >
      이전
    </button>

    <!-- 페이지 번호 버튼 -->
    <button
      v-for="page in visiblePages"
      :key="page"
      type="button"
      @click="changePage(page)"
      class="font-bold transition-all border"
      :class="[
        currentPage === page
          ? 'bg-gray-600 text-white border-gray-600 shadow-md scale-105'
          : 'text-gray-600 bg-white border-gray-300 hover:bg-gray-50 hover:border-gray-400',
        buttonSize === 'lg' ? 'w-9 h-9 rounded-lg text-base' : 'w-10 h-10 rounded-xl',
      ]"
    >
      {{ page }}
    </button>

    <!-- 다음 버튼 -->
    <button
      type="button"
      @click="changePage(currentPage + 1)"
      :disabled="currentPage === totalPages"
      class="p-2 rounded-xl border border-gray-300 bg-white hover:bg-gray-50 hover:border-gray-400 disabled:opacity-30 disabled:hover:bg-white disabled:hover:border-gray-300 transition-all group"
      v-if="prevNextType === 'icon'"
    >
      <IconChevronRight class="w-5 h-5 text-gray-500 group-hover:text-gray-700" />
    </button>
    <button
      type="button"
      @click="changePage(currentPage + 1)"
      :disabled="currentPage === totalPages"
      class="w-12 h-9 flex items-center justify-center rounded-md border border-gray-300 bg-white text-gray-600 text-sm font-bold transition-all hover:bg-gray-50 hover:border-gray-400 disabled:opacity-30 disabled:hover:bg-white disabled:hover:border-gray-300"
      v-else
    >
      다음
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import IconChevronLeft from '@/components/icons/IconChevronLeft.vue';
import IconChevronRight from '@/components/icons/IconChevronRight.vue';

const props = defineProps({
  currentPage: {
    type: Number,
    required: true,
  },
  totalPages: {
    type: Number,
    required: true,
  },
  maxVisiblePages: {
    type: Number,
    default: undefined, // undefined면 모든 페이지 표시
  },
  prevNextType: {
    type: String,
    default: 'icon', // 'icon' 또는 'text'
    validator: (value) => ['icon', 'text'].includes(value),
  },
  buttonSize: {
    type: String,
    default: 'xl', // 'xl' 또는 'lg'
    validator: (value) => ['xl', 'lg'].includes(value),
  },
  wrapperClass: {
    type: String,
    default: 'pt-8',
  },
});

const emit = defineEmits(['change-page']);

const visiblePages = computed(() => {
  if (!props.maxVisiblePages) {
    // 모든 페이지 표시
    const pages = [];
    for (let i = 1; i <= props.totalPages; i += 1) {
      pages.push(i);
    }
    return pages;
  }

  // 범위 기반 표시
  const range = props.maxVisiblePages;
  const currentGroup = Math.ceil(props.currentPage / range);
  const startPage = (currentGroup - 1) * range + 1;
  const endPage = Math.min(startPage + range - 1, props.totalPages);

  const pages = [];
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
});

const changePage = (page) => {
  if (page < 1 || page > props.totalPages || page === props.currentPage) return;
  emit('change-page', page);
};
</script>

