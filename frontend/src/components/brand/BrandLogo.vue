<template>
  <div class="flex items-center select-none">
    <BrandLogoImage :src="src" :alt="alt" :size="size" />
    <BrandLogoText :size="size" class="ml-2">
      {{ text }}
    </BrandLogoText>
  </div>
</template>

<script setup>
import BrandLogoImage from './BrandLogoImage.vue'
import BrandLogoText from './BrandLogoText.vue'

const props = defineProps({
  src: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: 'Logo'
  },
  text: {
    type: String,
    default: '야채랑'
  },
  size: {
    type: String,
    default: 'md'
  }
})
</script>