import { getMyOrders } from '@/api/api';
import { useUserStore } from '@/store/user';
import React, { useEffect, useMemo, useState } from 'react';
import {
  Card,
  Progress,
  Typography,
  Space,
  Statistic,
  Descriptions,
  Button,
  message,
  Empty,
} from 'antd';
import { Order } from '@/types/Order';
import Table, { ColumnsType } from 'antd/es/table';

const { Title, Text } = Typography;

// TODOs: level thresholds can be configured in backend
const LEVEL_THRESHOLDS = [0, 10000, 15000, 20000, Infinity];

const MyAccount: React.FC = () => {
  const user = useUserStore((s) => s.user);

  const [royalty, setRoyalty] = useState(0);
  const [loading, setLoading] = useState(false);
  const [orders, setOrders] = useState<Order[]>([]);

  async function fetchMyOrder() {
    try {
      setLoading(true);
      const response = await getMyOrders();
      if (!response.success) {
        message.error(response?.message ?? 'Unknown', 3);
        return;
      }
      setOrders(response.data || []);
      let totalRoyalty = 0;
      for (const order of response.data) {
        totalRoyalty += order.totalAmount || 0;
      }
      setRoyalty(Math.floor(totalRoyalty));
    } catch (err) {
      console.log('Failed to fetch orders:', err);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (user) fetchMyOrder();
  }, [user]);

  const orderCount = orders.length;

  const progressInfo = useMemo(() => {
    const level = Math.min(Math.max(user?.level ?? 1, 1), 4);
    const curMin = LEVEL_THRESHOLDS[level - 1];
    const next = LEVEL_THRESHOLDS[level];
    if (!isFinite(next)) {
      return {
        level,
        percent: 100,
        nextLabel: 'Top level',
        remainingLabel: 'You are at the highest level 🎉',
      };
    }
    const span = next - curMin;
    const progressed = Math.max(0, royalty - curMin);
    const percent = Math.max(
      0,
      Math.min(100, Math.round((progressed / span) * 100))
    );
    const remaining = Math.max(0, next - royalty);
    return {
      level,
      percent,
      nextLabel: `Level ${level + 1}`,
      remainingLabel: `${remaining} more to reach ${`Level ${level + 1}`}`,
    };
  }, [royalty, user?.level]);

  // Recent order point change
  const columns: ColumnsType<Order> = [
    {
      title: 'Order ID',
      dataIndex: 'id',
      key: 'id',
      width: 120,
      render: (id: number) => <Text strong>#{id}</Text>,
    },
    {
      title: 'Date',
      dataIndex: 'orderDate',
      key: 'orderDate',
      width: 180,
      render: (iso?: string) =>
        iso ? new Date(iso).toLocaleDateString() : '-',
    },

    {
      title: 'Amount',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      width: 140,
      align: 'right',
      render: (v: number) => <span>{v ?? 0}</span>,
    },
    {
      title: 'Points',
      key: 'points',
      width: 140,
      align: 'right',
      render: (_, o) => {
        const delta = o.totalAmount;
        const sign = delta > 0 ? '+' : delta < 0 ? '-' : '';
        const val = Math.abs(delta);
        return (
          <span
            className={
              delta > 0
                ? 'text-green-600'
                : delta < 0
                ? 'text-red-600'
                : 'text-gray-500'
            }
          >
            {sign}
            {val}
          </span>
        );
      },
    },
  ];

  return (
    <div className="container mx-auto max-w-5xl px-6 py-8">
      <div className="flex items-center justify-between">
        <Title level={2} className="!mb-0">
          My Account
        </Title>
        <Button onClick={fetchMyOrder} loading={loading}>
          Refresh
        </Button>
      </div>

      <div className="grid grid-cols-12 gap-6 mt-6">
        <div className="col-span-12 md:col-span-6 flex">
          <Card
            title="Profile"
            styles={{ body: { padding: 16 } }}
            className="rounded-2xl flex-1"
          >
            <Descriptions column={1} colon>
              <Descriptions.Item label="Username">
                {user?.username ?? '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Email">
                {user?.email ?? '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Level">
                {user?.level ?? 1}
              </Descriptions.Item>
              <Descriptions.Item label="Role">
                {user?.role.toUpperCase()}
              </Descriptions.Item>
              <Descriptions.Item label="Orders">{orderCount}</Descriptions.Item>
            </Descriptions>
          </Card>
        </div>

        {/* {user?.role !== 'admin' && ( */}
        <>
          <div className="col-span-12 md:col-span-6 flex">
            <Card
              title="Membership progress"
              styles={{ body: { padding: 16 } }}
              className="rounded-2xl flex-1"
            >
              <div className="flex items-center gap-6">
                <Progress type="dashboard" percent={progressInfo.percent} />
                <Space direction="vertical" size={4} style={{ marginTop: 10 }}>
                  <Statistic title="Total spend / royalty" value={royalty} />
                  <Text type="secondary">{progressInfo.remainingLabel}</Text>
                  <Text>Next: {progressInfo.nextLabel}</Text>
                </Space>
              </div>
            </Card>
          </div>{' '}
          {/* Orders list */}
          <div className="col-span-12">
            <Card
              title="Recent orders"
              styles={{ body: { padding: 0 } }}
              className="rounded-2xl"
            >
              {orders.length ? (
                <Table<Order>
                  columns={columns}
                  dataSource={orders}
                  rowKey="id"
                  pagination={{ pageSize: 5, size: 'small' }}
                />
              ) : (
                <div className="p-8">
                  <Empty description="No orders yet" />
                </div>
              )}
            </Card>
          </div>
        </>
        {/* )} */}
      </div>
    </div>
  );
};

export default MyAccount;
