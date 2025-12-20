import { defineStore } from 'pinia';

export const useNavigationStore = defineStore('navigation', {
  state: () => ({
    activeSectionIndex: 0,
    isScrolling: false,
  }),
  actions: {
    setActiveSection(index) {
      this.activeSectionIndex = index;
    },
    setScrolling(status) {
      this.isScrolling = status;
    }
  },
});