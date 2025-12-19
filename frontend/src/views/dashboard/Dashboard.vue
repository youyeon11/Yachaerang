<template>
  <main class="flex-1 overflow-y-auto p-8 bg-gray-50">
    <div class="mx-auto max-w-7xl space-y-6">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-bold tracking-tight text-gray-900">Dashboard</h1>
          <p class="text-gray-600">Track agricultural product prices over time</p>
        </div>
      </div>

      <!-- Filters -->
      <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div class="mb-4">
          <h2 class="text-lg font-semibold text-gray-900">Select Product & Time Range</h2>
          <p class="text-sm text-gray-600">Choose category, sub-category, and time period to view price trends</p>
        </div>
        <div class="grid gap-4 md:grid-cols-4">
          <select v-model="category" class="rounded-lg border border-gray-300 px-3 py-2 focus:border-[#F44323] focus:outline-none focus:ring-2 focus:ring-[#F44323]/20">
            <option value="">Main Category</option>
            <option value="vegetables">Vegetables</option>
            <option value="fruits">Fruits</option>
            <option value="grains">Grains</option>
            <option value="livestock">Livestock</option>
          </select>

          <select v-model="subCategory" class="rounded-lg border border-gray-300 px-3 py-2 focus:border-[#F44323] focus:outline-none focus:ring-2 focus:ring-[#F44323]/20">
            <option value="">Sub-Category</option>
            <option value="tomatoes">Tomatoes</option>
            <option value="potatoes">Potatoes</option>
            <option value="carrots">Carrots</option>
            <option value="cabbage">Cabbage</option>
          </select>

          <select v-model="timeRange" class="rounded-lg border border-gray-300 px-3 py-2 focus:border-[#F44323] focus:outline-none focus:ring-2 focus:ring-[#F44323]/20">
            <option value="">Time Range</option>
            <option value="daily">Daily</option>
            <option value="weekly">Weekly</option>
            <option value="monthly">Monthly</option>
            <option value="yearly">Yearly</option>
          </select>

          <button
            @click="isLiked = !isLiked"
            class="flex items-center justify-center gap-2 rounded-lg border px-3 py-2 font-medium transition-colors"
            :class="isLiked ? 'border-[#F44323] text-[#F44323]' : 'border-gray-300 text-gray-700 hover:bg-gray-50'"
          >
            <IconHeart class="h-4 w-4" :class="isLiked ? 'fill-[#F44323]' : 'fill-none'" />
            {{ isLiked ? 'Liked' : 'Add to Favorites' }}
          </button>
        </div>
      </div>

      <!-- Chart -->
      <div class="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        <div class="mb-4">
          <h2 class="text-lg font-semibold text-gray-900">Price Trends</h2>
          <p class="text-sm text-gray-600">Combined line and bar chart showing price movements</p>
        </div>
        <div class="h-96">
          <canvas ref="chartCanvas"></canvas>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Chart from 'chart.js/auto'
import IconHeart from '../../components/icons/IconHeart.vue'

const category = ref('')
const subCategory = ref('')
const timeRange = ref('')
const isLiked = ref(false)
const chartCanvas = ref(null)

const mockData = [
  { date: 'Jan', price: 4000 },
  { date: 'Feb', price: 3000 },
  { date: 'Mar', price: 5000 },
  { date: 'Apr', price: 4500 },
  { date: 'May', price: 6000 },
  { date: 'Jun', price: 5500 },
]

onMounted(() => {
  new Chart(chartCanvas.value, {
    type: 'bar',
    data: {
      labels: mockData.map(d => d.date),
      datasets: [
        {
          type: 'bar',
          label: 'Price (Bar)',
          data: mockData.map(d => d.price),
          backgroundColor: '#F44323',
          borderRadius: 4,
        },
        {
          type: 'line',
          label: 'Price (Line)',
          data: mockData.map(d => d.price),
          borderColor: '#FECC21',
          backgroundColor: '#FECC21',
          borderWidth: 3,
          pointRadius: 4,
          pointBackgroundColor: '#FECC21',
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'top',
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Price (₩)'
          }
        }
      }
    }
  })
})
</script>
