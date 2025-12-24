<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 w-full pt-4 pb-8">
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      
      <div v-if="farm" class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 h-fit">
        <h2 class="text-lg font-bold text-gray-800 mb-6 flex items-center gap-2">
          <span class="w-1 h-6 bg-[#F44323] rounded-full block"></span>
          농장 정보
        </h2>

        <div class="space-y-5">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">농장 위치</label>
            <div class="relative">
              <i class="fa-solid fa-location-dot absolute left-3 top-3.5 text-gray-400"></i>
              <div class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-700">
                {{ farm.location || '-' }}
              </div>
            </div>
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">주품목</label>
            <div class="relative">
              <i class="fa-solid fa-apple-whole absolute left-3 top-3.5 text-gray-400"></i>
              <div class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-700">
                {{ farm.mainCrop || '-' }}
              </div>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">경작면적</label>
              <div class="relative">
                <div class="w-full pl-4 pr-12 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-700">
                  {{ farm.cultivatedArea ? Number(farm.cultivatedArea).toLocaleString() : 0 }}
                </div>
                <span class="absolute right-4 top-3.5 text-gray-500 text-sm font-medium">㎡</span>
              </div>
            </div>
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">평지면적</label>
              <div class="relative">
                <div class="w-full pl-4 pr-12 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-700">
                  {{ farm.flatArea ? Number(farm.flatArea).toLocaleString() : 0 }}
                </div>
                <span class="absolute right-4 top-3.5 text-gray-500 text-sm font-medium">㎡</span>
              </div>
            </div>
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">농장 인력</label>
            <div class="flex items-center gap-4">
              <div class="w-10 h-10 rounded-full border border-gray-300 bg-gray-50 flex items-center justify-center text-gray-400">
                <i class="fa-solid fa-user"></i>
              </div>
              <span class="text-lg font-bold text-gray-800">{{ farm.manpower ?? 0 }}</span>
              <span class="text-gray-500">명</span>
            </div>
          </div>

          <button
            @click="$emit('edit')"
            class="w-full bg-[#F44323] text-white font-bold py-4 rounded-lg shadow-md hover:bg-[#d63a1f] transform active:scale-[0.98] transition-all flex items-center justify-center gap-2"
          >
            <i class="fa-solid fa-pen-to-square"></i>
            농장 정보 수정하기
          </button>
        </div>
      </div>

      <div v-else class="relative h-fit">
        <div class="blur-sm pointer-events-none select-none bg-white p-6 rounded-xl shadow-sm border border-gray-100 opacity-50">
          <h2 class="text-lg font-bold text-gray-800 mb-6 flex items-center gap-2">
            <span class="w-1 h-6 bg-[#F44323] rounded-full block"></span>
            농장 정보
          </h2>
          <div class="space-y-5">
             <div class="h-12 bg-gray-100 rounded-lg w-full"></div>
             <div class="h-12 bg-gray-100 rounded-lg w-full"></div>
             <div class="grid grid-cols-2 gap-5">
               <div class="h-12 bg-gray-100 rounded-lg w-full"></div>
               <div class="h-12 bg-gray-100 rounded-lg w-full"></div>
             </div>
          </div>
        </div>
        <div class="absolute inset-0 flex flex-col items-center justify-center rounded-xl z-10">
          <div class="w-16 h-16 bg-white rounded-full flex items-center justify-center mb-4 shadow-sm border border-gray-100">
            <i class="fa-solid fa-tractor text-2xl text-gray-400"></i>
          </div>
          <p class="text-lg font-bold text-gray-700 mb-1">농장 정보가 없습니다</p>
          <p class="text-sm text-gray-500 mb-5">정보를 등록하고 AI 진단을 받아보세요</p>
          <button
            @click="$emit('register')"
            class="bg-[#F44323] text-white font-bold px-6 py-3 rounded-lg shadow-md hover:bg-[#d63a1f] transition-all flex items-center gap-2"
          >
            <i class="fa-solid fa-plus"></i>
            농장 등록하기
          </button>
        </div>
      </div>

      <div v-if="farm && (farm.grade || farm.comment)" class="h-fit">
        
        <div class="bg-white p-6 pb-2 rounded-t-xl shadow-sm border border-gray-100 border-b-0 relative overflow-hidden z-10">
          <div class="absolute top-0 left-0 w-full h-2 bg-[#F44323]"></div>
          
          <div class="flex flex-col items-center justify-center pt-4">
            <p class="text-gray-500 font-medium mb-2 uppercase tracking-widest text-xs">Farm Grade</p>
            
            <div 
              :class="gradeCircleClass(farm.grade)"
              class="relative w-28 h-28 flex items-center justify-center rounded-full shadow-lg mb-3"
            >
              <span class="text-5xl font-black text-white drop-shadow-md">{{ farm.grade ?? '?' }}</span>
              <i class="fa-solid fa-star text-white text-lg absolute -top-1 -right-1"></i>
              <i class="fa-solid fa-star text-white text-lg absolute bottom-2 left-0"></i>
            </div>
            
            <h3 class="text-2xl font-bold text-gray-800">{{ gradeTitle(farm.grade) }}</h3>
            <p class="text-sm text-gray-400 mt-1">AI가 분석한 농장 종합 등급입니다</p>
          </div>
        </div>
        
        <div class="bg-white px-6 py-5 border-l border-r border-gray-100">
          
          <div class="w-full border-t border-dashed border-gray-200 mb-5"></div>

          <div class="flex flex-col">
            <p class="text-gray-400 font-bold mb-3 text-xs uppercase tracking-widest flex items-center gap-2">
              <i class="fa-solid fa-tag"></i> 농장 유형 분류
            </p>

            <div 
              class="rounded-xl p-4 border flex items-start gap-4 transition-all duration-300"
              :class="currentTypeConfig.color"
              :style="iconStyle"
            >
              <div 
                class="flex-shrink-0 w-12 h-12 rounded-lg flex items-center justify-center shadow-sm"
                :class="currentTypeConfig.iconBg"
              >
                <i class="fa-solid text-xl" :class="currentTypeConfig.icon"></i>
              </div>

              <div>
                <h4 class="font-bold text-base mb-1" :class="currentTypeConfig.textClass">
                  {{ currentTypeConfig.label }}
                </h4>
                <p class="text-sm opacity-90 leading-snug break-keep">
                  {{ currentTypeConfig.desc }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-amber-50 p-6 rounded-b-xl border border-gray-200 border-t-gray-100">
          <div class="flex gap-4">
            <div class="flex-shrink-0">
              <div class="w-10 h-10 rounded-full bg-[#F44323] text-white flex items-center justify-center shadow-sm">
                <i class="fa-solid fa-robot"></i>
              </div>
            </div>
            <div class="bg-white p-4 rounded-2xl rounded-tl-none shadow-sm border border-gray-100 flex-1">
              <p class="text-gray-700 text-sm leading-relaxed">
                <span class="font-bold text-[#F44323] block mb-1">AI 분석 리포트:</span> 
                {{ farm.comment ?? '평가 내용이 없습니다.' }}
              </p>
            </div>
          </div>
        </div>

      </div>

      <div 
        v-else
        class="flex flex-col items-center justify-center text-center h-full min-h-[400px] border-2 border-dashed border-gray-200 rounded-xl bg-gray-50/50"
      >
        <div class="w-20 h-20 bg-gray-100 rounded-full flex items-center justify-center mb-4">
          <i class="fa-solid fa-clipboard-list text-3xl text-gray-300"></i>
        </div>
        <h3 class="text-lg font-bold text-gray-400">
          {{ !farm ? '평가 대기 중' : '아직 평가가 없습니다' }}
        </h3>
        <p class="text-sm text-gray-400 max-w-xs mt-2 break-keep">
          {{ !farm ? '농장을 등록하면 AI 진단 결과를 확인할 수 있습니다.' : '농장 정보를 저장하면 AI가 자동으로 평가를 진행합니다.' }}
        </p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  farm: {
    type: Object,
    default: null,
  },
});

