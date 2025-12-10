<template>
  <button class="move-btn" @click="$emit('click')">상세 검색으로 이동</button>
</template>

<style scoped>
.move-btn {
  margin: 40px auto 0;
  display: block;
  padding: 14px 24px;
  border-radius: 24px;
  background: #e53935;
  color: #fff;
  border: none;
  cursor: pointer;
}
</style>
