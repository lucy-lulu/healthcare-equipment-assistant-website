// API文档中定义的订单状态
export type OrderStatus = 'PLACED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

// 订单项接口 - 对应API中的ItemInOrderDto
export interface OrderItem {
  productId: number;
  sku: string;
  name: string;
  price: number;
  quantity: number;
}

// 基础订单接口 - 对应API中的Order实体
export interface Order {
  id: number;
  userId: string;
  orderDate: string;
  status: OrderStatus;
  totalAmount: number;
  orderTrackingNumber: string | null;
  shippingTrackingNumber: string | null;
  comment: string | null;
}

// 详细订单接口 - 对应API中的OrderDetailsDto
export interface OrderDetails extends Order {
  items: OrderItem[];
}

// 创建订单请求接口
export interface PlaceOrderRequest {
  items: {
    productId: number;
    quantity: number;
  }[];
  comment?: string;
}

// 更新订单状态请求接口
export interface UpdateOrderStatusRequest {
  status: OrderStatus;
}

// 添加订单消息请求接口
export interface AddOrderMessageRequest {
  message: string;
}

// 分页响应接口
export interface OrderPageResponse {
  content: Order[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

// 订单过滤器接口
export interface OrderFilter {
  status: OrderStatus | 'ALL';
  searchText: string;
}

// 保持向后兼容的旧接口别名
/** @deprecated 请使用OrderItem */
export type OrderProduct = OrderItem; 