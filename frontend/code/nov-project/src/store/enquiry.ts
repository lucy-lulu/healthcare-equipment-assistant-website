import { Enquiry } from '@/types/Enquiry';
import { create } from 'zustand';

interface EnquiryState {
  resetState: () => void;

  enquiryPageList: Enquiry[] | null;
}

export const useEnquiryStore = create<EnquiryState>()((set) => {
  const initState = {
    enquiryPageList: null,
  } as EnquiryState;

  return {
    ...initState,
    resetState: () => {
      set({ ...initState });
    },
  };
});
