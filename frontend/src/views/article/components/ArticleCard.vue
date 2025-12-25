<template>
  <div
    class="group relative overflow-hidden rounded-3xl border border-gray-100 bg-white shadow-sm transition-all duration-300 hover:shadow-xl hover:-translate-y-1 cursor-pointer flex flex-col h-full"
    @click="emit('open', article.id)"
  >
    <div class="relative overflow-hidden w-full h-48 lg:h-56">
      <img
        :src="article.thumbnail"
        :alt="article.title"
        class="h-full w-full object-cover transition-transform duration-700 group-hover:scale-110"
      />
      <div
        class="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity"
      ></div>

      <!-- 북마크 버튼 -->
      <button
        type="button"
        @click.stop="handleToggleBookmark"
        :disabled="isLoading"
        class="absolute top-4 right-4 p-2.5 rounded-full bg-white/90 backdrop-blur-sm shadow-md transition-all hover:scale-110 active:scale-95 z-10 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <IconBookmark
          :active="isBookmarked"
          :class="[isBookmarked ? 'text-[#FECC21]' : 'text-gray-400 group-hover:text-[#FECC21]']"
          class="w-5 h-5 transition-colors"
        />
      </button>
    </div>

    <!-- 콘텐츠 영역 -->
    <div class="flex-1 p-5 lg:p-6 flex flex-col">
      <!-- 태그 -->
      <div class="flex flex-wrap gap-2 mb-3">
        <span
          v-for="tag in article.tags"
          :key="tag"
          @click.stop="handleTagClick(tag)"
          class="px-3 py-1 bg-[#FECC21]/10 text-gray-800 text-xs font-bold rounded-full border border-[#FECC21]/20 group-hover:bg-[#FECC21]/25 group-hover:border-[#FECC21]/50 group-hover:text-gray-900 transition-all duration-300 cursor-pointer hover:bg-[#FECC21]/30 hover:border-[#FECC21]/60"
        >
          #{{ tag }}
        </span>
      </div>

      <!-- 날짜 -->
      <p class="text-gray-400 text-sm font-medium mb-3">
        {{ formattedDate }}
      </p>

      <!-- 제목 -->
      <h3 class="text-lg lg:text-xl font-black text-gray-900 leading-tight line-clamp-2 mb-4 flex-1">
        {{ article.title }}
      </h3>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { saveBookmark, removeBookmark } from '@/api/article';
import { useToastStore } from '@/stores/toast';
import { tokenStorage } from '@/utils/storage';
import IconBookmark from '@/components/icons/IconBookmark.vue';

const props = defineProps({
  article: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['open', 'bookmark-updated', 'search-by-keyword']);

const formattedDate = computed(() => {
  if (!props.article?.date) return '';
  return new Date(props.article.date).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
});

const toastStore = useToastStore();
const isLoading = ref(false);
const isBookmarked = ref(props.article.isBookmarked ?? props.article.bookmarked ?? false);

watch(
  () => props.article.isBookmarked ?? props.article.bookmarked,
  (newVal) => {
    isBookmarked.value = newVal ?? false;
  }
);

const handleTagClick = (tag) => {
  emit('search-by-keyword', tag);
};

const handleToggleBookmark = async () => {
  if (isLoading.value) return;

  // 로그인 상태 확인
  const accessToken = tokenStorage.getAccessToken();
  const hasValidToken = accessToken && accessToken.trim() !== '' && accessToken !== 'null' && accessToken !== 'undefined';

  if (!hasValidToken) {
    toastStore.show('로그인이 필요한 서비스입니다. 로그인 후 이용해 주세요', 'info');
    return;
  }

  isLoading.value = true;
  const wasBookmarked = isBookmarked.value;

  isBookmarked.value = !wasBookmarked;

  try {
    if (wasBookmarked) {
      await removeBookmark(props.article.id);
      toastStore.show('북마크가 해제되었습니다.', 'success');
    } else {
      await saveBookmark(props.article.id);
      toastStore.show('북마크에 저장되었습니다.', 'success');
    }
    // 상태 변경
    emit('bookmark-updated', {
      articleId: props.article.id,
      bookmarked: isBookmarked.value,
    });
  } catch (error) {
    isBookmarked.value = wasBookmarked;
    console.error('북마크 처리 실패:', error);
    toastStore.show('북마크 처리 중 오류가 발생했습니다.', 'error');
  } finally {
    isLoading.value = false;
  }
};
</script>
