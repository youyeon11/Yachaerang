<template>
  <div
    class="p-4 sm:p-6 bg-gray-50/50 rounded-2xlbg-white rounded-xl shadow-md border border-gray-100 font-sans text-sm"
  >
    <div class="overflow-x-auto">
      <table class="w-full text-left table-fixed min-w-[800px]">
        <thead class="bg-slate-50 font-bold text-slate-500 uppercase border-b border-gray-100">
          <tr>
            <th class="w-[14%] px-4 py-5 tracking-tight">거래 일자</th>
            <th class="w-[15%] px-4 py-5 text-right text-slate-900 font-black">금년 시세</th>
            <th class="w-[14%] px-4 py-5 text-right">전일 차이</th>
            <th class="w-[14%] px-4 py-5 text-right">전일 변화율</th>
            <th class="w-[15%] px-4 py-5 text-right border-l border-slate-100">작년 시세</th>
            <th class="w-[14%] px-4 py-5 text-right">전년 변화량</th>
            <th class="w-[14%] px-4 py-5 text-right">전년 변화율</th>
          </tr>
        </thead>

        <tbody class="divide-y divide-slate-50 font-mono text-slate-700">
          <tr v-if="paginatedData.length === 0">
            <td colspan="7" class="px-6 py-12 text-center text-slate-400 font-sans italic text-base">
              데이터가 없습니다.
            </td>
          </tr>

          <tr
            v-for="row in paginatedData"
            :key="row.date"
            class="hover:bg-slate-50/80 transition-colors group"
            :class="[row.isMax ? 'bg-rose-50/40' : '', row.isMin ? 'bg-blue-50/40' : '']"
          >
            <td class="px-4 py-4 font-bold text-slate-500 font-sans flex items-center gap-1.5">
              <span class="text-sm whitespace-nowrap">{{ row.date }}</span>
              <span
                v-if="row.isMax"
                class="text-[10px] bg-rose-500 text-white px-1.5 py-0.5 rounded font-black shrink-0"
                >최고</span
              >
              <span
                v-if="row.isMin"
                class="text-[10px] bg-blue-500 text-white px-1.5 py-0.5 rounded font-black shrink-0"
                >최저</span
              >
            </td>

            <td class="px-4 py-4 text-right font-black text-slate-900 text-base tracking-tighter">
              {{ row.thisPrice.toLocaleString() }}원
            </td>

            <td
              class="px-4 py-4 text-right font-bold text-sm"
              :class="row.prevDiff > 0 ? 'text-rose-500' : row.prevDiff < 0 ? 'text-blue-500' : 'text-slate-400'"
            >
              <span class="hidden sm:inline text-xs mr-0.5">{{
                row.prevDiff > 0 ? '▲' : row.prevDiff < 0 ? '▼' : '-'
              }}</span>
              {{ row.prevDiff === 0 ? '0' : Math.abs(row.prevDiff).toLocaleString() }}
            </td>

            <td
              class="px-4 py-4 text-right font-bold text-sm"
              :class="row.prevRate > 0 ? 'text-rose-500' : row.prevRate < 0 ? 'text-blue-500' : 'text-slate-400'"
            >
              <span class="hidden sm:inline text-xs mr-0.5">{{
                row.prevRate > 0 ? '▲' : row.prevRate < 0 ? '▼' : '-'
              }}</span>
              <span>{{ row.prevRate === 0 ? '0.0' : Math.abs(row.prevRate).toFixed(1) }}%</span>
            </td>

            <td class="px-4 py-4 text-right font-bold text-slate-700 text-sm tracking-tighter border-l border-slate-50">
              <span v-if="row.lastPrice"> {{ row.lastPrice.toLocaleString() }}원 </span>
              <span v-else class="text-slate-300">-</span>
            </td>

            <td
              class="px-4 py-4 text-right font-bold text-sm"
              :class="row.yoyDiff > 0 ? 'text-rose-600' : row.yoyDiff < 0 ? 'text-blue-600' : 'text-slate-400'"
            >
              <span class="hidden sm:inline text-xs mr-0.5">{{
                row.yoyDiff > 0 ? '▲' : row.yoyDiff < 0 ? '▼' : '-'
              }}</span>
              {{ row.yoyDiff === 0 ? '0' : Math.abs(row.yoyDiff).toLocaleString() }}
            </td>

            <td
              class="px-4 py-4 text-right font-bold text-sm"
              :class="row.yoyRate > 0 ? 'text-rose-600' : row.yoyRate < 0 ? 'text-blue-600' : 'text-slate-400'"
            >
              <span class="hidden sm:inline text-xs mr-0.5">{{
                row.yoyRate > 0 ? '▲' : row.yoyRate < 0 ? '▼' : '-'
              }}</span>
              <span>{{ row.yoyRate === 0 ? '0.0' : Math.abs(row.yoyRate).toFixed(1) }}%</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

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
  @apply w-12 h-8 flex items-center justify-center rounded-md border border-slate-200 bg-white text-slate-600 text-xs font-bold transition-all hover:bg-slate-50 disabled:opacity-30 disabled:hover:bg-white;
}
div {
  scrollbar-width: thin;
  scrollbar-color: #e2e8f0 transparent;
}
</style>
