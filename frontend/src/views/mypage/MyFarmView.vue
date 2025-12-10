<template>
  <div>
    <!-- 농장 아직 없음 -->
    <MyFarmEmpty v-if="mode === 'empty'" @click-register="mode = 'form'" />

    <!-- 농장 등록 폼 -->
    <MyFarmForm v-else-if="mode === 'form'" @submitted="handleRegistered" @cancel="handleCancel" />

    <!-- 농장 이미 등록됨 -->
    <MyFarmSummary v-else-if="mode === 'summary'" :farm="farm" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import MyFarmEmpty from '@/views/mypage/components/MyFarmEmpty.vue';
import MyFarmForm from '@/views/mypage/components/MyFarmForm.vue';
import MyFarmSummary from '@/views/mypage/components/MyFarmSummary.vue';

const mode = ref('empty');
const farm = ref(null);

onMounted(async () => {
  // TODO:
});

const handleRegistered = (payload) => {
  farm.value = payload;
  mode.value = 'summary';
};

const handleCancel = () => {
  mode.value = 'empty';
};
</script>
