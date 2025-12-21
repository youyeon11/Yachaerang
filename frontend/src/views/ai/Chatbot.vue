<template>
  <main class="flex flex-1 flex-col p-8 bg-gray-50">
    <div class="mx-auto flex w-full max-w-4xl flex-1 flex-col space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight text-gray-900">Agricultural Assistant</h1>
          <p class="text-gray-600">Get personalized farming advice and market insights</p>
        </div>
        <button
          @click="handleNewChat"
          class="flex items-center gap-2 rounded-lg border border-gray-300 px-4 py-2 font-medium text-gray-700 transition-colors hover:bg-gray-50"
        >
          <IconRefresh class="h-4 w-4" />
          New Chat
        </button>
      </div>

      <!-- Chat Messages -->
      <div class="flex-1 rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div class="h-[500px] overflow-y-auto pr-4">
          <div class="space-y-4">
            <div
              v-for="(message, idx) in messages"
              :key="idx"
              class="flex gap-3"
              :class="message.role === 'user' ? 'justify-end' : 'justify-start'"
            >
              <div v-if="message.role === 'assistant'" class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-[#FECC21]">
                <svg class="h-6 w-6 text-[#F44323]" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M10 2a6 6 0 00-6 6v3.586l-.707.707A1 1 0 004 14h12a1 1 0 00.707-1.707L16 11.586V8a6 6 0 00-6-6zM10 18a3 3 0 01-3-3h6a3 3 0 01-3 3z" />
                </svg>
              </div>
              <div
                class="max-w-[70%] rounded-lg p-4"
                :class="message.role === 'user' ? 'bg-[#F44323] text-white' : 'bg-gray-100 text-gray-900'"
              >
                <p class="leading-relaxed">{{ message.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
        <div class="flex gap-2">
          <input
            v-model="input"
            @keypress.enter="handleSend"
            placeholder="Ask about farming techniques, market prices, or best practices..."
            class="flex-1 rounded-lg border border-gray-300 px-4 py-2 focus:border-[#F44323] focus:outline-none focus:ring-2 focus:ring-[#F44323]/20"
          />
          <button
            @click="handleSend"
            class="flex items-center justify-center rounded-lg bg-[#F44323] px-4 py-2 text-white transition-colors hover:bg-[#d63a1f]"
          >
            <IconSend class="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import IconRefresh from '../../components/icons/IconRefresh.vue'
import IconSend from '../../components/icons/IconSend.vue'

const messages = ref([
  {
    role: 'assistant',
    content: "Hello! I'm your agricultural assistant. How can I help you today? You can ask me about crop prices, farming techniques, market trends, or any other agricultural questions.",
  },
])

const input = ref('')

const handleSend = () => {
  if (!input.value.trim()) return

  messages.value.push({ role: 'user', content: input.value })

  setTimeout(() => {
    messages.value.push({
      role: 'assistant',
      content: 'Thank you for your question. Based on current market data and agricultural best practices, I recommend monitoring seasonal trends and consulting with local agricultural experts for personalized advice.',
    })
  }, 1000)

  input.value = ''
}

const handleNewChat = () => {
  messages.value = [
    {
      role: 'assistant',
      content: "Hello! I'm your agricultural assistant. How can I help you today? You can ask me about crop prices, farming techniques, market trends, or any other agricultural questions.",
    },
  ]
}
</script>