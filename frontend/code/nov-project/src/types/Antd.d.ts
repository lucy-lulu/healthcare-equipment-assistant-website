import { MenuProps } from 'antd';

interface SelectOptionsItem {
  value: string;
  label: string;
}

type MenuItem = Required<MenuProps>['items'][number];
