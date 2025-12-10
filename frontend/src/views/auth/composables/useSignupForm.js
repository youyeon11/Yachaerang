import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { signupApi } from '@/api/auth';

export function useSignupForm() {
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
  const isPasswordMatch = computed(() => password.value === passwordConfirm.value);

  // 전체 폼 유효성 검사
  const isFormValid = computed(() => (
    email.value.trim() !== ''
    && emailValid.value
    && isPasswordValid.value
    && isPasswordMatch.value
    && name.value.trim() !== ''
    && nickname.value.trim() !== ''
  ));

  // 이메일 중복 확인
  const checkEmailDuplicate = async () => {
    if (!email.value.trim()) {
      emailMessage.value = '이메일을 입력해주세요.';
      emailValid.value = false;
      return;
    }

    // TODO: 실제 중복 확인 API 연동
    emailValid.value = true;
    emailMessage.value = '중복 확인을 건너뛰었습니다. (임시로 허용)';
  };

  const handleSignup = async () => {
    if (!isFormValid.value) return;

    const payload = {
      name: name.value,
      nickname: nickname.value,
      email: email.value,
      password: password.value,
    };

    try {
      const res = await signupApi(payload);

      const body = res.data;
      const ok = body?.success === true && (body?.code === '204' || body?.httpStatus === 'NO_CONTENT');
      if (ok) {
        // eslint-disable-next-line no-alert
        alert('회원가입이 완료되었습니다!');
        router.push('/login');
        return;
      }

      // eslint-disable-next-line no-alert
      alert(body?.message || '회원가입에 실패했습니다.');
    } catch (err) {
      const status = err?.response?.status;
      const data = err?.response?.data;
      const url = err?.config?.baseURL ? err.config.baseURL + err.config.url : err?.config?.url;
      // eslint-disable-next-line no-alert,no-console
      alert(data?.message || `회원가입 실패 (status: ${status ?? 'unknown'})`);
      // eslint-disable-next-line no-console
      console.error('회원가입 실패:', status, url, data);
    }
  };

  return {
    email,
    password,
    passwordConfirm,
    name,
    nickname,
    emailMessage,
    emailValid,
    isPasswordValid,
    isPasswordMatch,
    isFormValid,
    checkEmailDuplicate,
    handleSignup,
  };
}


