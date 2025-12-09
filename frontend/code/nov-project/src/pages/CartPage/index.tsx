import React, { useMemo, useState } from 'react';
import {
  Button,
  Card,
  Divider,
  Empty,
  Typography,
  message,
  Image,
  InputNumber,
  Popconfirm,
  Table,
  Input,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { CloseOutlined } from '@ant-design/icons';
import { useUserStore } from '@/store/user';
import useCommonStore from '@/store/common';
import Paragraph from 'antd/es/typography/Paragraph';
import { useCartStore } from '@/store/cart';
import { Product } from '@/types/Product';
import { placeOrder } from '@/api/api';

const { Title, Text } = Typography;
const formatMoney = (n: number) => `$${n.toFixed(2)}`;

const getLevelPrice = (p: Product, level: number) => {
  const map: Record<number, number | undefined> = {
    4: p.price1,
    3: p.price2,
    2: p.price3,
    1: p.price4,
  };
  return map[level] ?? p.price1 ?? 0;
};

const firstImage = (p: Product) =>
  (p.images || '').split(',').map((s) => s.trim())[0] ?? '';

const CartPage: React.FC = () => {
  const userLevel = useUserStore((s) => s.user?.level ?? 1);

  const items = useCartStore((s) => s.items);
  const updateQty = useCartStore((s) => s.updateQty);
  const removeItem = useCartStore((s) => s.removeItem);

  type Row = {
    key: string;
    productId: number;
    name: string;
    image: string | null;
    qty: number;
    unitPrice: number;
    product: Product;
  };

  const rows: Row[] = useMemo(
    () =>
      items.map((it) => {
        const p = it.product;
        return {
          key: String(it.productId),
          productId: it.productId,
          name: p.name,
          image: firstImage(p),
          qty: it.qty,
          unitPrice: getLevelPrice(p, userLevel),
          product: p,
        };
      }),
    [items, userLevel]
  );

  const onChangeQty = (productId: number, qty: number) =>
    updateQty(productId, Math.max(1, qty));
  const onRemove = (productId: number) => removeItem(productId);

  const subtotal = rows.reduce((acc, r) => acc + r.unitPrice * r.qty, 0);
  const shipping = rows.length ? 15 : 0;
  const total = subtotal + shipping;
  const gstIncluded = total / 11;

  const [comment, setComment] = useState('');
  const buildPlaceOrderRequest = () => ({
    items: rows.map((r) => ({ productId: r.productId, quantity: r.qty })),
    comment: comment || undefined,
  });

  const columns: ColumnsType<Row> = [
    {
      title: '',
      dataIndex: 'key',
      width: 48,
      align: 'center',
      render: (_, row) => (
        <Popconfirm
          title="Remove item?"
          onConfirm={() => onRemove(row.productId)}
          okText="Remove"
        >
          <Button type="text" danger icon={<CloseOutlined />} />
        </Popconfirm>
      ),
    },
    {
      title: 'Product',
      dataIndex: 'name',
      className: 'min-w-[360px]',
      render: (_: string, row) => (
        <div className="flex items-center gap-4">
          <div className="h-20 w-20 overflow-hidden rounded bg-gray-100 shrink-0">
            {row.image ? (
              <Image
                src={row.image}
                alt={row.name}
                width={80}
                height={80}
                style={{ objectFit: 'cover' }}
                preview={false}
              />
            ) : (
              <div className="h-full w-full bg-gray-200" />
            )}
          </div>
          <div className="min-w-0">
            <div className="font-medium truncate">{row.name}</div>
            <div className="text-xs text-gray-400">SKU: {row.product.sku}</div>
            {/* <div className="text-xs text-gray-400">Type: {row.product.type}</div> */}
          </div>
        </div>
      ),
    },
    {
      title: 'Price',
      dataIndex: 'unitPrice',
      width: 120,
      align: 'right',
      render: (price: number) => <span>{formatMoney(price)}</span>,
    },
    {
      title: 'Quantity',
      dataIndex: 'qty',
      width: 140,
      align: 'center',
      render: (qty: number, row) => (
        <InputNumber
          min={1}
          value={qty}
          step={1}
          onChange={(val) =>
            onChangeQty(row.productId, Math.floor(Number(val ?? 1)))
          }
          className="w-24"
        />
      ),
    },
    {
      title: 'Subtotal',
      key: 'subtotal',
      width: 140,
      align: 'right',
      render: (_, row) => (
        <span className="font-medium">
          {formatMoney(row.unitPrice * row.qty)}
        </span>
      ),
    },
  ];

  const handleCheckout = async () => {
    if (!rows.length) return;
    const payload = buildPlaceOrderRequest();
    console.log('place order payload', payload);
    try {
      const response = await placeOrder(payload);
      if (!response.success) {
        message.error(response.message, 3);
      } else {
        message.success('Place Order Succssfully');
        useCartStore.getState().clear();
      }
    } catch (error: any) {
      //handle unexpected
      message.error('Error code : ' + error, 3);
    }
  };

  return (
    <div className="container px-0 py-0">
      {/* Header */}
      <div className="flex items-center justify-between">
        <Title level={2} className="!mb-0">
          My Cart
        </Title>
        <Button
          type="link"
          onClick={() => useCommonStore.getState().navigate('/me')}
        >
          My Account
        </Button>
      </div>

      <Divider className="my-4" />

      {/* Table */}
      <Card
        styles={{ body: { padding: 0 } }}
        className="rounded-2xl overflow-hidden"
      >
        {rows.length === 0 ? (
          <div className="p-8">
            <Empty description="Your cart is empty" />
          </div>
        ) : (
          <Table<Row>
            columns={columns}
            dataSource={rows}
            pagination={false}
            rowKey="key"
            className="[&_th]:font-medium"
          />
        )}
      </Card>

      <Card
        title="Order note"
        styles={{ body: { padding: 16 } }}
        className="rounded-2xl mt-4"
      >
        <Input
          placeholder="Add a comment or special instructions for your order"
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          maxLength={150}
          showCount
        />
      </Card>

      {/* Totals (bottom) */}
      <Card
        title="Cart totals"
        styles={{ body: { padding: 16 } }}
        className="rounded-2xl mt-6"
      >
        <div className="grid gap-3">
          <div className="flex justify-between">
            <Text>Subtotal</Text>
            <Text>{formatMoney(subtotal)}</Text>
          </div>
          <div className="flex justify-between">
            <div>
              <Text>Shipping</Text>
              <div className="text-gray-500 text-sm">
                Standard Shipping: {formatMoney(shipping)}
              </div>
            </div>
            <Text>{formatMoney(shipping)}</Text>
          </div>
          <Divider className="!my-2" />
          <div className="flex justify-between items-center">
            <Paragraph>
              <Text strong>Total</Text>
            </Paragraph>
            <Title level={4} className="!mb-0">
              {formatMoney(total)}{' '}
              <span className="text-gray-500 text-sm font-normal">
                (includes {formatMoney(gstIncluded)} GST)
              </span>
            </Title>
          </div>
          <div className="flex justify-end">
            <Button
              type="primary"
              size="large"
              className="mt-2"
              onClick={handleCheckout}
              disabled={!rows.length}
            >
              Place Order
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default CartPage;
