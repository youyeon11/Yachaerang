<template>
  <div class="messages">
    <ChatMessage v-for="msg in messages" :key="msg.id" :message="msg" />
  </div>
</template>

<script setup>
import ChatMessage from './ChatMessage.vue';

defineProps({ messages: Array });
</script>

<style scoped>
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 0;
}
</style>
