<template>
  <div class="relative flex items-center group">
    <input
      type="text"
      v-model="localQuery"
      placeholder="관심 있는 키워드를 검색하세요"
      class="w-full md:w-80 px-5 py-3 pr-20 rounded-2xl border-2 border-gray-200 focus:border-[#FECC21] outline-none transition-all shadow-sm group-hover:shadow-md"
      @keyup.enter="emitSearch"
    />

    <button
      v-if="localQuery"
      type="button"
      @click="clearSearch"
      class="absolute right-11 p-1 rounded-full text-gray-400 hover:text-gray-600 transition-colors"
    >
      <IconX class="w-4 h-4" />
    </button>

    <button
      type="button"
      @click="emitSearch"
      class="absolute right-2 p-2 bg-[#FECC21] rounded-xl text-gray-800 hover:scale-110 transition-transform shadow-sm"
    >
      <IconSearch class="w-5 h-5" />
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import IconSearch from '@/components/icons/IconSearch.vue';
import IconX from '@/components/icons/IconX.vue';

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['update:modelValue', 'search', 'clear']);

const localQuery = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit('update:modelValue', value);
  },
});

const emitSearch = () => {
  emit('search');
};

const clearSearch = () => {
  emit('update:modelValue', '');
  emit('clear');
};
</script>
