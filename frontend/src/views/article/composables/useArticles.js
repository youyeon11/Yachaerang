import { tokenStorage } from '@/utils/storage';
import { useToastStore } from '@/stores/toast';
import { addReaction, removeReaction } from '@/api/article';

const REACTION_TYPE_MAP = {
  like: 'GOOD',
  helpful: 'HELPFUL',
  suprise: 'SURPRISED',
  sad: 'SAD',
  bummer: 'BUMMER',
};

const REACTION_TYPE_REVERSE_MAP = {
  GOOD: 'like',
  HELPFUL: 'helpful',
  SURPRISED: 'suprise',
  SAD: 'sad',
  BUMMER: 'bummer',
};

export function useArticle() {
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


  const toggleReactionAction = async (articleId, type, myReaction, reactions) => {
    if (!checkAuth()) return;

    const backendType = REACTION_TYPE_MAP[type];
    if (!backendType) {
      console.error('알 수 없는 리액션 타입:', type);
      return;
    }

    const wasMyReaction = myReaction.value === type;
    const previousReaction = myReaction.value;

    if (wasMyReaction) {
      reactions.value[type].count--;
      myReaction.value = null;
    } else {
      if (previousReaction) {
        reactions.value[previousReaction].count--;
      }
      reactions.value[type].count++;
      myReaction.value = type;
    }

    try {
      if (wasMyReaction) {
        // 리액션 삭제
        await removeReaction(articleId, backendType);
      } else {
        // 리액션 추가
        if (previousReaction) {
          // 기존 리액션 삭제
          const previousBackendType = REACTION_TYPE_MAP[previousReaction];
          await removeReaction(articleId, previousBackendType);
        }
        await addReaction(articleId, backendType);
      }
    } catch (error) {
      // 실패 시 롤백
      if (wasMyReaction) {
        reactions.value[type].count++;
        myReaction.value = type;
      } else {
        if (previousReaction) {
          reactions.value[previousReaction].count++;
        }
        reactions.value[type].count--;
        myReaction.value = previousReaction;
      }
      console.error('리액션 처리 중 오류 발생:', error);
      toastStore.show('리액션 처리 중 오류가 발생했습니다.', 'error');
    }
  };

  return {
    toggleReactionAction,
  };
}
