<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <div class="brand-logo">
          <img src="@/assets/logo.png" alt="Logo" @error="handleLogoError" />
        </div>
        <div class="brand-info">
          <strong class="brand-name">Yachaerang</strong>
          <p class="brand-desc">데이터 분석 · 가격 대시보드 · AI 챗봇</p>
        </div>
      </div>

      <div class="footer-right">
        <nav class="legal-links">
          <a href="#">이용약관</a>
          <span class="dot">•</span>
          <a href="#">개인정보처리방침</a>
        </nav>
        <p class="copyright">© 2025 Farm Service. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
const handleLogoError = (e) => {
  e.target.style.display = 'none';
};
</script>

<style scoped>
.app-footer {
  width: 100%;
  background: linear-gradient(180deg, #ffffff 0%, #f8faf8 100%);
  border-top: 1px solid #e8ebe8;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 16px;
}

.brand-logo {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  overflow: hidden;
  display: grid;
  place-items: center;
}

.brand-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.brand-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.brand-name {
  font-size: 15px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: -0.3px;
}

.brand-desc {
  font-size: 12px;
  color: #6b7280;
  letter-spacing: -0.2px;
}

/* NavBar.vue와의 간격 조정 */
.footer-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.legal-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.legal-links a {
  font-size: 12px;
  color: #6b7280;
  text-decoration: none;
  transition: color 0.2s ease;
}

.legal-links a:hover {
  color: #fecc21;
}

.dot {
  font-size: 8px;
  color: #d1d5db;
}

.copyright {
  font-size: 11px;
  color: #9ca3af;
}

/* 반응형 */
@media (max-width: 640px) {
  .footer-inner {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .footer-brand {
    flex-direction: column;
    text-align: center;
  }

  .footer-right {
    align-items: center;
  }
}
</style>