defineEmits(['edit', 'register']);

// 등급 관련 색상
const gradeCircleClass = (grade) => {
  const map = {
    'A': 'bg-gradient-to-br from-[#F44323] to-orange-500',
    'B': 'bg-gradient-to-br from-blue-500 to-cyan-500',
    'C': 'bg-gradient-to-br from-yellow-400 to-yellow-600',
    'D': 'bg-gradient-to-br from-gray-400 to-gray-600',
  };
  return map[grade] || 'bg-gray-300';
};

const gradeTitle = (grade) => {
  const map = {
    'A': 'Excellent',
    'B': 'Good',
    'C': 'Average',
    'D': 'Bad',
  };
  return map[grade] || 'Unknown';
};

// 농장 유형 정의 - #F44323 기반 색상 체계
const TYPE_DEFINITIONS = {
  // 효율형
  'Efficiency-Oriented': {
    label: '효율형 농장 (Efficiency)',
    desc: '평지 비율이 높고 노동력 활용이 효율적인 구조입니다.',
    icon: 'fa-chart-line',
    iconBg: 'bg-green-100',
    textClass: 'text-green-800',
    imageUrl: '@/assets/efficiency-oriented'
  },
  '효율형 농장': 'Efficiency-Oriented',

  // 노동집약형 - 오렌지 (#F44323과 유사한 계열)
  'Labor-Intensive': {
    label: '노동집약형 농장 (Labor)',
    desc: '지형적 불리함을 높은 노동 투입으로 극복하는 구조입니다.',
    icon: 'fa-users-gear',
    color: 'bg-orange-50 text-orange-700 border-orange-200',
    iconBg: 'bg-orange-100',
    textClass: 'text-orange-800'
  },
  '노동집약형 농장': 'Labor-Intensive',

  // 집중형 - 블루 (집중/전문성)
  'Focused': {
    label: '집중형 농장 (Focused)',
    desc: '특정 핵심 작물에 역량을 집중하는 전문화된 구조입니다.',
    icon: 'fa-bullseye',
    color: 'bg-blue-50 text-blue-700 border-blue-200',
    iconBg: 'bg-blue-100',
    textClass: 'text-blue-800'
  },
  '집중형 농장': 'Focused',

  // 실험형 - 퍼플 (창의성/실험)
  'Experimental': {
    label: '실험형 농장 (Experimental)',
    desc: '미래 방향성을 탐색하기 위해 다품종 소량 생산을 시도합니다.',
    icon: 'fa-flask',
    color: 'bg-purple-50 text-purple-700 border-purple-200',
    iconBg: 'bg-purple-100',
    textClass: 'text-purple-800'
  },
  '실험형 농장': 'Experimental',

  // 도시형 - 시안/틸 (현대성/도시)
  'Urban': {
    label: '도시형 농장 (Urban)',
    desc: '도심 인근에 위치하여 접근성과 유통에 강점이 있는 구조입니다.',
    icon: 'fa-city',
    color: 'bg-cyan-50 text-cyan-700 border-cyan-200',
    iconBg: 'bg-cyan-100',
    textClass: 'text-cyan-800'
  },
  '도시형 농장': 'Urban',

  // 전통형 - 앰버 (전통/안정)
  'Traditional': {
    label: '전통형 농장 (Traditional)',
    desc: '전체적인 운영 구조가 균형 잡혀 있고 안정적인 형태입니다.',
    icon: 'fa-tractor',
    color: 'bg-amber-50 text-amber-700 border-amber-200',
    iconBg: 'bg-amber-100',
    textClass: 'text-amber-800'
  },
  '전통형 농장': 'Traditional',

  // 개선 필요 - 레드 (#F44323 메인 컬러 계열)
  'Needs Improvement': {
    label: '개선 필요형 (Needs Improv.)',
    desc: '현재 운영상 부담이 있으나, 명확한 개선 잠재력을 가지고 있습니다.',
    icon: 'fa-hammer',
    color: 'bg-red-50 text-[#F44323] border-red-200',
    iconBg: 'bg-red-100',
    textClass: 'text-[#F44323]'
  },
  '개선 필요형': 'Needs Improvement'
};

