import { Category } from '@/types/Product';
import {
  UserOutlined,
  AlertOutlined,
  MoneyCollectOutlined,
  BarsOutlined,
  ShoppingCartOutlined,
  ShoppingOutlined,
  CommentOutlined,
  DiscordOutlined,
} from '@ant-design/icons';
import { MenuProps } from 'antd';

type MenuItem = Required<MenuProps>['items'][number];

function getItem(
  label: React.ReactNode,
  key: React.Key,
  icon?: React.ReactNode,
  children?: MenuItem[],
  type?: 'group',
  roles?: string[]
): MenuItem {
  return {
    key,
    icon,
    children,
    label,
    type,
    roles,
  } as MenuItem;
}

export const defaultOpenKeys = ['sub1'];

export const menuItems = [
  getItem(
    'some module',
    '/usermanage',
    <UserOutlined />,
    undefined,
    undefined,
    ['admin']
  ),
  getItem('NovisAi', '/ai', <DiscordOutlined />),
  getItem('Products', '/products', <ShoppingOutlined />),
  getItem('Enquiry', '/enquiry', <BarsOutlined />, undefined, undefined, [
    'admin',
  ]),
  getItem('Orders', '/orders', <ShoppingCartOutlined />, undefined, undefined, [
    'admin',
  ]),
  getItem('some module', '/withdrawmanage', <MoneyCollectOutlined />),
  getItem('some module', '/reportmanage', <AlertOutlined />),
];

const generateMenuItemsUsingCategory = (category: Category): MenuItem => {
  if (category.children.length == 0) {
    return getItem(category.name, category.id);
  } else {
    return getItem(
      category.name,
      category.id,
      undefined,
      category.children.map((subCategory) => {
        return generateMenuItemsUsingCategory(subCategory);
      })
    );
  }
};

const genItemsWithCategory = (categoryMap: Map<number, Category>) => {
  const productCategories: MenuItem[] = [];
  categoryMap.forEach((v) => {
    if (v.parentId == null) {
      productCategories.push(generateMenuItemsUsingCategory(v));
    }
  });
  const rootProductNode = getItem(
    'Products',
    '/products',
    <ShoppingOutlined />,
    productCategories
  );
  return [
    getItem('NovisAi', '/ai', <DiscordOutlined />),
    getItem(
      'some module',
      '/usermanage',
      <UserOutlined />,
      undefined,
      undefined,
      ['admin']
    ),
    rootProductNode,
    getItem('Enquiry', '/enquiry', <BarsOutlined />, undefined, undefined, [
      'admin',
    ]),
    getItem('Orders', '/orders', <ShoppingCartOutlined />),
    getItem('My Account', '/me', <UserOutlined />, undefined, undefined, [
      'partner',
    ]),
    getItem(
      'My Enquiry',
      '/myenquiry',
      <CommentOutlined />,
      undefined,
      undefined,
      ['partner']
    ),
  ];
};

export function getMenuItemsForRole(
  userRole: string,
  categoryMap: Map<number, Category> | null
): MenuItem[] {
  if (categoryMap != null) {
    const newMenuItems = genItemsWithCategory(categoryMap);
    return newMenuItems.filter((item) => {
      const roles = (item as any).roles;
      return !roles || roles.includes(userRole);
    });
  }

  return menuItems.filter((item) => {
    const roles = (item as any).roles;
    return !roles || roles.includes(userRole);
  });
}
