import { defineStore } from 'pinia';
import { fetchItemsApi, fetchSubItemsApi } from '@/api/price';

export const useProductNameStore = defineStore('productName', {
  state: () => ({
    initialized: false,
    loading: false,
    nameMap: {},
  }),

  actions: {
    async loadProductNames(favoritesList = []) {
      if (this.initialized) return this.nameMap;

      if (this.loading) return this.nameMap;

      const productCodes = new Set(
        favoritesList.map((f) => f.productCode).filter((code) => typeof code === 'string' && code.trim() !== '')
      );

      if (productCodes.size === 0) {
        this.initialized = true;
        return this.nameMap;
      }

      this.loading = true;
      let loadedCount = 0;

      try {
        const itemsRes = await fetchItemsApi();
        const body = itemsRes.data;
        const items = Array.isArray(body) ? body : Array.isArray(body?.data) ? body.data : [];

        await Promise.all(
          items.map(async (item) => {
            const itemCode = item.itemCode ?? item.code ?? item.id;
            const itemName = item.itemName ?? item.name ?? '';
            if (!itemCode) return;

            try {
              const subRes = await fetchSubItemsApi(itemCode);
              const subBody = subRes.data;
              const subs = Array.isArray(subBody) ? subBody : Array.isArray(subBody?.data) ? subBody.data : [];

              subs.forEach((sub) => {
                const productCode = sub.productCode ?? sub.code ?? sub.id;
                if (!productCodes.has(productCode)) return;

                this.nameMap[productCode] = {
                  itemName,
                  varietyName: sub.subItemName ?? sub.name ?? sub.productName ?? '',
                };

                loadedCount++;
              });
            } catch (e) {
              console.error('하위 품목 조회 실패:', e);
            }
          })
        );

        if (loadedCount > 0) {
          this.initialized = true;
        }
      } catch (e) {
        console.error('품목 전체 조회 실패:', e);
      } finally {
        this.loading = false;
      }

      return this.nameMap;
    },

    reset() {
      this.initialized = false;
      this.loading = false;
      this.nameMap = {};
    },
  },
});
