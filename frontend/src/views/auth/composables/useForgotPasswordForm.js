import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { sendPasswordResetCodeApi, resetPasswordApi } from '@/api/auth';

export function useForgotPasswordForm() {
  const router = useRouter();

  const email = ref('');
  const emailSent = ref(false);
  const emailCode = ref('');
  const isLoading = ref(false);

  const sendResetCode = async () => {
    if (!email.value) {
      alert('이메일을 입력해주세요.');
      return;
    }

    try {
      isLoading.value = true;
      await sendPasswordResetCodeApi({
        mail: email.value,
      });
      emailSent.value = true;
      alert('입력하신 이메일로 인증번호를 발송했습니다.');
    } catch (error) {
      const errorCode = error.response?.data?.code;
      const message = error.response?.data?.message;

      if (errorCode === 'MEMBER_001') {
        alert('가입되지 않은 이메일입니다.');
      } else {
        alert(message || '비밀번호 찾기용 인증번호 발송 중 오류가 발생했습니다.');
      }
    } finally {
      isLoading.value = false;
    }
  };

  const resetPasswordWithCode = async () => {
    if (!email.value) {
      alert('이메일을 입력해주세요.');
      return;
    }
    if (!emailCode.value) {
      alert('이메일로 받은 인증번호를 입력해주세요.');
      return;
    }

    try {
      isLoading.value = true;
      await resetPasswordApi({
        mail: email.value,
        code: emailCode.value,
      });

      alert('임시 비밀번호가 메일로 전송되었습니다. 메일을 확인한 뒤 로그인해주세요.');
      router.push({ name: 'login' });
    } catch (error) {
      const errorCode = error.response?.data?.code;
      const message = error.response?.data?.message;

      if (errorCode === 'MAIL_001') {
        alert('인증코드가 만료되었거나 존재하지 않습니다. 다시 인증번호를 요청해주세요.');
      } else if (errorCode === 'MAIL_002') {
        alert('인증 코드가 올바르지 않습니다. 다시 확인해주세요.');
      } else if (errorCode === 'MEMBER_001') {
        alert('가입되지 않은 이메일입니다.');
      } else {
        alert(message || '비밀번호 재설정 중 오류가 발생했습니다.');
      }
    } finally {
      isLoading.value = false;
    }
  };

  return {
    email,
    emailSent,
    emailCode,
    isLoading,
    sendResetCode,
    resetPasswordWithCode,
  };
}