const DEFAULT_TYPE_CONFIG = {
  label: '유형 미지정',
  desc: '아직 농장 유형이 분석되지 않았습니다.',
  icon: 'fa-circle-question',
  color: 'bg-gray-50 text-gray-600 border-gray-100',
  iconBg: 'bg-white',
  textClass: 'text-gray-700'
};

const currentTypeConfig = computed(() => ({
  iconBg: 'bg-emerald-500',           // 기존 프레임 배경
  imageUrl: '/images/farm.png',       // 넣고 싶은 이미지
  label: '농장',
  fallbackText: '🌱',
}))

const iconStyle = computed(() => {
  if (!currentTypeConfig.value.imageUrl) return {}
  return {
    backgroundImage: `url('${currentTypeConfig.value.imageUrl}')`,
  }
})

function onImgError(e) {
  // 이미지 깨질 때 대체 처리
  e.target.style.display = 'none'
}

const currentTypeConfig = computed(() => {
  if (!props.farm || !props.farm.farmType) return DEFAULT_TYPE_CONFIG;

  const typeKey = props.farm.farmType;
  const config = TYPE_DEFINITIONS[typeKey];

  if (typeof config === 'string') {
    // Alias인 경우 (예: '효율형 농장' -> 'Efficiency-Oriented' -> 객체)
    return TYPE_DEFINITIONS[config] || DEFAULT_TYPE_CONFIG;
  }
  
  return config || DEFAULT_TYPE_CONFIG;
});
</script>