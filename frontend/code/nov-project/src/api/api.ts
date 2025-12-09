import { resetAllStoreStates } from '@/store/bus';
import useCommonStore from '@/store/common';
import { useUserStore } from '@/store/user';
import { AiResponse, CommonResponse } from '@/types/CommonHttp';
import { Category, Product, ProductListResponse } from '@/types/Product';
import { LoginRequestParam, UserDetail } from '@/types/User';
import {
  Order,
  OrderDetails,
  OrderPageResponse,
  PlaceOrderRequest,
  UpdateOrderStatusRequest,
  AddOrderMessageRequest,
} from '@/types/Order';
import { message } from 'antd';
import axios from 'axios';
import { Enquiry } from '@/types/Enquiry';

const VITE_API_URL = import.meta.env.VITE_API_URL;

const api = axios.create({
  baseURL: VITE_API_URL,
  timeout: 100000,
  headers: {
    'Content-Type': 'application/json',
  },
});

const aiapi = axios.create({
  baseURL: 'http://3.106.188.11:8000',
  timeout: 100000,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const user = useUserStore.getState().user;
    if (user) {
      config.headers.Authorization = `Bearer ${user.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // 自定义错误处理逻辑
    if (error.response && error.response.status === 401) {
      // 401 未授权逻辑
      message.error('unauthorized, please login', 2);
      // clear all stores
      resetAllStoreStates();
      // 比如跳转到登录页面
      useCommonStore.getState().navigate('/login');
    }
    if (error.response && error.response.status === 403) {
      // 403 權限不足逻辑
      message.error('權限不足', 2);
    }
    // 其他错误仍然继续抛出
    return Promise.reject(error);
  }
);

// 用戶校驗模塊
export const login = async (param: LoginRequestParam) => {
  const response = await api.post('/auth/login', param);
  return response.data as CommonResponse<UserDetail>;
};

export const listAllCategories = async () => {
  const response = await api.get('/categories/allcategories');
  return response.data as CommonResponse<Category[]>;
};

export const getProducts = async (data: { cursor: number; size: number }) => {
  const response = await api.get('/products', { params: data });
  return response.data as CommonResponse<ProductListResponse>;
};

export const getProductDetail = async (id: number) => {
  const response = await api.get(`/products/${id}`);
  return response.data as CommonResponse<Product>;
};

export const searchProducts = async (query?: string, category?: number) => {
  const response = await api.get('/products/search', {
    params: { query, category },
  });
  return response.data as CommonResponse<Product[]>;
};

export const getAllCategories = async () => {
  const response = await api.get('/categories');
  return response.data as CommonResponse<Category[]>;
};

// 订单管理 API 接口

// 获取所有订单（分页）- 管理员权限
export const getAllOrders = async (cursor: number = 0, size: number = 10) => {
  const response = await api.get('/orders', { params: { cursor, size } });
  return response.data as CommonResponse<OrderPageResponse>;
};

// 获取我的订单
export const getMyOrders = async () => {
  const response = await api.get('/orders/my');
  return response.data as CommonResponse<Order[]>;
};

// 按合作伙伴用户名搜索订单
export const searchOrdersByPartnerName = async (name: string) => {
  const response = await api.get('/orders/search', { params: { name } });
  return response.data as CommonResponse<Order[]>;
};

// 获取订单详情
export const getOrderDetails = async (orderId: number) => {
  const response = await api.get(`/orders/${orderId}`);
  return response.data as CommonResponse<OrderDetails>;
};

// 创建订单
export const placeOrder = async (orderRequest: PlaceOrderRequest) => {
  const response = await api.post('/orders/place', orderRequest);
  return response.data as CommonResponse<OrderDetails>;
};

// 更新订单状态
export const updateOrderStatus = async (
  orderId: number,
  statusRequest: UpdateOrderStatusRequest
) => {
  const response = await api.post(`/orders/${orderId}/status`, statusRequest);
  return response.data as CommonResponse<Order>;
};

// 添加订单消息
export const addOrderMessage = async (
  orderId: number,
  messageRequest: AddOrderMessageRequest
) => {
  const response = await api.post(`/orders/${orderId}/message`, messageRequest);
  return response.data as CommonResponse<Order>;
};

// Enquiry
export const sendEnquiry = async (question: string) => {
  const response = await api.post('/enquiries/send', { question });
  return response.data as CommonResponse<Enquiry>;
};

// (ADMIN) Get all enquiries
export const getAllEnquiries = async (
  cursor: number = 0,
  size: number = 10
) => {
  const response = await api.get('/enquiries', { params: { cursor, size } });
  return response.data as CommonResponse<Enquiry[]>;
};

export const getMyEnquiries = async (cursor: number = 0, size: number = 10) => {
  const response = await api.get('/enquiries/my', { params: { cursor, size } });
  return response.data as CommonResponse<Enquiry[]>;
};

export const replyEnquiry = async (
  enquiryId: number,
  answer: string
) => {
  const response = await api.post(`/enquiries/${enquiryId}/reply`, { answer });
  return response.data as CommonResponse<Enquiry>;
};

export const aiSearch = async (
  session_id: string,
  message: string,
  top_k: number
) => {
  const response = await aiapi.post('/chat', {
    session_id,
    message,
    top_k,
  });
  return response.data as AiResponse;
};

// export const logout = async () => {
//   const response = await api.post('/auth/logout');
//   return response.data as CommonResponse<never>;
// };

export default api;
