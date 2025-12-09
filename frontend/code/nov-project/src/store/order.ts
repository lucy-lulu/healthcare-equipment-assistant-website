import { create } from 'zustand';
import { Order, OrderFilter, OrderStatus, OrderDetails } from '@/types/Order';
import { 
  getAllOrders, 
  getMyOrders, 
  searchOrdersByPartnerName, 
  getOrderDetails,
  updateOrderStatus as apiUpdateOrderStatus,
  addOrderMessage 
} from '@/api/api';

interface OrderState {
  orders: Order[];
  orderDetails: { [key: number]: OrderDetails };
  filter: OrderFilter;
  loading: boolean;
  pagination: {
    current: number;
    pageSize: number;
    total: number;
  };
  // Actions
  setOrders: (orders: Order[]) => void;
  setFilter: (filter: Partial<OrderFilter>) => void;
  setLoading: (loading: boolean) => void;
  setPagination: (pagination: Partial<OrderState['pagination']>) => void;
  // API actions
  fetchAllOrders: (cursor?: number, size?: number) => Promise<void>;
  fetchMyOrders: () => Promise<void>;
  fetchOrdersByPartnerName: (name: string) => Promise<void>;
  fetchOrderDetails: (orderId: number) => Promise<OrderDetails | null>;
  updateOrderStatus: (orderId: number, status: OrderStatus) => Promise<void>;
  addMessageToOrder: (orderId: number, message: string) => Promise<void>;
  // Utilities
  getFilteredOrders: () => Order[];
}

export const useOrderStore = create<OrderState>((set, get) => ({
  orders: [],
  orderDetails: {},
  filter: {
    status: 'ALL',
    searchText: '',
  },
  loading: false,
  pagination: {
    current: 0,
    pageSize: 10,
    total: 0,
  },
  
  // Basic setters
  setOrders: (orders) => set({ orders }),
  setFilter: (filter) => set((state) => ({ filter: { ...state.filter, ...filter } })),
  setLoading: (loading) => set({ loading }),
  setPagination: (pagination) => set((state) => ({ 
    pagination: { ...state.pagination, ...pagination } 
  })),

  // API actions
  fetchAllOrders: async (cursor = 0, size = 10) => {
    set({ loading: true });
    try {
      const response = await getAllOrders(cursor, size);
      console.log('getAllOrders response:', response); // 调试日志
      if (response.success && response.data) {
        const pageData = response.data;
        set({ 
          orders: pageData.content || [],
          pagination: {
            current: pageData.pageable?.pageNumber || 0,
            pageSize: pageData.pageable?.pageSize || size,
            total: pageData.totalElements || 0,
          }
        });
      } else {
        console.warn('getAllOrders: response not successful or no data', response);
        set({ orders: [] });
      }
    } catch (error) {
      console.error('Failed to fetch orders:', error);
      set({ orders: [] }); // 确保在错误时重置订单列表
    } finally {
      set({ loading: false });
    }
  },

  fetchMyOrders: async () => {
    set({ loading: true });
    try {
      const response = await getMyOrders();
      console.log('getMyOrders response:', response); // 调试日志
      if (response.success && response.data) {
        set({ orders: response.data });
      } else {
        console.warn('getMyOrders: response not successful or no data', response);
        set({ orders: [] });
      }
    } catch (error) {
      console.error('Failed to fetch my orders:', error);
      set({ orders: [] }); // 确保在错误时重置订单列表
    } finally {
      set({ loading: false });
    }
  },

  fetchOrdersByPartnerName: async (name: string) => {
    set({ loading: true });
    try {
      const response = await searchOrdersByPartnerName(name);
      if (response.success && response.data) {
        set({ orders: response.data });
      }
    } catch (error) {
      console.error('Failed to search orders by partner name:', error);
    } finally {
      set({ loading: false });
    }
  },

  fetchOrderDetails: async (orderId: number) => {
    try {
      const response = await getOrderDetails(orderId);
      if (response.success && response.data) {
        set((state) => ({
          orderDetails: {
            ...state.orderDetails,
            [orderId]: response.data!
          }
        }));
        return response.data;
      }
      return null;
    } catch (error) {
      console.error('Failed to fetch order details:', error);
      return null;
    }
  },

  updateOrderStatus: async (orderId: number, status: OrderStatus) => {
    try {
      const response = await apiUpdateOrderStatus(orderId, { status });
      if (response.success && response.data) {
        set((state) => ({
          orders: state.orders.map((order) =>
            order.id === orderId ? { ...order, status } : order
          ),
        }));
      }
    } catch (error) {
      console.error('Failed to update order status:', error);
    }
  },

  addMessageToOrder: async (orderId: number, message: string) => {
    try {
      const response = await addOrderMessage(orderId, { message });
      if (response.success && response.data) {
        set((state) => ({
          orders: state.orders.map((order) =>
            order.id === orderId ? { ...order, comment: response.data!.comment } : order
          ),
        }));
      }
    } catch (error) {
      console.error('Failed to add message to order:', error);
    }
  },

  // Filter utility
  getFilteredOrders: () => {
    const { orders, filter } = get();
    return orders.filter((order) => {
      const matchesStatus = filter.status === 'ALL' || order.status.toUpperCase() === filter.status;
      const matchesSearch = filter.searchText
        ? order.id.toString().includes(filter.searchText) ||
          (order.orderTrackingNumber && order.orderTrackingNumber.toLowerCase().includes(filter.searchText.toLowerCase())) ||
          (order.shippingTrackingNumber && order.shippingTrackingNumber.toLowerCase().includes(filter.searchText.toLowerCase()))
        : true;
      return matchesStatus && matchesSearch;
    });
  },
})); 