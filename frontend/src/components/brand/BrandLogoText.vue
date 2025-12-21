<template>
  <span :class="spanClass">
    <slot>{{ text }}</slot>
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  text: {
    type: String,
    default: '야채랑'
  },
  size: {
    type: String,
    default: 'sm'
  },
  class: String
})

const sizeClassMap = {
  sm: 'h-6 text-[1.4rem] leading-[1.5rem] tracking-[0.02em]',
  md: 'h-8 text-[1.85rem] leading-[2rem] tracking-[0.02em]',
  lg: 'h-10 text-[2.3rem] leading-[2.5rem] tracking-[0.025em]'
}

const spanClass = computed(() => {
  const base = `flex items-center font-bold text-gray-900 ${sizeClassMap[props.size]}`
  return props.class ? `${base} ${props.class}` : base
})
</script>