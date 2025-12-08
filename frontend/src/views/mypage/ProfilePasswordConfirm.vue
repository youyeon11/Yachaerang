<template>
  <div class="pw-confirm">
    <h1 class="title">비밀번호 확인</h1>
    <p class="desc">회원님의 정보를 보호하기 위해 비밀번호를 다시 확인합니다.</p>

    <input v-model="password" type="password" class="input" placeholder="비밀번호" @keyup.enter="handleSubmit" />

    <button class="btn" @click="handleSubmit">확인</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// import api from '@/api/axios';

const router = useRouter();
const password = ref('');

const handleSubmit = async () => {
  // TODO: 비밀번호 검증 API
  router.push('/mypage/profile/edit');
};
</script>

<style scoped>
.pw-confirm {
  max-width: 360px;
  margin: 0 auto;
  text-align: center;
}

.title {
  margin: 80px 0 12px;
  font-size: 22px;
  font-weight: 700;
}

.desc {
  margin: 0 0 32px;
  font-size: 13px;
  color: #555;
}

.input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
  box-sizing: border-box;
}

.btn {
  margin-top: 16px;
  width: 100%;
  padding: 10px 0;
  border-radius: 8px;
  border: none;
  background: #fecc21;
  font-size: 14px;
  cursor: pointer;
}
</style>
