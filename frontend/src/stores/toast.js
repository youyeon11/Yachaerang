import { defineStore } from 'pinia';

export const useToastStore = defineStore('toast', {
  state: () => ({
    message: '',
    type: 'info',
    visible: false,
    timerId: null,
  }),

  actions: {
    show(message, type = 'info') {
      if (this.timerId) {
        clearTimeout(this.timerId);
      }
      this.message = message;
      this.type = type;
      this.visible = true;

      this.timerId = setTimeout(() => {
        this.visible = false;
        this.timerId = null;
      }, 2500);
    },
  },
});
