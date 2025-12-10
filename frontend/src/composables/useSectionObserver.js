import { onMounted, onBeforeUnmount, ref } from 'vue';

export function useSectionObserver() {
  const isVisible = ref(false);
  let observer = null;

  const observe = (el) => {
    if (!el) return;

    observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          isVisible.value = true;
          observer.disconnect();
        }
      },
      { threshold: 0.2 }
    );

    observer.observe(el);
  };

  onBeforeUnmount(() => {
    if (observer) observer.disconnect();
  });

  return { isVisible, observe };
}
