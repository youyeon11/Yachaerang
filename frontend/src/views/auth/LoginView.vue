<template>
  <div class="login-container">
<div class="login-box">
      <div class="logo-wrapper">
        <div class="logo-squash-stretch">
          <BrandLogoImage :src="logoUrl" size="lg"/>
        </div>
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
        <router-link to="/forgot-password" class="link">비밀번호 찾기</router-link>
        <span class="divider">|</span>
        <router-link to="/signup" class="link">회원가입</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useLoginForm } from '@/views/auth/composables/useLoginForm';

import BrandLogoImage from '@/components/brand/BrandLogoImage.vue';
import logoUrl from '../../assets/logo.svg'

const { email, password, isFormValid, errorMessage, handleLogin } = useLoginForm();
</script>

<style scoped>
/* LogoImage */
.logo-squash-stretch {
  animation: squashStretch 2s ease-in-out 2;
}

@keyframes squashStretch {
  0%, 100% {
    transform: scaleX(1) scaleY(1);
  }
  25% {
    transform: scaleX(1.1) scaleY(0.9);
  }
  50% {
    transform: scaleX(0.9) scaleY(1.1);
  }
  75% {
    transform: scaleX(1.05) scaleY(0.95);
  }
}

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
  max-width: 500px;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
}

.logo-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

.login-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px; /* input 간 간격 추가 */
}

.input-group {
  width: 100%;
}

.input-field {
  width: 100%;
  padding: 18px 24px;
  font-size: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px; /* 모든 input에 동일한 border-radius */
  background-color: #fff;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

.input-field:focus {
  border-color: #FECC21;
  box-shadow: 0 0 0 2px rgba(254, 204, 33, 0.2);
}

.input-field::placeholder {
  color: #999;
}

/* 버튼 */
.login-btn {
  width: 100%;
  padding: 18px;
  margin-top: 12px;
  font-size: 17px;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, #E53935 0%, #EF5350 100%);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s, background 0.2s;
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.3);
}

.login-btn:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(229, 57, 53, 0.4);
  background: linear-gradient(135deg, #D32F2F 0%, #E53935 100%);
}

.login-btn:active:not(.disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(229, 57, 53, 0.3);
}

.login-btn.disabled {
  background: #e0e0e0;
  color: #999;
  cursor: not-allowed;
  box-shadow: none;
}

/* 하단 링크 */
.bottom-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.link:hover {
  color: #E53935;
  text-decoration: none;
}

.divider {
  color: #ccc;
  font-size: 14px;
}

/* 에러 메시지 */
.error-message {
  width: 100%;
  margin-top: 12px;
  padding: 12px;
  background-color: #FFEBEE;
  color: #E53935;
  border: 1px solid #FFCDD2;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
  box-sizing: border-box;
}
</style>