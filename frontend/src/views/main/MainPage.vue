<template>
  <div 
    class="relative h-screen w-full bg-gray-50 focus:outline-none overflow-hidden lg:overflow-hidden"
    :class="{ 'overflow-y-auto snap-y snap-mandatory': isMobile }"
    tabindex="0"
    @keydown="handleKeydown"
  >
    
    <nav class="fixed right-8 top-1/2 z-50 flex -translate-y-1/2 flex-col gap-4">
      <button
        v-for="(label, index) in sectionLabels"
        :key="index"
        @click="scrollToSection(index)"
        class="group relative flex items-center justify-end"
        :aria-label="`Scroll to ${label}`"
      >
        <span class="mr-4 opacity-0 transition-opacity duration-300 group-hover:opacity-100 bg-black/70 text-white text-xs px-2 py-1 rounded">
          {{ label }}
        </span>
        <div
          class="h-3 w-3 rounded-full transition-all duration-500 shadow-sm border"
          :class="[
            navigationStore.activeSectionIndex === index
              ? [
                  'scale-150 border-transparent',
                  index === 0 ? 'bg-white border-[#E53935]' :
                  index === 1 ? 'bg-[#FFEBEE]' :
                  index === 2 ? 'bg-[#FFCDD2]' :
                  index === 3 ? 'bg-[#EF9A9A]' :
                  index === 4 ? 'bg-[#EF5350]' :
                  'bg-[#E53935]'
                ]
              : 'bg-white hover:bg-[#FFEBEE] border-gray-300'
          ]"
        ></div>
      </button>
    </nav>

    <div 
      class="h-full w-full transition-transform duration-1000 ease-in-out will-change-transform"
      :style="desktopTransformStyle"
    >
      <div class="h-screen w-full lg:snap-start shrink-0 overflow-hidden relative">
        <div class="flex min-h-screen items-center justify-center">
          <SectionIntro />
        </div>
      </div>
      <div class="h-screen w-full lg:snap-start shrink-0 overflow-hidden relative">
        <div class="flex min-h-screen items-center justify-center">
          <SectionDashboard />
        </div>
      </div>
      <div class="h-screen w-full lg:snap-start shrink-0 overflow-hidden relative">
        <div class="flex min-h-screen items-center justify-center">
          <SectionRanking />
        </div>
      </div>
      <div class="h-screen w-full lg:snap-start shrink-0 overflow-hidden relative">
        <div class="flex min-h-screen items-center justify-center">
          <SectionMyFarm />
        </div>
      </div>
      <div class="h-screen w-full lg:snap-start shrink-0 overflow-hidden relative">
        <div class="flex min-h-screen items-center justify-center">
          <SectionNews />
        </div>
      </div>
      <div class="h-screen w-full lg:snap-start shrink-0 overflow-hidden relative">
        <div class="flex min-h-screen items-center justify-center">
          <SectionAI />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useNavigationStore } from '@/stores/navigation';

// Import components
import SectionIntro from '@/views/main/components/SectionIntro.vue';
import SectionAI from '@/views/main/components/SectionAI.vue';
import SectionDashboard from '@/views/main/components/SectionDashboard.vue';
import SectionNews from '@/views/main/components/SectionNews.vue';
import SectionMyFarm from '@/views/main/components/SectionMyFarm.vue';
import SectionRanking from '@/views/main/components/SectionRanking.vue';

// Setup Store
const navigationStore = useNavigationStore();

// UI Data
const sectionLabels = ['야채랑에 대해 소개할게요', '대시보드', '최근 농촌 시세', '나의 농장', '한 눈에 보는 농촌 트렌드', '도우미봇 야치'];

// Responsive Check
const isMobile = ref(false);

// --- Desktop Slider Logic (Transform) ---
const desktopTransformStyle = computed(() => {
  if (isMobile.value) return {}; // Disable transform on mobile
  return { transform: `translateY(-${navigationStore.activeSectionIndex * 100}%)` };
});

const changeSection = (direction) => {
  if (navigationStore.isScrolling) return;

  const nextIndex = navigationStore.activeSectionIndex + direction;

  // Boundary checks
  const totalSections = sectionLabels.length;
  if (nextIndex >= 0 && nextIndex < totalSections) {
    lockScroll();
    navigationStore.setActiveSection(nextIndex);
  }
};

const scrollToSection = (index) => {
  if (navigationStore.isScrolling || index === navigationStore.activeSectionIndex) return;
  lockScroll();
  navigationStore.setActiveSection(index);
};

// --- Scroll Lock / Debounce ---
const lockScroll = () => {
  navigationStore.setScrolling(true);
  setTimeout(() => {
    navigationStore.setScrolling(false);
  }, 1000);
};

// --- Event Handlers ---
const handleWheel = (e) => {
  if (isMobile.value) return;

  e.preventDefault();

  if (Math.abs(e.deltaY) < 15) return;

  if (!navigationStore.isScrolling) {
    const direction = e.deltaY > 0 ? 1 : -1;
    changeSection(direction);
  }
};

const handleKeydown = (e) => {
  // Arrow keys support
  if (['ArrowDown', 'ArrowUp'].includes(e.key)) {
    e.preventDefault();
    const direction = e.key === 'ArrowDown' ? 1 : -1;
    changeSection(direction);
  }
};

const checkMobile = () => {
  isMobile.value = window.innerWidth < 1024;
};

// --- Lifecycle Hooks ---
onMounted(() => {
  // Initial check
  checkMobile();
  window.addEventListener('resize', checkMobile);
  window.addEventListener('wheel', handleWheel, { passive: false });
  window.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile);
  window.removeEventListener('wheel', handleWheel);
  window.removeEventListener('keydown', handleKeydown);
});
</script>

<style scoped>
div {
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}
div::-webkit-scrollbar {
  display: none; /* Chrome/Safari */
}
</style>