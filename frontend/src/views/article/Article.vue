<template>
  <main class="flex-1 overflow-y-auto p-8 bg-gray-50">
    <div class="mx-auto max-w-5xl space-y-6">
      <!-- 헤더 -->
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold tracking-tight text-gray-900">기사</h1>
          <p class="text-gray-600">한눈에 확인하는, 야채랑 PICK 농촌 기사</p>
        </div>

        <!-- 검색창 -->
        <div class="flex border-2 border-[#FECC21] rounded-lg overflow-hidden">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="검색어를 입력하세요."
            class="w-60 px-4 py-2.5 text-sm border-none outline-none"
            @keyup.enter="handleSearch"
          />
          <button 
            @click="handleSearch"
            class="px-4 py-2.5 bg-[#FECC21] border-none cursor-pointer flex items-center justify-center text-gray-800 hover:bg-[#e5b800] transition-colors"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <path d="m21 21-4.3-4.3"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- 기사 목록 -->
      <div class="space-y-4">
        <div
          v-for="article in articles"
          :key="article.id"
          @click="goToDetail(article.id)"
          class="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm transition-shadow hover:shadow-lg cursor-pointer"
        >
          <div class="flex flex-col md:flex-row">
            <img
              :src="article.thumbnail"
              :alt="article.title"
              class="h-48 w-full object-cover md:w-64"
            />
            <div class="flex-1 p-6">
              <h3 class="mb-2 text-xl font-semibold text-gray-900">{{ article.title }}</h3>
              
              <!-- 태그 -->
              <div v-if="article.tags && article.tags.length" class="flex gap-2 flex-wrap mb-2">
                <span 
                  v-for="tag in article.tags" 
                  :key="tag" 
                  class="text-sm text-[#F44323]"
                >
                  #{{ tag }}
                </span>
              </div>

              <p class="mb-4 text-sm text-gray-500">{{ formatDate(article.date) }}</p>
              
              <button 
                @click.stop="goToDetail(article.id)"
                class="rounded-lg border-2 border-[#F44323] bg-transparent px-4 py-2 font-medium text-[#F44323] transition-colors hover:bg-[#F44323] hover:text-white"
              >
                자세히 보기
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex items-center justify-center gap-2">
        <!-- 처음 버튼 -->
        <button
          @click="goToPage(1)"
          :disabled="currentPage === 1"
          class="px-3 h-10 rounded-lg border border-gray-300 text-sm text-gray-600 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          처음
        </button>

        <!-- 이전 버튼 -->
        <button
          @click="goToPage(currentPage - 1)"
          :disabled="currentPage === 1"
          class="flex h-10 w-10 items-center justify-center rounded-lg border border-gray-300 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m15 18-6-6 6-6"></path>
          </svg>
        </button>

        <!-- 페이지 번호들 -->
        <button
          v-for="page in visiblePages"
          :key="page"
          @click="goToPage(page)"
          class="h-10 rounded-lg px-4 font-medium transition-colors"
          :class="currentPage === page 
            ? 'bg-[#F44323] text-white' 
            : 'border border-gray-300 text-gray-700 hover:bg-gray-50'"
        >
          {{ page }}
        </button>

        <!-- 다음 버튼 -->
        <button
          @click="goToPage(currentPage + 1)"
          :disabled="currentPage === totalPages"
          class="flex h-10 w-10 items-center justify-center rounded-lg border border-gray-300 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m9 18 6-6-6-6"></path>
          </svg>
        </button>

        <!-- 마지막 버튼 -->
        <button
          @click="goToPage(totalPages)"
          :disabled="currentPage === totalPages"
          class="px-3 h-10 rounded-lg border border-gray-300 text-sm text-gray-600 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
        >
          마지막
        </button>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchArticles } from '@/api/article';

const router = useRouter();
const searchQuery = ref('');
const currentPage = ref(1);
const itemsPerPage = 5;

const articles = ref([]);
const totalPages = ref(1);

const visiblePages = computed(() => {
  const pages = [];
  const start = Math.max(1, currentPage.value - 2);
  const end = Math.min(totalPages.value, start + 4);
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});

const loadArticles = async (page = 1) => {
  try {
    const response = await fetchArticles({
      page,
      size: itemsPerPage,
    });

    const data = response.data?.data;

    if (data) {
      currentPage.value = data.currentPage ?? page;
      totalPages.value = data.totalPages ?? 1;

      articles.value = (data.content || []).map((item) => ({
        id: item.articleId,
        title: item.title,
        tags: item.tagList || [],
        date: item.createdAt,
        thumbnail: item.imageUrl,
        bookmarked: false,
      }));
    }
  } catch (error) {
    console.error('농촌 기사 목록 조회 실패:', error);
  }
};

onMounted(() => {
  loadArticles();
});

const handleSearch = () => {
  console.log('검색:', searchQuery.value);
  // TODO: 검색 API 호출
};

const goToDetail = (id) => {
  router.push(`/articles/${id}`);
};

const toggleBookmark = (id) => {
  const article = articles.value.find((a) => a.id === id);
  if (article) {
    article.bookmarked = !article.bookmarked;
  }
};

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    loadArticles(page);
  }
};

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('ko-KR');
};
</script>