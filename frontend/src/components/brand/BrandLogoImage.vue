<template>
  <img
    v-show="!errored"
    :src="src"
    :alt="alt"
    :class="imgClass"
    @error="onError"
  />
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  src: String,
  alt: String,
  size: String,
  class: String
})

const errored = ref(false)

const sizeClassMap = {
  sm: 'h-8 w-8',
  md: 'h-auto w-[80px]',
  lg: 'h-auto w-[180px]'
}

const imgClass = computed(() => {
  const sizeClass = sizeClassMap[props.size] || sizeClassMap.md
  return props.class ? `${sizeClass} ${props.class}` : sizeClass
})

function onError() {
  errored.value = true
}
</script>