<template>
  <div class="box">
    <!-- 로그인 안 한 상태 -->
    <div v-if="!isLoggedIn" class="login-guard">
      <div class="lock-icon">
        <LockIcon />
      </div>
      <p class="guard-title">로그인 서비스입니다.</p>
      <button class="login-btn" @click="goLogin">로그인 하러가기</button>
    </div>

    <!-- 로그인 한 상태 -->
    <template v-else>
      <div class="header">
        <h4>나의 품목</h4>
        <button class="edit-btn" @click="$emit('edit')">수정</button>
      </div>

      <div class="list">
        <button
          v-for="item in items"
          :key="item.favoriteId ?? item.productCode ?? item.displayName"
          class="item"
          type="button"
          @click="$emit('select', item)"
        >
          <span class="item-main">{{ item.displayName ?? item }}</span>
          <span v-if="item.periodLabel" class="item-period">{{ item.periodLabel }}</span>
        </button>

        <p v-if="items.length === 0" class="empty-text">아직 등록한 관심 품목이 없어요</p>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import LockIcon from '@/components/icons/LockIcon.vue';

const props = defineProps({
  items: {
    type: Array,
    default: () => [],
  },
});

defineEmits(['select', 'edit']);

const router = useRouter();

const isLoggedIn = computed(() => {
  const token = localStorage.getItem('accessToken');
  return token && token !== 'null' && token !== 'undefined' && token.trim() !== '';
});

const goLogin = () => {
  router.push('/login');
};
</script>

<style scoped>
.box {
  width: 280px;
  border-radius: 16px;
  padding: 20px 18px;
  background: #ffffff;
  border: 1px solid #e5e5e5;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h4 {
  font-size: 16px;
  font-weight: 700;
  color: #333;
}

.edit-btn {
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 6px;
  border: none;
  background: #f0f0f0;
  cursor: pointer;
  transition: 0.2s ease;
}

.edit-btn:hover {
  background: #e0e0e0;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.item {
  padding: 10px 12px;
  border-radius: 10px;
  background: #fafafa;
  border: 1px solid #ebebeb;
  font-size: 14px;
  line-height: 1.4;
  color: #444;
  transition: 0.18s ease;
}

.item:hover {
  background: #f3f3f3;
}

.login-guard {
  height: 100%;
  min-height: 220px;
  border-radius: 14px;
  border: 1px dashed #ddd;
  background: #fafafa;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 14px;
  text-align: center;
}

.lock-icon {
  font-size: 32px;
}

.guard-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.login-btn {
  margin-top: 6px;
  padding: 10px 18px;
  border-radius: 10px;
  border: none;
  background: #3a3a3a;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s ease;
}

.login-btn:hover {
  opacity: 0.9;
}

.empty-text {
  font-size: 13px;
  color: #999;
  padding: 8px 4px;
}
.lock-icon {
  color: #6b7280;
  margin-bottom: 4px;
}
</style>
