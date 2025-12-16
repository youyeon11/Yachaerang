<template>
  <div class="farm-form">
    <h1 class="page-title">나의 농장</h1>

    <table class="farm-table">
      <tbody>
        <tr>
          <th>인력</th>
          <td>
            <input v-model="form.manpower" type="number" class="input" />
          </td>
          <td class="unit">명</td>
        </tr>
        <tr>
          <th>농장위치</th>
          <td colspan="2">
            <input v-model="form.location" type="text" class="input" placeholder="주소" />
          </td>
        </tr>
        <tr>
          <th>주품목</th>
          <td colspan="2">
            <input v-model="form.mainCrop" type="text" class="input" placeholder="주품목" />
          </td>
        </tr>
        <tr>
          <th>평지면적</th>
          <td>
            <input v-model="form.flatArea" type="number" class="input" />
          </td>
          <td class="unit">m²</td>
        </tr>
        <tr>
          <th>경작면적</th>
          <td>
            <input v-model="form.cultivatedArea" type="number" class="input" />
          </td>
          <td class="unit">m²</td>
        </tr>
      </tbody>
    </table>

    <div class="farm-actions">
      <button class="btn primary" @click="handleSubmit">
        {{ isEdit ? '수정하기' : '등록하기' }}
      </button>
      <button class="btn secondary" @click="openConfirm">다음에 입력하기</button>
    </div>
  </div>

  <div v-if="showConfirm" class="modal-backdrop">
    <div class="modal">
      <p class="modal-message">
        작성하신 것들이 저장되지 않습니다.<br />
        그래도 취소하시겠습니까?
      </p>
      <div class="modal-actions">
        <button class="btn primary" @click="confirmKeepEditing">이어작성하기</button>
        <button class="btn secondary" @click="confirmCancel">취소하기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';

const props = defineProps({
  initialFarm: {
    type: Object,
    default: null,
  },
  isEdit: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['submitted', 'cancel']);

const form = reactive({
  manpower: props.initialFarm?.manpower ?? '',
  location: props.initialFarm?.location ?? '',
  mainCrop: props.initialFarm?.mainCrop ?? '',
  flatArea: props.initialFarm?.flatArea ?? '',
  cultivatedArea: props.initialFarm?.cultivatedArea ?? '',
});

const showConfirm = ref(false);

const handleSubmit = () => {
  const payload = {
    manpower: form.manpower === '' ? 0 : Number(form.manpower),
    location: !form.location || form.location.trim() === '' ? '없음' : form.location,
    mainCrop: !form.mainCrop || form.mainCrop.trim() === '' ? '없음' : form.mainCrop,
    flatArea: form.flatArea === '' ? 0 : Number(form.flatArea),
    cultivatedArea: form.cultivatedArea === '' ? 0 : Number(form.cultivatedArea),
  };
  emit('submitted', payload);
};

const openConfirm = () => {
  showConfirm.value = true;
};

const confirmKeepEditing = () => {
  showConfirm.value = false;
};

const confirmCancel = () => {
  showConfirm.value = false;
  emit('cancel');
};
</script>

<style scoped>
.page-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 24px;
}

.farm-table {
  width: 100%;
  border-collapse: collapse;
  border-top: 1px solid #ddd;
  border-bottom: 1px solid #ddd;
}

.farm-table th,
.farm-table td {
  padding: 10px 12px;
  font-size: 14px;
  border-top: 1px solid #eee;
}

.farm-table th {
  width: 140px;
  text-align: left;
  background: #fafafa;
}

.farm-table tr:first-child th,
.farm-table tr:first-child td {
  border-top: none;
}

.input {
  width: 100%;
  padding: 8px 10px;
  border-radius: 4px;
  border: 1px solid #ddd;
  font-size: 14px;
  box-sizing: border-box;
}

.unit {
  width: 60px;
  text-align: left;
  font-size: 13px;
  color: #666;
}

.farm-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn {
  flex: 1;
  padding: 10px 0;
  border-radius: 6px;
  font-size: 14px;
  border: none;
  cursor: pointer;
}

.btn.primary {
  background: #fecc21;
}

.btn.secondary {
  background: #bbb;
  color: #fff;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 40;
}

.modal {
  background: #fff;
  border-radius: 12px;
  padding: 24px 28px 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  text-align: center;
  min-width: 260px;
}

.modal-message {
  margin: 0 0 18px;
  font-size: 14px;
  line-height: 1.6;
}

.modal-actions {
  display: flex;
  gap: 8px;
}
</style>
