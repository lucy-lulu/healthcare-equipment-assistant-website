import { Navigate } from 'react-router-dom';
import AuthWrapper from '@/router/withAuth';
import LoginPage from '@/pages/LoginPage';
import HomePage from '@/pages/HomePage';
import EnquiryPage from '@/pages/EnquiryPage';
import OrderPage from '@/pages/OrderPage';
import ProductPage from '@/pages/ProductPage';
import CartPage from '@/pages/CartPage';
import MyAccount from '@/pages/MyAccountPage';
import MyEnquiry from '@/pages/MyEnquiry';
import AiPage from '@/pages/AiPage';

// 定义路由结构体 , 设计字段为noauth, 减少配置需要
interface RouteConfig {
  path: string;
  element: React.ReactNode;
  noauth?: boolean; // 可选，表示是否需要鉴权
}

const routesMap: RouteConfig[] = [
  {
    path: '/login',
    element: <LoginPage />,
    noauth: true,
  },
  {
    path: '/home',
    element: <HomePage />,
  },
  {
    path: '/enquiry',
    element: <EnquiryPage />,
  },
  {
    path: '/myenquiry',
    element: <MyEnquiry />,
  },
  {
    path: '/orders',
    element: <OrderPage />,
  },
  {
    path: '/products/:categoryId',
    element: <ProductPage />,
  },
  {
    path: '/cart',
    element: <CartPage />,
  },
  {
    path: '/ai',
    element: <AiPage />,
  },
  {
    path: '/me',
    element: <MyAccount />,
  },

  {
    path: '/',
    element: <Navigate to="/ai" />,
  },
  {
    path: '*',
    element: <Navigate to="/" />,
  },
];

// 动态处理鉴权逻辑
const processRoutes = (routes: RouteConfig[]) =>
  routes.map((route) => {
    //确认是否需要校验
    if (!route.noauth) {
      return {
        ...route,
        element: <AuthWrapper>{route.element}</AuthWrapper>,
      };
    }
    return route;
  });

const finalRoutes = processRoutes(routesMap);

export default finalRoutes;
