<template>
  <main class="flex-1 overflow-y-auto p-8 bg-gray-50">
    <div class="mx-auto max-w-4xl space-y-6">
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-gray-900">My Page</h1>
        <p class="text-gray-600">Manage your account and preferences</p>
      </div>

      <!-- Profile Information -->
      <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div class="mb-4">
          <h2 class="text-lg font-semibold text-gray-900">Profile Information</h2>
          <p class="text-sm text-gray-600">View and edit your personal details</p>
        </div>
        <div class="space-y-6">
          <div class="flex items-center gap-6">
            <div class="flex h-24 w-24 items-center justify-center rounded-full bg-[#FECC21] text-2xl font-bold text-[#F44323]">
              {{ profile.name.charAt(0) }}
            </div>
            <button class="rounded-lg border border-gray-300 px-4 py-2 font-medium text-gray-700 transition-colors hover:bg-gray-50">
              Change Photo
            </button>
          </div>

          <div class="space-y-4">
            <div class="grid gap-4 md:grid-cols-2">
              <div class="space-y-2">
                <label for="name" class="text-sm font-medium text-gray-900">Name</label>
                <input
                  id="name"
                  v-model="profile.name"
                  :disabled="!isEditing"
                  class="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-[#F44323] focus:outline-none focus:ring-2 focus:ring-[#F44323]/20 disabled:bg-gray-50"
                />
              </div>

              <div class="space-y-2">
                <label for="nickname" class="text-sm font-medium text-gray-900">Nickname</label>
                <input
                  id="nickname"
                  v-model="profile.nickname"
                  :disabled="!isEditing"
                  class="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-[#F44323] focus:outline-none focus:ring-2 focus:ring-[#F44323]/20 disabled:bg-gray-50"
                />
              </div>
            </div>

            <div class="space-y-2">
              <label for="email" class="text-sm font-medium text-gray-900">Email Address</label>
              <input
                id="email"
                v-model="profile.email"
                type="email"
                :disabled="!isEditing"
                class="w-full rounded-lg border border-gray-300 px-3 py-2 focus:border-[#F44323] focus:outline-none focus:ring-2 focus:ring-[#F44323]/20 disabled:bg-gray-50"
              />
            </div>

            <div class="flex gap-2">
              <template v-if="isEditing">
                <button
                  @click="isEditing = false"
                  class="rounded-lg bg-[#F44323] px-4 py-2 font-medium text-white transition-colors hover:bg-[#d63a1f]"
                >
                  Save Changes
                </button>
                <button
                  @click="isEditing = false"
                  class="rounded-lg border border-gray-300 px-4 py-2 font-medium text-gray-700 transition-colors hover:bg-gray-50"
                >
                  Cancel
                </button>
              </template>
              <button
                v-else
                @click="isEditing = true"
                class="flex items-center gap-2 rounded-lg border border-gray-300 px-4 py-2 font-medium text-gray-700 transition-colors hover:bg-gray-50"
              >
                <IconEdit class="h-4 w-4" />
                Edit Information
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Account Actions -->
      <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div class="mb-4">
          <h2 class="text-lg font-semibold text-gray-900">Account Actions</h2>
          <p class="text-sm text-gray-600">Security and account management</p>
        </div>
        <div class="space-y-3">
          <button class="w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2 text-left font-medium text-gray-700 transition-colors hover:bg-gray-50">
            Change Password
          </button>
          <button class="flex w-full items-center gap-2 rounded-lg border border-gray-300 bg-transparent px-4 py-2 text-left font-medium text-red-500 transition-colors hover:bg-red-50">
            <IconLogout class="h-4 w-4" />
            Log Out
          </button>
        </div>
      </div>

      <!-- Favorites -->
      <div class="grid gap-6 md:grid-cols-2">
        <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
          <h2 class="mb-4 flex items-center gap-2 text-lg font-semibold text-gray-900">
            <IconHeart class="h-5 w-5 fill-[#F44323] text-[#F44323]" />
            Favorite Dashboards
          </h2>
          <div class="space-y-2">
            <div
              v-for="(dashboard, idx) in favoriteDashboards"
              :key="idx"
              class="rounded-lg border border-gray-200 p-3 text-sm transition-colors hover:bg-gray-50"
            >
              {{ dashboard }}
            </div>
          </div>
        </div>

        <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
          <h2 class="mb-4 flex items-center gap-2 text-lg font-semibold text-gray-900">
            <IconNewspaper class="h-5 w-5 text-[#F44323]" />
            Favorite Articles
          </h2>
          <div class="space-y-2">
            <div
              v-for="(article, idx) in favoriteArticles"
              :key="idx"
              class="rounded-lg border border-gray-200 p-3 text-sm transition-colors hover:bg-gray-50"
            >
              {{ article }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import IconHeart from '../../components/icons/IconHeart.vue'
import IconNewspaper from '../../components/icons/IconNewspaper.vue'
import IconEdit from '../../components/icons/IconEdit.vue'
import IconLogout from '../../components/icons/IconLogout.vue'

const isEditing = ref(false)
const profile = ref({
  name: 'Kim Min-soo',
  nickname: 'FarmKing',
  email: 'kimm@example.com',
})

const favoriteDashboards = ['Tomato Prices - Monthly', 'Rice Trends - Yearly', 'Beef Market - Weekly']
const favoriteArticles = ['Smart Farming Technology Trends in 2025', 'Understanding Market Price Fluctuations']
</script>
