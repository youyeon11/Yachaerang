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
import { useRouter } from 'vue-router';
import { changePasswordApi } from '@/api/member';
import { logout } from '@/stores/auth';
import apiClient from '@/api/axios';

const router = useRouter();

const currentPassword = ref('');
const newPassword = ref('');
const newPasswordConfirm = ref('');

const currentPasswordValid = ref(false);
const currentPasswordMsg = ref('');

const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;

const newPasswordMsg = computed(() => {
  if (!passwordRegex.test(newPassword.value)) {
    return '문자, 숫자, 특수문자를 포함한 8~20자여야 합니다.';
  }
  return '사용 가능한 비밀번호입니다.';
});

const newPasswordClass = computed(() => (passwordRegex.test(newPassword.value) ? 'msg success' : 'msg error'));

const confirmMsg = computed(() =>
  newPassword.value === newPasswordConfirm.value ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.'
);

const confirmClass = computed(() => (newPassword.value === newPasswordConfirm.value ? 'msg success' : 'msg error'));

const currentPasswordClass = computed(() => (currentPasswordValid.value ? 'msg success' : 'msg error'));

const handleSubmit = async () => {
  if (!currentPasswordValid.value) {
    alert('현재 비밀번호를 확인해주세요.');
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

    logout();
    router.push('/login');
  } catch (error) {
    alert(error.response?.data?.message || '비밀번호 변경 중 오류가 발생했습니다.');
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
