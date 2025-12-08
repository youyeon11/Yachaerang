<template>
  <div class="article-page">
    <!-- 헤더 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">기사</h1>
        <p class="page-subtitle">한눈에 확인하는, 야채랑 PICK 농촌 기사</p>
      </div>
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
        <button class="bookmark-btn" @click.stop="toggleBookmark(article.id)">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            :fill="article.bookmarked ? '#f5b041' : 'none'"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="m19 21-7-4-7 4V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16z"></path>
          </svg>
        </button>
      </div>
    </div>

    <!-- 페이지네이션 -->
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
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const searchQuery = ref('');
const currentPage = ref(1);
const itemsPerPage = 5;

// 임시 데이터
const articles = ref([
  {
    id: 1,
    title: '[환경] 두더지가 나타났다!!',
    tags: ['농촌', '여성', '청년', '시골'],
    date: '2025.11.25 01:02',
    thumbnail: 'https://via.placeholder.com/120x80',
    bookmarked: false,
  },
  {
    id: 2,
    title: '[환경] 두더지가 나타났다!!',
    tags: ['농촌', '여성', '청년', '시골'],
    date: '2025.11.25 01:02',
    thumbnail: 'https://via.placeholder.com/120x80',
    bookmarked: false,
  },
  {
    id: 3,
    title: '[환경] 두더지가 나타났다!!',
    tags: ['농촌', '여성', '청년', '시골'],
    date: '2025.11.25 01:02',
    thumbnail: 'https://via.placeholder.com/120x80',
    bookmarked: false,
  },
  {
    id: 4,
    title: '[환경] 두더지가 나타났다!!',
    tags: ['농촌', '여성', '청년', '시골'],
    date: '2025.11.25 01:02',
    thumbnail: 'https://via.placeholder.com/120x80',
    bookmarked: false,
  },
]);

const totalPages = computed(() => Math.ceil(articles.value.length / itemsPerPage) || 5);

const visiblePages = computed(() => {
  const pages = [];
  const start = Math.max(1, currentPage.value - 2);
  const end = Math.min(totalPages.value, start + 4);
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
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
    currentPage.value = page;
    // TODO: 해당 페이지 데이터 로드
  }
};
</script>

<style scoped>
.article-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.page-subtitle {
  font-size: 14px;
  color: #888;
  margin: 0;
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
