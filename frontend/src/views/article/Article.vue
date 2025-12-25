<template>
  <main class="min-h-screen bg-gray-50 text-[#1f2937] font-sans">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 pt-4 md:pt-6 w-full">
      <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 md:gap-6">
        <PageHeader title="농촌 기사" description="야채랑 PICK이 엄선한 농촌 소식을 만나보세요." />

        <div class="w-full md:w-auto md:flex-shrink-0">
          <ArticleSearchBar v-model="searchQuery" @search="handleSearch" @clear="handleClearSearch" />
        </div>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 sm:px-6 w-full mt-2">
      <div class="flex-1 min-w-0 w-full">
        <div class="flex justify-end mb-6 pb-4">
          <div class="text-[13px] text-gray-400 font-medium">
            전체 <span class="text-gray-600 font-semibold">{{ totalElements }}</span
            >개
          </div>
        </div>

        <div v-if="articles.length">
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
            <ArticleCard
              v-for="article in articles"
              :key="article.id"
              :article="article"
              @open="goToDetail"
              @search-by-keyword="handleSearchByKeyword"
            />
          </div>

          <div class="mt-16 pb-16">
            <Pagination :current-page="currentPage" :total-pages="totalPages" @change-page="goToPage" />
          </div>
        </div>

        <ArticleEmptyState v-else :keyword="searchQuery" @reset="handleResetSearch" class="pb-16" />
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { fetchArticles, searchArticles } from '@/api/article';
import ArticleSearchBar from '@/views/article/components/ArticleSearchBar.vue';
import ArticleCard from '@/views/article/components/ArticleCard.vue';
import Pagination from '@/components/common/Pagination.vue';
import ArticleEmptyState from '@/views/article/components/ArticleEmptyState.vue';
import PageHeader from '@/components/layout/PageHeader.vue';

const router = useRouter();
const route = useRoute();
const searchQuery = ref('');
const currentPage = ref(1);
const totalPages = ref(1);
const totalElements = ref(0);
const articles = ref([]);

const loadArticles = async (page = 1, keyword = '') => {
  try {
    const response = keyword
      ? await searchArticles({ page, size: 6, keyword })
      : await fetchArticles({ page, size: 6 });

    const data = response.data?.data;
    if (data) {
      currentPage.value = data.currentPage;
      totalPages.value = data.totalPages;
      totalElements.value = data.totalElements || 0;

      articles.value = (data.content || []).map((item) => ({
        id: item.articleId,
        title: item.title,
        tags: item.tagList || [],
        date: item.createdAt,
        thumbnail: item.imageUrl,
        isBookmarked: item.isBookmarked ?? false,
      }));
    }
  } catch (error) {
    console.error('데이터 로드 실패:', error);
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadArticles(1, searchQuery.value);
};

const handleSearchByKeyword = (keyword) => {
  searchQuery.value = keyword;
  currentPage.value = 1;
  loadArticles(1, keyword);
};

const resetToAll = () => {
  searchQuery.value = '';
  currentPage.value = 1;
  loadArticles(1);
};

const handleClearSearch = () => resetToAll();
const handleResetSearch = () => resetToAll();

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    loadArticles(page, searchQuery.value);
  }
};

const goToDetail = (id) => router.push(`/articles/${id}`);

onMounted(() => {
  const keyword = route.query.keyword;
  if (keyword) {
    searchQuery.value = keyword;
    loadArticles(1, keyword);
  } else {
    loadArticles();
  }
});
</script>
