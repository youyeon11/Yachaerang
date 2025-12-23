<template>
  <main class="flex-1 overflow-y-auto p-8 bg-gray-50">
    <div class="mx-auto max-w-5xl space-y-8">
      <div class="flex flex-col md:flex-row md:items-end md:justify-between gap-6">
        <div>
          <h1 class="text-4xl font-extrabold tracking-tight text-gray-900">기사</h1>
          <p class="mt-2 text-lg text-gray-600">야채랑 PICK이 엄선한 농촌 소식을 만나보세요.</p>
        </div>

        <ArticleSearchBar
          v-model="searchQuery"
          @search="handleSearch"
          @clear="handleClearSearch"
        />
      </div>

      <div v-if="articles.length" class="grid gap-6">
        <ArticleCard
          v-for="article in articles"
          :key="article.id"
          :article="article"
          @open="goToDetail"
          @toggle-bookmark="handleToggleBookmark"
        />
      </div>

      <ArticleEmptyState
        v-else
        :keyword="searchQuery"
        @reset="handleResetSearch"
      />

      <ArticlePagination
        :current-page="currentPage"
        :total-pages="totalPages"
        @change-page="goToPage"
      />
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { fetchArticles, searchArticles } from '@/api/article';
import { useArticle } from '@/views/article/composables/useArticles';
import ArticleSearchBar from '@/views/article/components/ArticleSearchBar.vue';
import ArticleCard from '@/views/article/components/ArticleCard.vue';
import ArticlePagination from '@/views/article/components/ArticlePagination.vue';
import ArticleEmptyState from '@/views/article/components/ArticleEmptyState.vue';

const router = useRouter();
const searchQuery = ref('');
const currentPage = ref(1);
const totalPages = ref(1);
const articles = ref([]);

const { toggleBookmarkAction } = useArticle();

const loadArticles = async (page = 1, keyword = '') => {
  try {
    const response = keyword
      ? await searchArticles({ page, size: 5, keyword })
      : await fetchArticles({ page, size: 5 });

    const data = response.data?.data;
    if (data) {
      currentPage.value = data.currentPage;
      totalPages.value = data.totalPages;
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
    console.error('데이터 로드 실패:', error);
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  loadArticles(1, searchQuery.value);
};

const resetToAll = () => {
  searchQuery.value = '';
  currentPage.value = 1;
  loadArticles(1);
};

const handleClearSearch = () => {
  resetToAll();
};

const handleResetSearch = () => {
  resetToAll();
};

const handleToggleBookmark = (article) => {
  toggleBookmarkAction(article);
};

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    loadArticles(page, searchQuery.value);
  }
};

const goToDetail = (id) => router.push(`/articles/${id}`);

onMounted(() => loadArticles());
</script>
