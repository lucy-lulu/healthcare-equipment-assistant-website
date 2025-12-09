import { NavigateFunction } from 'react-router-dom';
import { create } from 'zustand';

interface CommonState {
  navigate: NavigateFunction;
  selectedKeys: string[];
  currentSiderWidth: number;
}

const useCommonStore = create<CommonState>()(() => ({
  navigate: () => {},
  selectedKeys: [],
  currentSiderWidth: 320,
}));

export default useCommonStore;
