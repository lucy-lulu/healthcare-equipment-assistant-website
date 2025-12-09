// Enquiry status
export type EnquiryStatus = 'PENDING' | 'ANSWERED';

// Enquiry interface
export interface Enquiry {
  id: number;
  askId?: string;
  responderId?: string | null;
  question: string;
  answer: string | null;
  status: EnquiryStatus;
}

export interface EnquiryListResponse {
  content: Enquiry[];
  pageable: Pageable;
  totalElements: number;
  totalPages: number;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
}
