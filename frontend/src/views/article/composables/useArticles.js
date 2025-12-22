import { ref } from 'vue';
import { tokenStorage } from '@/utils/storage';
import { useToastStore } from '@/stores/toast';

export function useArticle() {
  const isProcessing = ref(false);
  const toastStore = useToastStore();

  const checkAuth = () => {
    const accessToken = tokenStorage.getAccessToken();
    const hasValidToken =
      accessToken && accessToken.trim() !== '' && accessToken !== 'null' && accessToken !== 'undefined';

    if (!hasValidToken) {
      toastStore.show('로그인이 필요한 서비스입니다. 로그인 후 이용해 주세요', 'info');
      return false;
    }

    return true;
  };

  const toggleBookmarkAction = async (article) => {
    if (!checkAuth()) return;

    isProcessing.value = true;
    try {
      // TODO: 실제 API 연동 (privateClient 사용)
      // await api.postBookmark(article.id);
      article.bookmarked = !article.bookmarked;
    } catch (error) {
      console.error('북마크 처리 중 오류 발생:', error);
    } finally {
      isProcessing.value = false;
    }
  };

  const toggleReactionAction = async (type, myReaction, reactions) => {
    if (!checkAuth()) return;

    if (myReaction.value === type) {
      reactions.value[type].count--;
      myReaction.value = null;
    } else {
      if (myReaction.value) reactions.value[myReaction.value].count--;
      reactions.value[type].count++;
      myReaction.value = type;
    }

    // TODO: api.postReaction(articleId, type);
  };

  return {
    isProcessing,
    checkAuth,
    toggleBookmarkAction,
    toggleReactionAction,
  };
}
