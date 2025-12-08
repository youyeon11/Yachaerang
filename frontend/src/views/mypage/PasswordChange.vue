<template>
  <div class="password-change">
    <h1 class="title">비밀번호 변경</h1>
    <div class="form-area">
      <div class="field">
        <label>현재 비밀번호</label>
        <input
          v-model="currentPassword"
          type="password"
          class="input"
          placeholder="비밀번호 입력 (문자, 숫자, 특수문자 포함 8~20자)"
        />
      </div>

      <div class="field">
        <label>새 비밀번호</label>
        <input
          v-model="newPassword"
          type="password"
          class="input"
          placeholder="비밀번호 입력 (문자, 숫자, 특수문자 포함 8~20자)"
        />
      </div>

      <div class="field">
        <label>새 비밀번호 확인</label>
        <input
          v-model="newPasswordConfirm"
          type="password"
          class="input"
          placeholder="비밀번호 입력 (문자, 숫자, 특수문자 포함 8~20자)"
        />
      </div>

      <button class="btn" @click="handleSubmit">확인</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const currentPassword = ref('');
const newPassword = ref('');
const newPasswordConfirm = ref('');

const handleSubmit = () => {
  // TODO: 유효성 검사 + 비밀번호 변경 API
};
</script>

<style scoped>
.password-change {
  width: 100%;
}
.form-area {
  width: 100%;
  max-width: 480px;
  margin: 0 auto;
  margin-top: 100px;
}

.title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 24px;
}

.field {
  margin-bottom: 16px;
}

.field label {
  display: block;
  font-size: 13px;
  margin-bottom: 6px;
}

.input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  box-sizing: border-box;
}

.btn {
  margin-top: 8px;
  width: 100%;
  padding: 10px 0;
  border-radius: 8px;
  border: none;
  background: #fecc21;
  font-size: 14px;
  cursor: pointer;
}
</style>
