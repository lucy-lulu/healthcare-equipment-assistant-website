import { useEffect, useState } from 'react';
import { Input, Menu, Pagination, Spin, Button, Space, message } from 'antd';
import {
  SearchOutlined,
  ReloadOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { useOrderStore } from '@/store/order';
import { useUserStore } from '@/store/user';
import OrderCard from '@/components/OrderCard';
import { OrderStatus } from '@/types/Order';

const OrderPage = () => {
  const user = useUserStore((state) => state.user);
  const { 
    filter, 
    loading, 
    pagination,
    setFilter,
    getFilteredOrders,
    fetchAllOrders,
    fetchMyOrders,
    fetchOrdersByPartnerName,
  } = useOrderStore();

  const [partnerSearchValue, setPartnerSearchValue] = useState('');

  // 根据用户角色决定加载哪些订单
  const loadOrders = async (page = 0, size = 10) => {
    if (user?.role === 'admin' || user?.role === 'sales') {
      await fetchAllOrders(page, size);
    } else {
      await fetchMyOrders();
    }
  };

  useEffect(() => {
    loadOrders();
  }, [user]);

  const handleStatusChange = (status: OrderStatus | 'ALL') => {
    setFilter({ status });
  };

  const handleSearch = (value: string) => {
    setFilter({ searchText: value });
  };

  const handlePartnerSearch = async () => {
    if (!partnerSearchValue.trim()) {
      message.warning('Please enter partner username');
      return;
    }
    try {
      await fetchOrdersByPartnerName(partnerSearchValue.trim());
      message.success('Search completed');
    } catch (error) {
      message.error('Search failed');
    }
  };

  const handlePaginationChange = (page: number, pageSize?: number) => {
    const newPage = page - 1; // API uses 0-based page numbers
    const newSize = pageSize || pagination.pageSize;
    loadOrders(newPage, newSize);
  };

  const handleRefresh = () => {
    loadOrders(pagination.current, pagination.pageSize);
  };

  const canSearchByPartner = () => {
    return user?.role === 'admin' || user?.role === 'sales';
  };

  const filteredOrders = getFilteredOrders();

  return (
    <div className="flex h-full">
      {/* Left Sidebar */}
      <div className="w-64 bg-white p-4 border-r">
        <div className="mb-4">
          <h3 className="font-semibold mb-2">Order Status</h3>
          <Menu
            mode="inline"
            selectedKeys={[filter.status]}
            onClick={({ key }) => handleStatusChange(key as OrderStatus | 'ALL')}
            items={[
              { key: 'ALL', label: 'All Orders' },
              { key: 'PLACED', label: 'Placed' },
              { key: 'PROCESSING', label: 'Processing' },
              { key: 'SHIPPED', label: 'Shipped' },
              { key: 'DELIVERED', label: 'Delivered' },
              { key: 'CANCELLED', label: 'Cancelled' },
            ]}
          />
        </div>

        {canSearchByPartner() && (
          <div className="border-t pt-4">
            <h3 className="font-semibold mb-2">Partner Search</h3>
            <div className="space-y-2">
              <Input
                placeholder="Enter username"
                value={partnerSearchValue}
                onChange={(e) => setPartnerSearchValue(e.target.value)}
                prefix={<UserOutlined />}
              />
              <Button
                type="primary"
                block
                onClick={handlePartnerSearch}
                loading={loading}
              >
                Search Orders
              </Button>
            </div>
          </div>
        )}
      </div>

      {/* Main Content */}
      <div className="flex-1 p-6">
        {/* Header */}
        <div className="mb-6 flex justify-between items-center">
          <div className="flex-1">
            <Input
              placeholder="Search by Order ID or tracking number"
              prefix={<SearchOutlined />}
              onChange={(e) => handleSearch(e.target.value)}
              className="max-w-md"
            />
          </div>
          <Space>
            <Button 
              icon={<ReloadOutlined />} 
              onClick={handleRefresh}
              loading={loading}
            >
              Refresh
            </Button>
          </Space>
        </div>

        {/* Content */}
        <Spin spinning={loading}>
          <div className="space-y-4 min-h-[400px]">
            {filteredOrders.length > 0 ? (
              filteredOrders.map((order) => (
                <OrderCard key={order.id} order={order} />
              ))
            ) : (
              <div className="text-center py-16 text-gray-500">
                {loading ? 'Loading...' : 'No orders found'}
              </div>
            )}
          </div>
        </Spin>

        {/* Pagination - Only shown for admin/sales roles */}
        {(user?.role === 'admin' || user?.role === 'sales') && pagination.total > 0 && (
          <div className="mt-6 flex justify-center">
            <Pagination
              current={pagination.current + 1} // UI uses 1-based page numbers
              pageSize={pagination.pageSize}
              total={pagination.total}
              onChange={handlePaginationChange}
              showSizeChanger
              showQuickJumper
              showTotal={(total, range) => `${range[0]}-${range[1]} of ${total} items`}
              pageSizeOptions={['10', '20', '50', '100']}
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default OrderPage;
