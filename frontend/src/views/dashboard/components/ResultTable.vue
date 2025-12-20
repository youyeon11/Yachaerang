<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden font-mono text-[10px] md:text-xs">
    <table class="w-full text-left">
      <thead class="bg-gray-50 font-bold text-gray-500 uppercase border-b border-gray-100 font-sans tracking-tighter">
        <tr>
          <th class="px-6 py-4">거래 일자</th>
          <th class="px-6 py-4 text-right">금년 시세</th>
          <th class="px-6 py-4 text-right">어제대비</th>
          <th class="px-6 py-4 text-right">전년비</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-gray-50">
        <tr v-if="paginatedData.length === 0">
          <td colspan="4" class="px-6 py-10 text-center text-gray-400 font-sans italic">데이터가 없습니다.</td>
        </tr>
        <tr v-for="row in paginatedData" :key="row.date" class="hover:bg-red-50/20 transition-colors">
          <td class="px-6 py-3.5 font-bold text-gray-500">{{ row.date }}</td>
          <td class="px-6 py-3.5 text-right font-black text-gray-800">{{ row.thisPrice.toLocaleString() }}원</td>
          <td
            class="px-6 py-3.5 text-right font-bold font-sans"
            :class="row.dailyDiff > 0 ? 'text-red-500' : 'text-slate-500'"
          >
            {{ row.dailyDiff > 0 ? '▲' : '▼' }} {{ Math.abs(row.dailyDiff).toLocaleString() }}
          </td>
          <td
            class="px-6 py-3.5 text-right font-bold font-sans"
            :class="row.yoyDiff > 0 ? 'text-red-500' : 'text-slate-500'"
          >
            <span class="text-slate-200 mr-1.5 text-[9px] font-normal font-sans">
              ({{ row.lastPrice.toLocaleString() }})
            </span>
            {{ row.yoyDiff > 0 ? '▲' : '▼' }} {{ Math.abs(row.yoyDiff).toLocaleString() }}
          </td>
        </tr>
      </tbody>
    </table>

    <div
      v-if="totalPages > 1"
      class="p-4 flex justify-center items-center gap-1.5 bg-gray-50/30 font-sans font-bold border-t border-gray-100"
    >
      <button
        @click="$emit('updatePage', Math.max(1, currentPage - 1))"
        :disabled="currentPage === 1"
        class="page-nav-btn"
      >
        &lt;
      </button>

      <button
        v-for="p in visiblePages"
        :key="p"
        @click="$emit('updatePage', p)"
        :class="
          currentPage === p
            ? 'bg-red-500 text-white shadow-sm border-red-500'
            : 'bg-white text-gray-400 border-gray-200'
        "
        class="w-7 h-7 rounded-md text-[10px] border transition-all hover:scale-110 active:scale-95"
      >
        {{ p }}
      </button>

      <button
        @click="$emit('updatePage', Math.min(totalPages, currentPage + 1))"
        :disabled="currentPage === totalPages"
        class="page-nav-btn"
      >
        &gt;
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  paginatedData: Array,
  totalPages: Number,
  currentPage: Number,
});

const emit = defineEmits(['updatePage']);

const visiblePages = computed(() => {
  const range = 10;
  const currentGroup = Math.ceil(props.currentPage / range);
  const startPage = (currentGroup - 1) * range + 1;
  const endPage = Math.min(startPage + range - 1, props.totalPages);

  const pages = [];
  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }
  return pages;
});
</script>

<style scoped>
@reference "../../../assets/main.css";
.page-nav-btn {
  @apply w-7 h-7 flex items-center justify-center rounded-md border border-gray-200 bg-white text-gray-400 transition-all hover:bg-gray-50 disabled:opacity-30 disabled:hover:bg-white;
}
</style>
