<template>
  <div class="password-change">
    <h1 class="title">비밀번호 변경</h1>
    <div class="form-area">
      <div class="field">
        <label>현재 비밀번호</label>
        <input v-model="currentPassword" type="password" class="input" placeholder="현재 비밀번호 입력" />

        <p v-if="currentPasswordMsg" :class="currentPasswordClass">
          {{ currentPasswordMsg }}
        </p>
      </div>

      <div class="field">
        <label>새 비밀번호</label>
        <input
          v-model="newPassword"
          type="password"
          class="input"
          placeholder="비밀번호 입력 (문자, 숫자, 특수문자 포함 8~20자)"
        />
        <p v-if="newPassword" :class="newPasswordClass">
          {{ newPasswordMsg }}
        </p>
      </div>

      <div class="field">
        <label>새 비밀번호 확인</label>
        <input
          v-model="newPasswordConfirm"
          type="password"
          class="input"
          placeholder="비밀번호 입력 (문자, 숫자, 특수문자 포함 8~20자)"
        />
        <p v-if="newPasswordConfirm" :class="confirmClass">
          {{ confirmMsg }}
        </p>
      </div>

      <button class="btn" @click="handleSubmit">확인</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { changePasswordApi } from '@/api/member';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const currentPassword = ref('');
const newPassword = ref('');
const newPasswordConfirm = ref('');

const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;

// 현재 비밀번호와 새 비밀번호 동일 여부
const isSameAsCurrent = computed(() => {
  if (!currentPassword.value || !newPassword.value) return false;
  return currentPassword.value === newPassword.value;
});

// 새 비밀번호 안내 메시지(동일 여부 우선)
const newPasswordMsg = computed(() => {
  if (!newPassword.value) return '';
  if (isSameAsCurrent.value) return '현재 비밀번호와 다른 비밀번호를 입력해주세요.';
  if (!passwordRegex.test(newPassword.value)) return '문자, 숫자, 특수문자를 포함한 8~20자여야 합니다.';
  return '사용 가능한 비밀번호입니다.';
});

const newPasswordClass = computed(() => {
  if (!newPassword.value) return 'msg';
  if (isSameAsCurrent.value) return 'msg error';
  return passwordRegex.test(newPassword.value) ? 'msg success' : 'msg error';
});

const confirmMsg = computed(() =>
  newPassword.value === newPasswordConfirm.value ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.'
);
const confirmClass = computed(() => (newPassword.value === newPasswordConfirm.value ? 'msg success' : 'msg error'));

const handleSubmit = async () => {
  if (!currentPassword.value) {
    alert('현재 비밀번호를 입력해주세요.');
    return;
  }

  if (!newPassword.value) {
    alert('새 비밀번호를 입력해주세요.');
    return;
  }

  // 동일하면 요청 자체 차단
  if (isSameAsCurrent.value) {
    alert('현재 비밀번호와 다른 비밀번호를 입력해주세요.');
    return;
  }

  if (!passwordRegex.test(newPassword.value)) {
    alert('새 비밀번호 형식을 확인해주세요.');
    return;
  }

  if (newPassword.value !== newPasswordConfirm.value) {
    alert('새 비밀번호가 일치하지 않습니다.');
    return;
  }

  try {
    await changePasswordApi(currentPassword.value, newPassword.value);

    alert('비밀번호를 수정하였습니다.\n다시 로그인해주세요.');
    await authStore.logout();
  } catch (error) {
    const errorResponse = error.response?.data;
    const errorCode = errorResponse?.code;

    if (errorCode === 'LOGIN_001') {
      alert('현재 비밀번호가 올바르지 않습니다.');
      return;
    }
    if (errorCode === 'LOGIN_002') {
      alert(errorResponse?.message || '비밀번호 변경에 실패했습니다.');
      return;
    }

    alert('비밀번호 변경 중 오류가 발생했습니다.');
    console.error('change password error:', error);
  }
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
  height: 44px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  box-sizing: border-box;
  white-space: nowrap;
}

.input::placeholder {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

.msg {
  margin-top: 6px;
  font-size: 12px;
}

.msg.success {
  color: #1f9d55;
}

.msg.error {
  color: #e53e3e;
}
</style>
