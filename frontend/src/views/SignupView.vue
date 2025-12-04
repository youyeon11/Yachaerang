<template>
  <div class="signup-container">
    <div class="signup-box">
      <h1 class="signup-title">회원가입</h1>

      <form class="signup-form" @submit.prevent="handleSignup">
        <!-- 이메일 -->
        <div class="form-group">
          <label class="form-label">이메일</label>
          <div class="input-with-button">
            <input
              type="email"
              v-model="email"
              placeholder="이메일 입력"
              class="input-field"
            />
            <button type="button" class="check-btn" @click="checkEmailDuplicate">
              중복 확인
            </button>
          </div>
          <p v-if="emailMessage" :class="['message', emailValid ? 'success' : 'error']">
            {{ emailMessage }}
          </p>
        </div>

        <!-- 비밀번호 -->
        <div class="form-group">
          <label class="form-label">비밀번호</label>
          <input
            type="password"
            v-model="password"
            placeholder="비밀번호 입력 (문자, 숫자, 특수문자 포함 8~20자)"
            class="input-field"
          />
          <p v-if="password && !isPasswordValid" class="message error">
            문자, 숫자, 특수문자 포함 8~20자로 입력해주세요.
          </p>
        </div>

        <!-- 비밀번호 확인 -->
        <div class="form-group">
          <label class="form-label">비밀번호 확인</label>
          <input
            type="password"
            v-model="passwordConfirm"
            placeholder="비밀번호 입력 (문자, 숫자, 특수문자 포함 8~20자)"
            class="input-field"
          />
          <p v-if="passwordConfirm && !isPasswordMatch" class="message error">
            비밀번호가 일치하지 않습니다.
          </p>
        </div>

        <!-- 이름 -->
        <div class="form-group">
          <label class="form-label">이름</label>
          <input
            type="text"
            v-model="name"
            placeholder="이름을 입력해주세요"
            class="input-field"
          />
        </div>

        <!-- 닉네임 -->
        <div class="form-group">
          <label class="form-label">닉네임</label>
          <input
            type="text"
            v-model="nickname"
            placeholder="닉네임을 입력해주세요"
            class="input-field"
          />
        </div>

        <!-- 회원가입 버튼 -->
        <button
          type="submit"
          class="signup-btn"
          :class="{ disabled: !isFormValid }"
          :disabled="!isFormValid"
        >
          회원가입
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const email = ref('');
const password = ref('');
const passwordConfirm = ref('');
const name = ref('');
const nickname = ref('');

const emailMessage = ref('');
const emailValid = ref(false);

// 비밀번호 유효성 검사 (문자, 숫자, 특수문자 포함 8~20자)
const isPasswordValid = computed(() => {
  const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;
  return regex.test(password.value);
});

// 비밀번호 일치 확인
const isPasswordMatch = computed(() => {
  return password.value === passwordConfirm.value;
});

// 전체 폼 유효성 검사
const isFormValid = computed(() => {
  return (
    email.value.trim() !== '' &&
    emailValid.value &&
    isPasswordValid.value &&
    isPasswordMatch.value &&
    name.value.trim() !== '' &&
    nickname.value.trim() !== ''
  );
});

// 이메일 중복 확인
const checkEmailDuplicate = async () => {
  if (!email.value.trim()) {
    emailMessage.value = '이메일을 입력해주세요.';
    emailValid.value = false;
    return;
  }

  try {
    // TODO: 실제 백엔드 API 호출 (axios 사용)
    // const response = await axios.get(`/api/v1/auth/check-email?email=${email.value}`)
    // emailValid.value = response.data.available

    // 임시: 중복 확인 성공 처리
    emailValid.value = true;
    emailMessage.value = '사용 가능한 이메일입니다.';
  } catch (error) {
    emailValid.value = false;
    emailMessage.value = '이미 사용 중인 이메일입니다.';
  }
};

// 회원가입 처리
const handleSignup = async () => {
  if (!isFormValid.value) return;

  try {
    // TODO: 실제 백엔드 API 호출 (axios 사용)
    // const response = await axios.post('/api/v1/auth/signup', {
    //   email: email.value,
    //   password: password.value,
    //   name: name.value,
    //   nickname: nickname.value
    // })

    // 임시: 회원가입 성공 처리
    console.log('회원가입 시도:', {
      email: email.value,
      password: password.value,
      name: name.value,
      nickname: nickname.value,
    });

    alert('회원가입이 완료되었습니다!');
    router.push('/login');
  } catch (error) {
    console.error('회원가입 실패:', error);
    alert('회원가입에 실패했습니다.');
  }
};
</script>

<style scoped>
.signup-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fafafa;
  padding: 40px 20px;
}

.signup-box {
  width: 100%;
  max-width: 500px;
}

.signup-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 40px;
}

.signup-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.input-field {
  width: 100%;
  padding: 14px 16px;
  font-size: 14px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fff;
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.input-field:focus {
  border-color: #f5b041;
}

.input-field::placeholder {
  color: #999;
}

.input-with-button {
  display: flex;
  gap: 8px;
}

.input-with-button .input-field {
  flex: 1;
}

.check-btn {
  padding: 14px 20px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  background-color: #f5b041;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  white-space: nowrap;
  transition: background-color 0.2s;
}

.check-btn:hover {
  background-color: #e5a030;
}

.message {
  font-size: 12px;
  margin: 0;
}

.message.success {
  color: #27ae60;
}

.message.error {
  color: #e74c3c;
}

.signup-btn {
  width: 100%;
  padding: 16px;
  margin-top: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  background-color: #f5b041;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.signup-btn:hover:not(.disabled) {
  background-color: #e5a030;
}

.signup-btn.disabled {
  background-color: #ccc;
  color: #888;
  cursor: not-allowed;
}
</style>



