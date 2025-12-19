<template>
  <div class="max-w-4xl mx-auto px-4 py-6 sm:px-6 sm:py-8">
    <!-- 에러 메시지 -->
    <div
     v-if="errorMessage"
     class="mb-6 p-4 rounded-xl border border-red-300 bg-red-50"
      role="alert"
      aria-live="polite"
    >
        <div class="flex items-start gap-3">
            <div class="flex-shrink-0 mt-0.5">
                <svg class="w-5 h-5 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                    <path
                    fill-rule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                    clip-rule="evenodd"
                    />
                </svg>
            </div>
                <p class="text-sm text-red-800 font-medium leading-relaxed">
                    {{ errorMessage }}
                </p>
        </div>
    </div>

    <!-- 메인 컨텐츠 -->
    <div class="farm-content">
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
      <MyFarmSummary 
        v-else-if="mode === 'summary'" 
        :farm="farm" 
        @edit="handleEdit" 
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

import MyFarmEmpty from '@/views/myfarm/components/MyFarmEmpty.vue';
import MyFarmForm from '@/views/myfarm/components/MyFarmForm.vue';
import MyFarmSummary from '@/views/myfarm/components/MyFarmSummary.vue';
import { getMyFarm, createFarm, updateFarm } from '@/api/farm';

const mode = ref('empty');
const farm = ref(null);
const isEdit = ref(false);
const errorMessage = ref('');

const unwrapFarmData = (response) => {
  const body = response?.data;
  if (body && typeof body === 'object' && ('grade' in body || 'comment' in body || 'id' in body)) {
    return body;
  }
  return body?.data ?? null;
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

    // 농장 생성 또는 수정
    let createOrUpdateResponse;
    if (isEdit.value) {
      createOrUpdateResponse = await updateFarm(payload);
      console.log('✏️ 수정 응답:', createOrUpdateResponse);
    } else {
      createOrUpdateResponse = await createFarm(payload);
      console.log('➕ 생성 응답:', createOrUpdateResponse);
    }

    // 최신 농장 정보 다시 조회
    console.log('🔄 농장 정보 재조회 시작...');
    const response = await getMyFarm();
    console.log('📦 조회 응답 원본:', response);
    
    const farmData = unwrapFarmData(response);
    console.log('🎯 unwrap 후 데이터:', farmData);
    console.log('✅ hasFarm 체크:', hasFarm(farmData));

    if (hasFarm(farmData)) {
      farm.value = farmData;
      mode.value = 'summary';
      console.log('✨ summary 모드로 전환 완료');
    } else {
      farm.value = null;
      mode.value = 'empty';
      console.log('⚠️ 농장 데이터가 없어 empty 모드로 전환');
    }

    isEdit.value = false;
  } catch (e) {
    console.error('❌ 농장 저장 실패', e?.response ?? e);
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
.farm-content {
  min-height: 400px;
}
</style>