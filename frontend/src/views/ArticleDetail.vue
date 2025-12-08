<template>
  <div class="article-detail-page">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">기사</h1>
        <p class="page-subtitle">한눈에 확인하는, 야채랑 PICK 농촌 기사</p>
      </div>
    </div>

    <article class="article-content">
      <h1 class="article-title">{{ article.title }}</h1>

      <div class="article-meta">
        <div class="meta-left">
          <span>작성일자 : {{ article.date }}</span>
        </div>
        <div class="meta-right">
          <span>출처: </span>
          <a :href="article.sourceUrl" target="_blank" class="source-link">{{ article.sourceUrl }}</a>
        </div>
      </div>

      <div class="article-image" v-if="article.image">
        <img :src="article.image" :alt="article.title" />
      </div>

      <div class="article-body">
        <p v-for="(paragraph, index) in article.content" :key="index">
          {{ paragraph }}
        </p>
      </div>

      <div class="article-tags" v-if="article.tags && article.tags.length">
        <span class="tags-label">태그</span>
        <div class="tags-list">
          <span v-for="tag in article.tags" :key="tag" class="tag">#{{ tag }}</span>
        </div>
      </div>
    </article>

    <div class="back-to-list">
      <button class="back-btn" @click="goToList">
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
        목록으로
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const searchQuery = ref('');

// 임시 데이터
const article = ref({
  id: 1,
  title: "청년 여성 농촌살이, 2025 '시골언니 프로젝트' 시작",
  date: '2025.12.01',
  sourceUrl: 'https://www.ikpnews.net/news/articleView.html?idxno=67411',
  image: 'https://via.placeholder.com/400x250',
  content: [
    '내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용',
    '내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용',
    '내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용내용',
  ],
  tags: ['농촌', '여성', '청년'],
});

onMounted(() => {
  const articleId = route.params.id;
  console.log('기사 ID:', articleId);
  // TODO: API로 기사 상세 정보 가져오기
  // const response = await axios.get(`/api/v1/articles/${articleId}`)
  // article.value = response.data
});

const goToList = () => {
  router.push('/articles');
};
</script>

<style scoped>
.article-detail-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 16px 0 40px;
}

.page-header {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 24px;
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

/* 기사 내용 */
.article-content {
  padding-bottom: 40px;
  border-bottom: 1px solid #eee;
}

.article-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 20px 0;
  line-height: 1.4;
  text-align: center;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #888;
  margin-bottom: 32px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.meta-left {
  display: flex;
  gap: 8px;
}

.meta-left .divider {
  color: #ddd;
}

.source-link {
  color: #888;
  text-decoration: none;
}

.source-link:hover {
  text-decoration: underline;
}

.article-image {
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}

.article-image img {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
  border-radius: 8px;
}

.article-body {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
}

.article-body p {
  margin: 0 0 24px 0;
}

.article-body p:last-child {
  margin-bottom: 0;
}

.article-tags {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.tags-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.tags-list {
  display: flex;
  gap: 8px;
}

.tag {
  padding: 6px 14px;
  background-color: #f5f5f5;
  border-radius: 20px;
  font-size: 13px;
  color: #555;
}

.back-to-list {
  margin-top: 32px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: none;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  background-color: #f5f5f5;
  border-color: #ccc;
}

/* 작은 화면에서는 위아래로 쌓이게 */
@media (max-width: 640px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-box {
    width: 100%;
  }

  .search-input {
    width: 100%;
  }

  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
