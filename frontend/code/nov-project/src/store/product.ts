import { Category, Product } from '@/types/Product';
import { create } from 'zustand';

interface ProductState {
  resetState: () => void;

  productPageList: Product[] | null;
  categoryMap: Map<number, Category> | null;
}

export const useProductStore = create<ProductState>()((set) => {
  const initState = {
    productPageList: null,
    categoryMap: null,
  } as ProductState;

  return {
    ...initState,
    resetState: () => {
      set({ ...initState });
    },
  };
});
