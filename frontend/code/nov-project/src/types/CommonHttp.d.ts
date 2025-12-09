import { Product } from '@/types/Product';

export interface CommonResponse<T> {
  data: T;
  success: boolean;
  message: string;
}

interface CommonListResponseData<T> {
  list: T[];
}

interface AiResponse {
  questionText: string;
  session_id: string;
  reply: string;
  products: Product[];
}
