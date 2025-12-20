import { defineStore } from 'pinia';

export const useNavigationStore = defineStore('navigation', {
  state: () => ({
    activeSectionIndex: 0,
    isScrolling: false, // Locks input during animation
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