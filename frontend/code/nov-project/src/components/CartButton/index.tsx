import useCommonStore from '@/store/common';
import { Button } from 'antd';
import React from 'react';
import { ShoppingCartOutlined } from '@ant-design/icons';
import { useUserStore } from '@/store/user';

const CartButton: React.FC = () => {
  if (useUserStore.getState().user?.level === 0) return <div></div>;
  return (
    <Button
      onClick={() => useCommonStore.getState().navigate('/cart')}
      shape="round"
      type="primary"
    >
      <ShoppingCartOutlined /> My Cart
    </Button>
  );
};

export default CartButton;
