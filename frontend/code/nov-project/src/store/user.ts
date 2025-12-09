import { UserDetail } from '@/types/User';
import { create } from 'zustand';

interface UserState {
  resetState: () => void;

  user: UserDetail | null;
}

export const useUserStore = create<UserState>()((set) => {
  const initState = {
    user: null,
  } as UserState;

  return {
    ...initState,
    resetState: () => {
      set({ ...initState });
    },
  };
});
