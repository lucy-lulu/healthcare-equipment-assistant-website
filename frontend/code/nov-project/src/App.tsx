import useCommonStore from '@/store/common';
import { useLocation, useNavigate, useRoutes } from 'react-router-dom';
import { useEffect } from 'react';
import { resetAllStoreStates } from '@/store/bus';
import { Layout, message } from 'antd';
import MySider from '@/components/MySider';
import { Content } from 'antd/es/layout/layout';
import finalRoutes from '@/router';
import { useUserStore } from '@/store/user';
import { listAllCategories } from '@/api/api';
import { Category } from '@/types/Product';
import { useProductStore } from '@/store/product';

const App: React.FC = () => {
  //加載路由
  const routes = useRoutes(finalRoutes);
  //用於處理刷新等情況造成的頁面混亂
  const navigate = useNavigate();
  const currentSideWidth = useCommonStore((s) => s.currentSiderWidth);
  const { pathname } = useLocation();
  const userInfo = useUserStore((s) => s.user);

  const listCategoryOperation = async () => {
    try {
      const response = await listAllCategories();
      console.log('response = ', response);
      if (!response.success) {
        message.error('list category fail , err =' + response.message, 3);
      } else {
        //generate the tree
        const categoryMap = new Map<number, Category>();
        response.data.forEach((item) => {
          item.children = [];
          categoryMap.set(item.id, item);
        });

        response.data.forEach((item) => {
          if (item.parentId != null) {
            const parent = categoryMap.get(item.parentId);
            parent?.children.push(item);
          }
        });

        console.log(categoryMap);
        console.log('111');

        categoryMap.forEach((v) => {
          if (v.parentId == null) {
            console.log(v);
          }
        });
        useProductStore.setState({ categoryMap: categoryMap });
      }
    } catch (error: any) {
      //handle unexpected
      message.error('Error code : ' + error, 3);
    }
  };

  useEffect(() => {
    // 在应用加载时调用 resetAll
    resetAllStoreStates();
  }, []);

  //when login, list all categories
  useEffect(() => {
    if (userInfo != null) {
      listCategoryOperation();
    }
  }, [userInfo]);

  useEffect(() => {
    // console.log('path name =', pathname);
    if (pathname.startsWith('/products')) {
      console.log('now set selected key to ', [pathname.split('/')[2]]);
      useCommonStore.setState({ selectedKeys: [pathname.split('/')[3]] });
    } else {
      useCommonStore.setState({ selectedKeys: [pathname] });
    }
  }, [pathname]);

  const ifRenderSider = !pathname.includes('login');

  console.log('加載navigate');
  //將navigate作為全局變量存儲
  useCommonStore.setState({
    navigate: navigate,
  });
  return (
    <Layout style={{ minHeight: '100vh' }}>
      {ifRenderSider && <MySider />}
      <Layout
        style={{ marginInlineStart: ifRenderSider ? currentSideWidth : '0px' }}
      >
        {/* <MyHeader selectedKeys={selectedKeys} menuItems={menuItems} /> */}

        <Content style={{  minHeight: '100vh' }}>
          {routes}
        </Content>
      </Layout>
    </Layout>
  );
};

export default App;
