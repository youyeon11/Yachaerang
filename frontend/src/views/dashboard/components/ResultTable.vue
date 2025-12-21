<template>
  <div class="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden font-sans text-sm">
    <table class="w-full text-left table-fixed">
      <thead class="bg-slate-50 font-bold text-slate-500 uppercase border-b border-gray-100">
        <tr>
          <th class="w-2/12 px-5 py-5 tracking-tight">거래 일자</th>
          <th class="w-2/12 px-5 py-5 text-right text-slate-900 font-black">금년 시세</th>
          <th class="w-2/12 px-5 py-5 text-right">전일비</th>
          <th class="w-3/12 px-5 py-5 text-right border-l border-slate-100">전년비 변화량</th>
          <th class="w-3/12 px-5 py-5 text-right">전년비 변화율</th>
        </tr>
      </thead>

      <tbody class="divide-y divide-slate-50 font-mono text-slate-700">
        <tr v-if="paginatedData.length === 0">
          <td colspan="5" class="px-6 py-12 text-center text-slate-400 font-sans italic text-base">
            데이터가 없습니다.
          </td>
        </tr>

        <tr
          v-for="row in paginatedData"
          :key="row.date"
          class="hover:bg-slate-50/80 transition-colors group"
          :class="[row.isMax ? 'bg-rose-50/40' : '', row.isMin ? 'bg-blue-50/40' : '']"
        >
          <td class="px-5 py-4 font-bold text-slate-500 font-sans flex items-center gap-2">
            <span class="text-sm">{{ row.date }}</span>
            <span v-if="row.isMax" class="text-[10px] bg-rose-500 text-white px-1.5 py-0.5 rounded font-black"
              >최고</span
            >
            <span v-if="row.isMin" class="text-[10px] bg-blue-500 text-white px-1.5 py-0.5 rounded font-black"
              >최저</span
            >
          </td>

          <td class="px-5 py-4 text-right font-black text-slate-900 text-base tracking-tighter">
            {{ row.thisPrice.toLocaleString() }}원
          </td>

          <td
            class="px-5 py-4 text-right font-bold text-sm"
            :class="row.dailyDiff > 0 ? 'text-rose-500' : row.dailyDiff < 0 ? 'text-blue-500' : 'text-slate-400'"
          >
            <span class="text-xs mr-0.5">{{ row.dailyDiff > 0 ? '▲' : row.dailyDiff < 0 ? '▼' : '-' }}</span>
            {{ row.dailyDiff === 0 ? '0' : Math.abs(row.dailyDiff).toLocaleString() }}
          </td>

          <td
            class="px-5 py-4 text-right border-l border-slate-50 font-bold text-sm"
            :class="row.yoyDiff > 0 ? 'text-rose-600' : 'text-blue-600'"
          >
            <span class="text-xs mr-0.5">{{ row.yoyDiff > 0 ? '▲' : '▼' }}</span>
            {{ Math.abs(row.yoyDiff).toLocaleString() }}
          </td>

          <td class="px-5 py-4 text-right font-bold" :class="row.yoyDiff > 0 ? 'text-rose-600' : 'text-blue-600'">
            <span
              class="inline-block min-w-[55px] bg-white border border-slate-200 shadow-sm px-2 py-1 rounded-md text-xs tracking-tight"
            >
              {{ ((row.yoyDiff / (row.thisPrice - row.yoyDiff)) * 100).toFixed(1) }}%
            </span>
          </td>
        </tr>
      </tbody>
    </table>

    <div
      v-if="totalPages > 1"
      class="p-5 flex justify-center items-center gap-2 bg-slate-50/50 font-sans border-t border-slate-100"
    >
      <button
        @click="$emit('updatePage', Math.max(1, currentPage - 1))"
        :disabled="currentPage === 1"
        class="page-nav-btn"
      >
        이전
      </button>

      <button
        v-for="p in visiblePages"
        :key="p"
        @click="$emit('updatePage', p)"
        :class="
          currentPage === p
            ? 'bg-slate-800 text-white shadow-md border-slate-800'
            : 'bg-white text-slate-500 border-slate-200 hover:border-slate-400'
        "
        class="w-9 h-9 rounded-lg text-sm font-bold border transition-all hover:scale-105 active:scale-95 flex items-center justify-center"
      >
        {{ p }}
      </button>

      <button
        @click="$emit('updatePage', Math.min(totalPages, currentPage + 1))"
        :disabled="currentPage === totalPages"
        class="page-nav-btn"
      >
        다음
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
  @apply w-7 h-7 flex items-center justify-center rounded-md border border-slate-200 bg-white text-slate-400 transition-all hover:bg-slate-50 disabled:opacity-30 disabled:hover:bg-white;
}
</style>
