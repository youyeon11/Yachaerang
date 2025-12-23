<template>
  <div class="flex items-center justify-center gap-2 pt-8" v-if="totalPages > 1">
    <button
      type="button"
      @click="changePage(currentPage - 1)"
      :disabled="currentPage === 1"
      class="p-2 rounded-xl border bg-white hover:border-[#FECC21] disabled:opacity-30 disabled:hover:border-transparent transition-all group"
    >
      <IconChevronLeft class="w-5 h-5 text-gray-400 group-hover:text-[#FECC21]" />
    </button>

    <button
      v-for="page in visiblePages"
      :key="page"
      type="button"
      @click="changePage(page)"
      class="w-10 h-10 rounded-xl font-bold transition-all"
      :class="
        currentPage === page
          ? 'bg-[#FECC21] text-gray-800 shadow-md scale-105'
          : 'text-gray-500 bg-white border border-transparent hover:border-[#FECC21] hover:text-[#FECC21]'
      "
    >
      {{ page }}
    </button>

    <button
      type="button"
      @click="changePage(currentPage + 1)"
      :disabled="currentPage === totalPages"
      class="p-2 rounded-xl border bg-white hover:border-[#FECC21] disabled:opacity-30 disabled:hover:border-transparent transition-all group"
    >
      <IconChevronRight class="w-5 h-5 text-gray-400 group-hover:text-[#FECC21]" />
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
});

const emit = defineEmits(['change-page']);

const visiblePages = computed(() => {
  const pages = [];
  for (let i = 1; i <= props.totalPages; i += 1) {
    pages.push(i);
  }
  return pages;
});

const changePage = (page) => {
  if (page < 1 || page > props.totalPages || page === props.currentPage) return;
  emit('change-page', page);
};
</script>
