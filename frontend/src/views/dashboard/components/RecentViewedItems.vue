<template>
  <div class="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
    <div class="flex items-center justify-between mb-3">
      <h3 class="text-sm font-bold">최근 조회한 품목</h3>

      <button
        v-if="items.length"
        @click="$emit('clear')"
        class="text-gray-400 hover:text-red-500 transition-colors p-1"
        title="초기화"
      >
        <IconRefresh class="w-4 h-4" />
      </button>
    </div>

    <ul v-if="items.length" class="space-y-2 text-[11px]">
      <li
        v-for="item in items"
        :key="item.productCode"
        class="flex justify-between text-gray-600 cursor-pointer hover:text-gray-900"
        @click="$emit('select', item)"
      >
        <span>{{ item.itemLabel }} / {{ item.varietyLabel }}</span>
        <span class="text-gray-400 text-right flex flex-col items-end">
          <span>{{ periodMap[item.periodType] }}</span>
          <span v-if="item.dateRangeLabel" class="text-[10px]">
            {{ item.dateRangeLabel }}
          </span>
        </span>
      </li>
    </ul>

    <p v-else class="text-[11px] text-gray-400 italic">최근 조회한 품목이 없습니다.</p>
  </div>
</template>

<script>
import IconRefresh from '@/components/icons/IconRefresh.vue';

export default {
  name: 'RecentViewedItems',
  components: {
    IconRefresh,
  },
  emits: ['select', 'clear'],
  props: {
    items: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      periodMap: {
        day: '일간',
        week: '주간',
        month: '월간',
        year: '연간',
      },
    };
  },
};
</script>
