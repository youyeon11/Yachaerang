<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo-wrapper">
        <img src="@/assets/logo.png" alt="야채랑 로고" class="logo" />
      </div>

      <form class="login-form" @submit.prevent="handleLogin">
        <div class="input-group">
          <input type="email" v-model="email" placeholder="이메일" class="input-field" />
        </div>
        <div class="input-group">
          <input type="password" v-model="password" placeholder="비밀번호" class="input-field" />
        </div>
        <button type="submit" class="login-btn" :class="{ disabled: !isFormValid }" :disabled="!isFormValid">
          로그인
        </button>
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
      </form>

      <div class="bottom-links">
        <a href="#" class="link">비밀번호 찾기</a>
        <span class="divider">|</span>
        <router-link to="/signup" class="link">회원가입</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useLoginForm } from '@/composables/useLoginForm';

const {
  email,
  password,
  isFormValid,
  errorMessage,
  handleLogin,
} = useLoginForm();
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 40px 20px;
}

.logo-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 60px;
}

.logo {
  width: 120px;
  height: auto;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.input-group {
  width: 100%;
}

.input-field {
  width: 100%;
  padding: 16px 20px;
  font-size: 16px;
  border: 1px solid #e0e0e0;
  background-color: #fff;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.input-field:first-of-type,
.input-group:first-child .input-field {
  border-radius: 8px 8px 0 0;
  border-bottom: none;
}

.input-group:last-of-type .input-field,
.input-group:nth-child(2) .input-field {
  border-radius: 0 0 8px 8px;
}

.input-field:focus {
  border-color: #f5b041;
}

.input-field::placeholder {
  color: #999;
}

.login-btn {
  width: 100%;
  padding: 16px;
  margin-top: 24px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  background-color: #f5b041;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s, opacity 0.2s;
}

.login-btn:hover:not(.disabled) {
  background-color: #e5a030;
}

.login-btn.disabled {
  background-color: #ccc;
  color: #888;
  cursor: not-allowed;
}

.bottom-links {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 12px;
}

.link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.link:hover {
  color: #333;
  text-decoration: underline;
}

.divider {
  color: #ccc;
  font-size: 14px;
}

.error-message {
  margin-top: 12px;
  padding: 12px;
  background-color: #fee;
  color: #c33;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
}
</style>
