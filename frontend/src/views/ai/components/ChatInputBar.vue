<template>
  <div class="input-wrapper">
    <button v-if="messages.length > 0" class="reset-btn" @click="emit('reset')">
      <span class="icon">↺</span>
      <span class="label">새 채팅</span>
    </button>

    <input
      v-model="text"
      class="input-box"
      type="text"
      placeholder="궁금한 점을 야치에게 질문해주세요!"
      @keyup.enter="send"
    />

    <button class="send-btn" @click="send">➤</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  messages: Array,
});

const emit = defineEmits(['send', 'reset']);

const text = ref('');

function send() {
  if (!text.value.trim()) return;
  emit('send', text.value);
  text.value = '';
}
</script>

<style scoped>
.input-wrapper {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 40px);
  max-width: 720px;

  display: flex;
  align-items: center;
  gap: 12px;

  background: #f5f5f5;
  padding: 12px 18px;
  border-radius: 30px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);

  z-index: 999;
}

.input-box {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 16px;
  outline: none;
}

.send-btn {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: none;
  background: #fecc21;
  color: white;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
}

.reset-btn {
  position: relative;
  height: 36px;
  padding: 0 10px;
  border: none;
  background: #e9e9e9;
  border-radius: 20px;
  cursor: pointer;

  display: flex;
  align-items: center;
  gap: 6px;

  font-size: 15px;

  transition: all 0.25s ease;
  overflow: hidden;
  white-space: nowrap;

  width: 36px;
}
.reset-btn:hover {
  width: 110px;
  background: #dcdcdc;
}
.icon {
  font-size: 18px;
  flex-shrink: 0;
}
.label {
  opacity: 0;
  width: 0;
  overflow: hidden;
  padding: 0;
  transform: translateX(-6px);
  transition: 0.25s ease;
}
.reset-btn:hover .label {
  opacity: 1;
  width: auto;
  padding-left: 4px;
  transform: translateX(0);
}
</style>
