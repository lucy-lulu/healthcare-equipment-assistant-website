// withAuth.tsx
import React from 'react';
import { Navigate } from 'react-router-dom';

import { message } from 'antd';
import { useUserStore } from '@/store/user';

// 鉴权逻辑组件
const AuthWrapper = ({ children }: { children: React.ReactNode }) => {
  const isAuthenticated = useUserStore.getState().user != null;
  if (!isAuthenticated) {
    message.info('need to login first', 2);
  }
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
};

export default AuthWrapper;
