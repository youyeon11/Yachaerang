<template>
  <div class="message" :class="message.role">
    <img v-if="message.role === 'assistant'" class="avatar" src="@/assets/yachi.png" />

    <div class="bubble">{{ message.content }}</div>
  </div>
</template>

<script setup>
defineProps({
  message: Object,
});
</script>

<style scoped>
.message {
  display: flex;
  gap: 10px;
  margin: 16px 0;
  max-width: 90%;
}

.message.user {
  margin-left: auto;
  justify-content: flex-end;
}

.message.assistant {
  margin-right: auto;
  justify-content: flex-start;
}

.avatar {
  width: 40px;
  height: 40px;
}

.bubble {
  background: #f2f2f2;
  padding: 12px 18px;
  border-radius: 16px;
  line-height: 1.4;
}

.message.user .bubble {
  background: #fde594;
}
</style>
