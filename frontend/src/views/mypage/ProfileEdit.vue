<template>
  <div class="profile-edit">
    <div class="header-row">
      <h1 class="title">프로필 수정</h1>
      <button class="btn-out">회원탈퇴</button>
    </div>

    <table class="profile-table">
      <tbody>
        <tr>
          <th>프로필 이미지</th>
          <td class="profile-img-cell">
            <div class="avatar-circle">
              <img
                v-if="form.imageUrl"
                :src="form.imageUrl"
                alt="프로필 이미지"
              />
              <span v-else>👤</span>
            </div>
          </td>
          <td class="align-right">
            <button class="btn-small">업로드</button>
          </td>
        </tr>
        <tr>
          <th>이메일</th>
          <td colspan="2">{{ form.email }}</td>
        </tr>
        <tr>
          <th>비밀번호</th>
          <td colspan="2">
            <button class="btn-small" @click="goPasswordChange">비밀번호 변경</button>
          </td>
        </tr>
        <tr>
          <th>이름</th>
          <td colspan="2">
            <input v-model="form.name" class="input" />
          </td>
        </tr>
        <tr>
          <th>닉네임</th>
          <td colspan="2">
            <input v-model="form.nickname" class="input" />
          </td>
        </tr>
      </tbody>
    </table>

    <button class="btn-submit" @click="handleSubmit">수정하기</button>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { getMyProfile, updateProfile } from '@/api/member';

const router = useRouter();

const form = reactive({
  email: '',
  name: '',
  nickname: '',
  imageUrl: '',
});

onMounted(async () => {
  try {
    const { data } = await getMyProfile();
    if (data.success) {
      form.email = data.data.email;
      form.name = data.data.name;
      form.nickname = data.data.nickname;
      form.imageUrl = data.data.imageUrl || '';
    }
  } catch (e) {
    console.error(e);
  }
});

const goPasswordChange = () => {
  router.push('/mypage/password');
};

const handleSubmit = async () => {
  try {
    const payload = {
      name: form.name,
      nickname: form.nickname,
    };

    const { data } = await updateProfile(payload);

    if (!data.success) {
      alert(data.message || '프로필 수정 실패');
      return;
    }

    alert('프로필이 수정되었습니다.');
    router.push('/mypage');
  } catch (e) {
    console.error(e);
    alert('서버 오류가 발생했습니다.');
  }
};
</script>

<style scoped>
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.title {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.btn-out {
  padding: 6px 14px;
  border-radius: 4px;
  border: 1px solid #ddd;
  background: #fff;
  font-size: 12px;
  cursor: pointer;
}

.profile-table {
  width: 100%;
  border-collapse: collapse;
  border-top: 1px solid #ddd;
  border-bottom: 1px solid #ddd;
  margin-bottom: 24px;
}

.profile-table th,
.profile-table td {
  padding: 10px 12px;
  border-top: 1px solid #eee;
  font-size: 14px;
}

.profile-table tr:first-child th,
.profile-table tr:first-child td {
  border-top: none;
}

.profile-img-cell {
  width: 80px;
}

.avatar-circle {
  width: 60px;
  height: 60px;
  border-radius: 999px;
  border: 1px solid #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.align-right {
  text-align: right;
}

.btn-small {
  padding: 6px 12px;
  border-radius: 4px;
  border: 1px solid #ddd;
  background: #fff;
  font-size: 12px;
  cursor: pointer;
}

.input {
  width: 100%;
  padding: 8px 10px;
  border-radius: 4px;
  border: 1px solid #ddd;
  box-sizing: border-box;
}

.btn-submit {
  width: 100%;
  padding: 10px 0;
  border-radius: 8px;
  border: none;
  background: #fecc21;
  font-size: 14px;
  cursor: pointer;
}
</style>
