<template>
  <div class="forgot-container">
    <div class="forgot-box">
      <h1 class="title">비밀번호 찾기</h1>

      <div class="form">
        <div class="form-group">
          <label class="label">이메일</label>
          <input
            type="email"
            v-model="email"
            class="input"
            :disabled="emailSent"
            placeholder="가입하신 이메일을 입력하세요"
          />
          <button v-if="!emailSent" type="button" class="btn" @click="sendResetCode" :disabled="isLoading">
            인증번호 보내기
          </button>
          <p v-if="emailSent" class="message success">입력하신 이메일로 인증번호를 발송했습니다.</p>
        </div>

        <div v-if="emailSent" class="form-group">
          <label class="label">인증번호</label>
          <input type="text" v-model="emailCode" maxlength="6" class="input" placeholder="인증번호 6자리 입력" />
          <button type="button" class="btn" @click="resetPasswordWithCode" :disabled="isLoading">인증하기</button>
        </div>

        <router-link class="back-link" :to="{ name: 'login' }">로그인으로 돌아가기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useForgotPasswordForm } from '@/views/auth/composables/useForgotPasswordForm';

const { email, emailSent, emailCode, isLoading, sendResetCode, resetPasswordWithCode } = useForgotPasswordForm();
</script>

<style scoped>
.forgot-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  padding: 40px 20px;
}

.forgot-box {
  width: 100%;
  max-width: 420px;
}

.title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 12px;
  color: #333;
}

.description {
  font-size: 14px;
  color: #777;
  margin-bottom: 32px;
  line-height: 1.5;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-with-button {
  display: flex;
  gap: 8px;
}

.input-with-button .input {
  flex: 1;
}

.button-slot {
  display: flex;
  align-items: stretch;
}

.label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.input {
  padding: 12px 14px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}

.input:focus {
  border-color: #f5b041;
}

.message {
  font-size: 12px;
  margin: 0;
}

.message.success {
  color: #27ae60;
}

.btn {
  margin-top: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  background-color: #f5b041;
  color: #333;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
  white-space: nowrap;
  min-width: 130px;
}

.btn:hover:not(:disabled) {
  background-color: #e5a030;
}

.btn:disabled {
  background-color: #ccc;
  color: #888;
  cursor: not-allowed;
}

.btn-placeholder {
  margin-top: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  min-width: 130px;
}

.back-link {
  margin-top: 4px;
  font-size: 13px;
  color: #666;
  text-align: center;
  text-decoration: none;
}

.back-link:hover {
  color: #333;
  text-decoration: underline;
}
</style>
