import CartButton from '@/components/CartButton';
import { useUserStore } from '@/store/user';
import React from 'react';

const HomePage: React.FC = () => {
  const user = useUserStore((s) => s.user);

  return (
    <div>
      Hello, {user?.username} Role = {user?.role}
      <div className="fixed top-4 right-4 z-50">
        <CartButton />
      </div>
    </div>
  );
};

export default HomePage;
