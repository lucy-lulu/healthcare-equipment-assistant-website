import Version from '@/components/Version';
import { defaultOpenKeys, getMenuItemsForRole } from '@/router/sider';
import { resetAllStoreStates } from '@/store/bus';
import useCommonStore from '@/store/common';
import { useProductStore } from '@/store/product';
import { useUserStore } from '@/store/user';
import { Button, Flex, Menu, message } from 'antd';
import Sider from 'antd/es/layout/Sider';
import React from 'react';

const siderStyle: React.CSSProperties = {
  overflow: 'auto',
  height: '100vh',
  position: 'fixed',
  insetInlineStart: 0,
  top: 0,
  bottom: 0,
  scrollbarWidth: 'thin',
  scrollbarGutter: 'stable',
  // maxWidth: '400px',
};

const MySider: React.FC = () => {
  const selectedKeys = useCommonStore((s) => s.selectedKeys);
  const userInfo = useUserStore((s) => s.user);
  const categoryMap = useProductStore((s) => s.categoryMap);

  if (userInfo == null) {
    useCommonStore.getState().navigate('/login', { replace: true });
    return;
  }

  // const logoutOperation = async () => {
  //   try {
  //     const response = await logout();
  //     if (response.code !== 0) {
  //       //handle backend defined error
  //       message.error('Error code : ' + response.message, 3);
  //     } else {
  //       useCommonStore.setState({ userInfo: null });
  //       useCommonStore.getState().navigate('/login', { replace: true });
  //       message.success('logout success', 3);
  //     }
  //   } catch (error: any) {
  //     //handle unexpected
  //     message.error('Error code : ' + error, 3);
  //   }
  // };

  return (
    <Sider
      className="mysider"
      // collapsible
      style={siderStyle}
      // onCollapse={(value) => {
      //   const newWidth = value ? 80 : 400;
      //   useCommonStore.setState({ currentSiderWidth: newWidth });
      // }}
    >
      <Flex justify="space-between" vertical style={{ height: '100vh' }}>
        <Menu
          defaultOpenKeys={defaultOpenKeys}
          theme="dark"
          defaultSelectedKeys={[]}
          selectedKeys={selectedKeys}
          mode="inline"
          items={getMenuItemsForRole(userInfo.role, categoryMap)}
          onSelect={(menuItem) => {
            console.log('now click !', menuItem);
            if (menuItem.keyPath.includes('/products')) {
              useCommonStore.getState().navigate(`/products/${menuItem.key}`);
            } else {
              useCommonStore.getState().navigate(menuItem.key);
            }
          }}
        />
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            flexDirection: 'column',
            gap: '5px',
            marginBottom: '50px',
          }}
        >
          <Version />
          <div style={{ color: 'white' }}>Hi! {userInfo.username}</div>
          <Button
            onClick={() => {
              resetAllStoreStates();
              useCommonStore.getState().navigate('/login');
              message.success('logout success', 3);
            }}
          >
            Logout
          </Button>
        </div>
      </Flex>
    </Sider>
  );
};

export default MySider;
