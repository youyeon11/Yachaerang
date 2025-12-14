<template>
  <div class="message" :class="message.role">
    <img v-if="message.role === 'assistant'" class="avatar" src="@/assets/yachi.png" />

    <div v-if="message.role === 'assistant'" class="bubble markdown-body" v-html="renderedContent" />
    <div v-else class="bubble">
      {{ message.content }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { marked } from 'marked';

const props = defineProps({
  message: {
    type: Object,
    required: true,
  },
});

const renderedContent = computed(() => marked.parse(props.message?.content ?? '', { breaks: true }));
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

.markdown-body {
  max-width: 100%;
}

.markdown-body p {
  margin: 0 0 4px;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 20px;
  margin: 4px 0;
}

.markdown-body code {
  background: rgba(0, 0, 0, 0.04);
  padding: 2px 4px;
  border-radius: 4px;
  font-size: 12px;
}

.markdown-body pre {
  background: rgba(0, 0, 0, 0.04);
  padding: 8px 10px;
  border-radius: 8px;
  overflow-x: auto;
  font-size: 12px;
}
</style>
