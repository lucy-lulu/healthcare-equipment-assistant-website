import { useUserStore } from '@/store/user';

export const resetAllStoreStates = () => {
  useUserStore.getState().resetState();
  //more incoming stores reset func...
};
