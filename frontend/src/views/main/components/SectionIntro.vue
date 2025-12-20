<template>
  <section 
    class="section intro relative w-full overflow-hidden px-4 py-16" 
    ref="target" 
    :class="{ 'opacity-100 translate-y-0': isVisible, 'opacity-0 translate-y-10': !isVisible }"
  >
    <div class="mb-12 text-center transition-all duration-700 delay-100">
      <h1 class="text-4xl font-extrabold text-gray-900 md:text-5xl">
        <span class="text-[#F57C00]">야채랑</span>을 소개합니다
      </h1>
      <p class="mt-4 text-base text-gray-600 md:text-lg">
        농수산물 가격 제공 및 농업 도우미 플랫폼
      </p>
    </div>

    <div class="cards flex flex-wrap justify-center gap-6 perspective-container">
      
      <div 
        v-for="(card, index) in cards" 
        :key="index"
        class="group relative h-[280px] w-[200px] cursor-pointer [perspective:1000px]"
        @click="handleCardClick(card.route)"
      >
        <div class="relative h-full w-full transition-all duration-500 [transform-style:preserve-3d] group-hover:[transform:rotateY(180deg)] shadow-xl rounded-2xl">
          
          <div 
            class="absolute inset-0 flex flex-col items-center justify-center rounded-2xl bg-white p-6 text-center [backface-visibility:hidden]"
            :class="card.isGuide ? 'bg-gradient-to-br from-orange-50 to-white border-2 border-orange-100' : 'bg-gray-50'"
          >
            <div 
              class="mb-4 flex h-16 w-16 items-center justify-center rounded-full shadow-sm"
              :class="card.iconBg"
            >
              <component :is="card.icon" class="h-8 w-8 text-white" />
            </div>
            
            <h3 class="text-xl font-bold text-gray-800 group-hover:text-[#F57C00] transition-colors">
              {{ card.title }}
            </h3>
            
            <div class="mt-2 h-1 w-10 rounded-full" :class="card.barColor"></div>
          </div>

          <div 
            class="absolute inset-0 flex flex-col items-center justify-center rounded-2xl px-4 py-6 text-center text-white [transform:rotateY(180deg)] [backface-visibility:hidden]"
            :class="card.gradient"
          >
            <h4 class="mb-2 text-lg font-bold">{{ card.title }}</h4>
            <p class="mb-6 text-sm opacity-90 leading-relaxed">
              {{ card.description }}
            </p>
            
            <button class="rounded-full bg-white/20 px-6 py-2 text-sm font-semibold backdrop-blur-sm transition-colors hover:bg-white hover:text-gray-900 border border-white/30">
              바로가기 &rarr;
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useSectionObserver } from '@/views/auth/composables/useSectionObserver';

// Icons
import IconDashboard from '../../../components/icons/IconDashboard.vue'
import IconTrendingUp from '../../../components/icons/IconTrendingUp.vue'
import IconSprout from '../../../components/icons/IconSprout.vue'
import IconNewspaper from '../../../components/icons/IconNewspaper.vue'
import IconMessage from '../../../components/icons/IconMessage.vue'

const router = useRouter();
const target = ref(null);
const { isVisible, observe } = useSectionObserver();

// Card Data Configuration
const cards = [
  {
    title: '대시보드',
    description: '대시보드를 통해 원하는 날자에 대한 가격을 한 눈에 조회해요.',
    route: '/dashboard',
    icon: IconDashboard,
    iconBg: 'bg-[#E53935]',
    barColor: 'bg-[#E53935]',
    gradient: 'bg-gradient-to-br from-[#E53935] to-[#C62828]'
  },
  {
    title: '시세 랭킹',
    description: '전국적으로 어떤 농수산물의 가격이 높고 낮은지를 조회해보세요!',
    route: '/rank',
    icon: IconTrendingUp,
    iconBg: 'bg-[#F57C00]',
    barColor: 'bg-[#F57C00]',
    gradient: 'bg-gradient-to-br from-[#F57C00] to-[#E65100]'
  },
  {
    title: '나의 농장',
    description: '내 농장의 정보를 입력하고 피드백을 들어보세요!',
    route: '/myfarm',
    icon: IconSprout,
    iconBg: 'bg-[#FECC21]',
    barColor: 'bg-[#FECC21]',
    gradient: 'bg-gradient-to-br from-[#FECC21] to-[#F9A825]'
  },
  {
    title: '농업 소식',
    description: '다양한 플랫폼에서의 농촌 기사들과 새로운 지원 정책들을 확인해보세요!',
    route: '/articles',
    icon: IconNewspaper,
    iconBg: 'bg-[#FF8A65]',
    barColor: 'bg-[#FF8A65]',
    gradient: 'bg-gradient-to-br from-[#FF8A65] to-[#F4511E]'
  },
  {
    title: 'AI 챗봇',
    description: '챗봇 "야치"와 대화하며 판매 전략을 세워보세요!',
    route: '/ai-chat',
    icon: IconMessage,
    iconBg: 'bg-[#FFB74D]',
    barColor: 'bg-[#FFB74D]',
    gradient: 'bg-gradient-to-br from-[#FFB74D] to-[#FF9800]'
  }
];

const handleCardClick = (route) => {
  if (route) {
    router.push(route);
  }
};

onMounted(() => {
  if (target.value) {
    observe(target.value);
  }
});
</script>

<style scoped>
.section {
  transition: opacity 1s ease-out, transform 1s ease-out;
}
</style>