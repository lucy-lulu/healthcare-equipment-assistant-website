import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { Product } from '@/types/Product';

export interface CartItem {
  productId: number;
  qty: number;
  product: Product;
}

interface CartState {
  items: CartItem[];
  addItem: (p: Product, qty: number) => void;
  updateQty: (productId: number, qty: number) => void;
  removeItem: (productId: number) => void;
  clear: () => void;
}

export const useCartStore = create<CartState>()(
  persist(
    (set) => ({
      items: [],

      addItem: (p, qty) => {
        set((state) => {
          const exist = state.items.find((i) => i.productId === p.id);
          if (exist) {
            return {
              items: state.items.map((i) =>
                i.productId === p.id
                  ? { ...i, qty: i.qty + qty, product: p }
                  : i
              ),
            };
          }
          const newItem: CartItem = {
            productId: p.id,
            qty,
            product: p,
          };
          return { items: [...state.items, newItem] };
        });
      },

      updateQty: (productId, qty) =>
        set((state) => ({
          items: state.items.map((i) =>
            i.productId === productId ? { ...i, qty: Math.max(1, qty) } : i
          ),
        })),

      removeItem: (productId) =>
        set((state) => ({
          items: state.items.filter((i) => i.productId !== productId),
        })),

      clear: () => set({ items: [] }),
    }),
    { name: 'cart:v1' }
  )
);
