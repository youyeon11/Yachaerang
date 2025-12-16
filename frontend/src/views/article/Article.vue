<template>
  <div class="page-container article-page">
    <header class="page-header">
      <div class="page-header-left">
        <h1 class="page-title">기사</h1>
        <p class="page-subtitle">한눈에 확인하는, 야채랑 PICK 농촌 기사</p>
      </div>

      <div class="page-header-right">
        <div class="search-box">
          <input
            type="text"
            v-model="searchQuery"
            placeholder="검색어를 입력하세요."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="11" cy="11" r="8"></circle>
              <path d="m21 21-4.3-4.3"></path>
            </svg>
          </button>
        </div>
      </div>
    </header>

    <div class="article-list">
      <div v-for="article in articles" :key="article.id" class="article-item" @click="goToDetail(article.id)">
        <div class="article-thumbnail">
          <img :src="article.thumbnail" :alt="article.title" />
        </div>
        <div class="article-content">
          <h3 class="article-title">{{ article.title }}</h3>
          <div class="article-tags" v-if="article.tags && article.tags.length">
            <span v-for="tag in article.tags" :key="tag" class="tag">#{{ tag }}</span>
          </div>
          <div class="article-meta">
            <span class="date">{{ article.date }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="pagination">
      <button class="page-btn nav" :disabled="currentPage === 1" @click="goToPage(1)">처음</button>
      <button class="page-btn nav" :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="m15 18-6-6 6-6"></path>
        </svg>
      </button>
      <button
        v-for="page in visiblePages"
        :key="page"
        class="page-btn"
        :class="{ active: page === currentPage }"
        @click="goToPage(page)"
      >
        {{ page }}
      </button>
      <button class="page-btn nav" :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <path d="m9 18 6-6-6-6"></path>
        </svg>
      </button>
      <button class="page-btn nav" :disabled="currentPage === totalPages" @click="goToPage(totalPages)">마지막</button>
    </div>
  </div>
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
      // TODO: 검색 파라미터가 정의되면 여기에 추가 (예: keyword: searchQuery.value)
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
</script>

<style scoped>
.article-page {
  max-width: 900px;
  margin: 0 auto;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.search-box {
  display: flex;
  border: 2px solid #fecc21;
  border-radius: 8px;
  overflow: hidden;
}

.search-input {
  width: 240px;
  padding: 10px 14px;
  font-size: 14px;
  border: none;
  outline: none;
}

.search-input::placeholder {
  color: #999;
}

.search-btn {
  padding: 10px 14px;
  background-color: #fecc21;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
}

.search-btn:hover {
  background-color: #e5a030;
}

.article-list {
  display: flex;
  flex-direction: column;
}

.article-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
  border-top: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.article-item:hover {
  background-color: #fafafa;
}

.article-item:last-child {
  border-bottom: 1px solid #eee;
}

.article-thumbnail {
  width: 120px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.article-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.article-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.article-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tag {
  font-size: 12px;
  color: #666;
}

.article-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.bookmark-btn {
  padding: 8px;
  background: none;
  border: none;
  cursor: pointer;
  color: #ccc;
  transition: color 0.2s;
}

.bookmark-btn:hover {
  color: #f5b041;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  margin-top: 40px;
}

.page-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 12px;
  border: none;
  border-radius: 50%;
  background: none;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background-color: #f5f5f5;
}

.page-btn.active {
  background-color: #333;
  color: #fff;
}

.page-btn.nav {
  border-radius: 8px;
  font-size: 13px;
  color: #999;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
