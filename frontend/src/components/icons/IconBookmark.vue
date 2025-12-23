<template>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    viewBox="0 0 24 24"
    :fill="active ? '#F44323' : 'none'"
    :stroke="active ? '#F44323' : '#999'"
    stroke-width="2"
  >
    <path d="m19 21-7-4-7 4V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16z"></path>
  </svg>
</template>

<script setup>
defineProps({
  active: {
    type: Boolean,
    default: false,
  },
});
</script>


