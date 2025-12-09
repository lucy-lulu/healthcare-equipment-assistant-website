import { useState } from 'react';
import { Order, OrderStatus } from '@/types/Order';
import { Card, Button, List, Tag, Space, Input, Modal, Select, Spin, Divider } from 'antd';
import { useOrderStore } from '@/store/order';
import { format } from 'date-fns';
import { useUserStore } from '@/store/user';
import { EyeOutlined, EditOutlined, MessageOutlined } from '@ant-design/icons';

const { TextArea } = Input;
const { Option } = Select;

interface OrderCardProps {
  order: Order;
}

const OrderCard: React.FC<OrderCardProps> = ({ order }) => {
  const { updateOrderStatus, fetchOrderDetails, addMessageToOrder, orderDetails } = useOrderStore();
  const user = useUserStore((state) => state.user);
  const [detailsVisible, setDetailsVisible] = useState(false);
  const [statusModalVisible, setStatusModalVisible] = useState(false);
  const [messageModalVisible, setMessageModalVisible] = useState(false);
  const [newStatus, setNewStatus] = useState<OrderStatus>(order.status);
  const [newMessage, setNewMessage] = useState('');
  const [loadingDetails, setLoadingDetails] = useState(false);

  const details = orderDetails[order.id];

  const getStatusColor = (status: OrderStatus) => {
    switch (status) {
      case 'PLACED':
        return 'blue';
      case 'PROCESSING':
        return 'orange';
      case 'SHIPPED':
        return 'cyan';
      case 'DELIVERED':
        return 'green';
      case 'CANCELLED':
        return 'red';
      default:
        return 'default';
    }
  };

  const getStatusText = (status: OrderStatus) => {
    switch (status) {
      case 'PLACED':
        return 'Placed';
      case 'PROCESSING':
        return 'Processing';
      case 'SHIPPED':
        return 'Shipped';
      case 'DELIVERED':
        return 'Delivered';
      case 'CANCELLED':
        return 'Cancelled';
      default:
        return status;
    }
  };

  const canManageOrder = () => {
    return user?.role === 'admin' || user?.role === 'sales';
  };

  const handleViewDetails = async () => {
    if (!details) {
      setLoadingDetails(true);
      await fetchOrderDetails(order.id);
      setLoadingDetails(false);
    }
    setDetailsVisible(true);
  };

  const handleUpdateStatus = async () => {
    await updateOrderStatus(order.id, newStatus);
    setStatusModalVisible(false);
  };

  const handleAddMessage = async () => {
    if (newMessage.trim()) {
      await addMessageToOrder(order.id, newMessage.trim());
      setMessageModalVisible(false);
      setNewMessage('');
      // 刷新详情以获取最新的comment
      if (details) {
        await fetchOrderDetails(order.id);
      }
    }
  };

  return (
    <>
      <Card className="mb-4 shadow-md">
        <div className="flex justify-between items-start mb-4">
          <div>
            <h3 className="text-lg font-semibold">Order #{order.id}</h3>
            <p className="text-gray-500">
              {format(new Date(order.orderDate), 'yyyy-MM-dd HH:mm')}
            </p>
            {/* <p className="text-sm text-gray-400">User ID: {order.userId}</p> */}
          </div>
          <Tag
            color={getStatusColor(order.status.toUpperCase() as OrderStatus)}
          >
            {getStatusText(order.status.toUpperCase() as OrderStatus)}
          </Tag>
        </div>

        <div className="mb-4 grid grid-cols-2 gap-4">
          <div>
            <p><strong>Total Amount:</strong> ${order.totalAmount.toFixed(2)}</p>
            {order.orderTrackingNumber && (
              <p><strong>Order Tracking Number:</strong> {order.orderTrackingNumber}</p>
            )}
          </div>
          <div>
            {order.shippingTrackingNumber && (
              <p><strong>Shipping Tracking Number:</strong> {order.shippingTrackingNumber}</p>
            )}
            {order.comment && (
              <p><strong>Comment:</strong> {order.comment}</p>
            )}
          </div>
        </div>

        <div className="flex justify-between items-center">
          <Space>
            <Button 
              icon={<EyeOutlined />} 
              onClick={handleViewDetails}
              loading={loadingDetails}
            >
              View Details
            </Button>
            {canManageOrder() && (
              <>
                <Button 
                  icon={<EditOutlined />} 
                  onClick={() => setStatusModalVisible(true)}
                >
                  Update Status
                </Button>
                <Button 
                  icon={<MessageOutlined />} 
                  onClick={() => setMessageModalVisible(true)}
                >
                  Add Comment
                </Button>
              </>
            )}
          </Space>
        </div>
      </Card>

      {/* 订单详情Modal */}
      <Modal
        title={`Order Details #${order.id}`}
        open={detailsVisible}
        onCancel={() => setDetailsVisible(false)}
        footer={null}
        width={800}
      >
        {details ? (
          <div>
            <div className="mb-4">
              <h4 className="font-medium mb-2">Basic Information</h4>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p>
                    <strong>Order Date:</strong>{' '}
                    {format(new Date(details.orderDate), 'yyyy-MM-dd HH:mm')}
                  </p>
                  <p>
                    <strong>Status:</strong>{' '}
                    <Tag color={order.status.toUpperCase() as OrderStatus}>
                      {getStatusText(order.status.toUpperCase() as OrderStatus)}
                    </Tag>
                  </p>
                  <p>
                    <strong>Total Amount:</strong> $
                    {details.totalAmount.toFixed(2)}
                  </p>
                </div>
                <div>
                  {/* <p><strong>User ID:</strong> {details.userId}</p> */}
                  {details.orderTrackingNumber && <p><strong>Order Tracking Number:</strong> {details.orderTrackingNumber}</p>}
                  {details.shippingTrackingNumber && <p><strong>Shipping Tracking Number:</strong> {details.shippingTrackingNumber}</p>}
                </div>
              </div>
            </div>

            <Divider />

            <div className="mb-4">
              <h4 className="font-medium mb-2">Order Items</h4>
              <List
                dataSource={details.items}
                renderItem={(item) => (
                  <List.Item>
                    <div className="flex items-center justify-between w-full">
                      <div className="flex-1">
                        <p className="font-medium">{item.name}</p>
                        <p className="text-gray-500">SKU: {item.sku}</p>
                        <p className="text-gray-500">
                          Quantity: {item.quantity} × ${item.price.toFixed(2)}
                        </p>
                      </div>
                      <p className="font-medium text-lg">
                        ${(item.quantity * item.price).toFixed(2)}
                      </p>
                    </div>
                  </List.Item>
                )}
              />
            </div>

            {details.comment && (
              <>
                <Divider />
                <div>
                  <h4 className="font-medium mb-2">Comments</h4>
                  <div className="bg-gray-50 p-3 rounded">
                    {details.comment.split('\n---\n').map((comment, index) => (
                      <p key={index} className="mb-2 last:mb-0">
                        {comment}
                      </p>
                    ))}
                  </div>
                </div>
              </>
            )}
          </div>
        ) : (
          <div className="text-center py-8">
            <Spin size="large" />
            <p className="mt-2">Loading order details...</p>
          </div>
        )}
      </Modal>

      {/* 更新状态Modal */}
      {canManageOrder() && (
        <Modal
          title="Update Order Status"
          open={statusModalVisible}
          onOk={handleUpdateStatus}
          onCancel={() => setStatusModalVisible(false)}
          okText="Confirm"
          cancelText="Cancel"
        >
          <div className="mb-4">
            <p className="mb-2">
              Current Status:{' '}
              <Tag
                color={getStatusColor(
                  order.status.toString().toUpperCase() as OrderStatus
                )}
              >
                {getStatusText(
                  order.status.toString().toUpperCase() as OrderStatus
                )}
              </Tag>
            </p>
            <p className="mb-2">Select New Status:</p>
            <Select
              value={newStatus}
              onChange={setNewStatus}
              style={{ width: '100%' }}
            >
              <Option value="placed">Placed</Option>
              <Option value="processing">Processing</Option>
              <Option value="shipped">Shipped</Option>
              <Option value="delivered">Delivered</Option>
              <Option value="cancelled">Cancelled</Option>
            </Select>
          </div>
        </Modal>
      )}

      {/* 添加备注Modal */}
      {canManageOrder() && (
        <Modal
          title="Add Order Comment"
          open={messageModalVisible}
          onOk={handleAddMessage}
          onCancel={() => {
            setMessageModalVisible(false);
            setNewMessage('');
          }}
          okText="Add"
          cancelText="Cancel"
        >
          <div className="mb-4">
            <p className="mb-2">Please enter the comment to add:</p>
            <TextArea
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              rows={4}
              placeholder="Enter comment..."
            />
          </div>
        </Modal>
      )}
    </>
  );
};

export default OrderCard;
