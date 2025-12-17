<template>
  <div>
    <!-- 에러 메시지 표시 -->
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    <!-- 농장 아직 없음 -->
    <MyFarmEmpty v-if="mode === 'empty'" @click-register="mode = 'form'" />

    <!-- 농장 등록 폼 -->
    <MyFarmForm
      v-else-if="mode === 'form'"
      :initialFarm="isEdit ? farm : null"
      :isEdit="isEdit"
      @submitted="handleSubmit"
      @cancel="handleCancel"
    />

    <!-- 농장 이미 등록됨 -->
    <MyFarmSummary v-else-if="mode === 'summary'" :farm="farm" @edit="handleEdit" />
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import MyFarmEmpty from '@/views/mypage/components/MyFarmEmpty.vue';
import MyFarmForm from '@/views/mypage/components/MyFarmForm.vue';
import MyFarmSummary from '@/views/mypage/components/MyFarmSummary.vue';
import { getMyFarm, createFarm, updateFarm } from '@/api/farm';

const mode = ref('empty');
const farm = ref(null);
const isEdit = ref(false);
const errorMessage = ref('');

const unwrapFarmData = (response) => {
  return response?.data?.data ?? null;
};

const hasFarm = (farmData) => farmData != null;

onMounted(async () => {
  try {
    const response = await getMyFarm();
    const farmData = unwrapFarmData(response);

    if (hasFarm(farmData)) {
      farm.value = farmData;
      mode.value = 'summary';
    } else {
      farm.value = null;
      mode.value = 'empty';
    }
  } catch (e) {
    console.error('농장 정보 조회 실패', e);
    errorMessage.value = '농장 정보를 불러오는데 실패했습니다. 새로고침 후 다시 시도해주세요.';
    mode.value = 'empty';
    farm.value = null;
  }
});

const handleSubmit = async (payload) => {
  try {
    errorMessage.value = '';

    if (isEdit.value) {
      await updateFarm(payload);
    } else {
      await createFarm(payload);
    }

    const response = await getMyFarm();
    const farmData = unwrapFarmData(response);

    if (hasFarm(farmData)) {
      farm.value = farmData;
      mode.value = 'summary';
    } else {
      farm.value = null;
      mode.value = 'empty';
    }

    isEdit.value = false;
  } catch (e) {
    console.error('농장 저장 실패', e?.response ?? e);
    const msg = e?.response?.data?.message || '농장 정보 등록에 실패했습니다. 잠시 후 다시 시도해주세요.';
    errorMessage.value = msg;
  }
};

const handleEdit = () => {
  isEdit.value = true;
  mode.value = 'form';
};

const handleCancel = () => {
  isEdit.value = false;
  mode.value = farm.value ? 'summary' : 'empty';
};
</script>

<style scoped>
.error-message {
  margin-bottom: 12px;
  padding: 8px 10px;
  border-radius: 4px;
  background: #fee2e2;
  color: #b91c1c;
  font-size: 13px;
}
</style>
