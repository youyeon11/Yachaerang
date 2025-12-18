import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { sendEmailCodeApi, verifyEmailCodeApi, signupApi } from '@/api/auth';

export function useSignupForm() {
  const router = useRouter();

  const email = ref('');
  const password = ref('');
  const passwordConfirm = ref('');
  const name = ref('');
  const nickname = ref('');

  const emailSent = ref(false);
  const emailCode = ref('');
  const emailVerified = ref(false);

  const sendEmailCode = async () => {
    if (!email.value) {
      alert('이메일을 입력해주세요.');
      return;
    }
    try {
      await sendEmailCodeApi({
        mail: email.value,
      });
      emailSent.value = true;
      alert('입력하신 이메일로 인증번호를 발송했습니다.');
    } catch (error) {
      const errorCode = error.response?.data?.code;
      if (errorCode === 'MEMBER_002') {
        alert('이미 가입된 이메일입니다.');
      } else {
        alert(error.response?.data?.message || '이메일 인증번호 발송 중 오류가 발생했습니다.');
      }
    }
  };

  const verifyEmailCode = async () => {
    if (!emailCode.value) {
      alert('인증번호를 입력해주세요.');
      return;
    }

    try {
      const { data } = await verifyEmailCodeApi({
        mail: email.value,
        code: emailCode.value,
      });

      if (data?.data === true) {
        emailVerified.value = true;
        alert('이메일 인증이 완료되었습니다.');
      } else {
        alert('인증번호가 올바르지 않습니다.');
      }
    } catch (error) {
      console.error('이메일 인증 중 오류:', error);
      alert(error.response?.data?.message || '이메일 인증 중 오류가 발생했습니다.');
    }
  };

  const isPasswordValid = computed(() => /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&]).{8,20}$/.test(password.value));

  const isPasswordMatch = computed(() => password.value === passwordConfirm.value);

  const isFormValid = computed(
    () => email.value && isPasswordValid.value && isPasswordMatch.value && name.value && nickname.value
  );

  const handleSignup = async () => {
    if (!emailVerified.value) {
      alert('이메일 인증을 완료해주세요.');
      return;
    }
    try {
      await signupApi({
        email: email.value,
        password: password.value,
        name: name.value,
        nickname: nickname.value,
      });
      alert('회원가입이 완료되었습니다.');
      router.push({ name: 'login' });
    } catch (error) {
      const errorCode = error.response?.data?.code;
      if (errorCode === 'MEMBER_002') {
        alert('이미 가입된 이메일입니다.');
      } else {
        alert(error.response?.data?.message || '회원가입 중 오류가 발생했습니다.');
      }
    }
  };

  return {
    email,
    password,
    passwordConfirm,
    name,
    nickname,

    emailSent,
    emailCode,
    emailVerified,

    isPasswordValid,
    isPasswordMatch,
    isFormValid,

    sendEmailCode,
    verifyEmailCode,
    handleSignup,
  };
}
