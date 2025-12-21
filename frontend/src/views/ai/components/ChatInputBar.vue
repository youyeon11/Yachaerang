<template>
  <div class="flex items-end gap-2 bg-[#f8f9fa] border border-[#e9ecef] rounded-[15px] px-[15px] py-[10px]">
    <textarea
      ref="textareaRef"
      :value="modelValue"
      @input="onInput"
      @keydown.enter.exact.prevent="emitSend"
      :placeholder="placeholder"
      rows="1"
      class="flex-1 bg-transparent border-0 outline-none resize-none px-2 py-2 text-base"
    />

    <button
      type="button"
      @click="emitSend"
      class="w-10 h-10 rounded-[10px] bg-[#fecc21] text-[#212121] inline-flex items-center justify-center disabled:opacity-30"
      :disabled="disabled || !modelValue.trim()"
    >
      <IconSend class="w-[18px] h-[18px]" />
    </button>
  </div>
</template>

<script setup>
import IconSend from '@/components/icons/IconSend.vue';
import { ref, watch, nextTick } from 'vue';

const props = defineProps({
  modelValue: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  placeholder: { type: String, default: '메시지를 입력하세요...' },
});
const emit = defineEmits(['update:modelValue', 'send']);

const textareaRef = ref(null);

const adjustHeight = () => {
  const el = textareaRef.value;
  if (!el) return;
  el.style.height = 'auto';
  el.style.height = el.scrollHeight + 'px';
};

const onInput = (e) => {
  emit('update:modelValue', e.target.value);
  adjustHeight();
};

const emitSend = () => {
  if (props.disabled) return;
  emit('send');
};

watch(
  () => props.modelValue,
  async (val) => {
    if (val === '') {
      await nextTick();
      const el = textareaRef.value;
      if (el) el.style.height = 'auto';
    }
  }
);
</script>
