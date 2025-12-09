import React, { useEffect } from 'react';
import { searchProducts } from '@/api/api';
import { useProductStore } from '@/store/product';
import { useParams } from 'react-router-dom';
import { useUserStore } from '@/store/user';
import Search from 'antd/es/input/Search';
import ProductDisplayPanel from '@/components/Product/ProductDisplayPanel';
import CartButton from '@/components/CartButton';
import { Flex } from 'antd';

const ProductPage: React.FC = () => {
  const user = useUserStore((s) => s.user);

  const { categoryId } = useParams();

  const productList = useProductStore((s) => s.productPageList);

  async function fetchProductList(searchText?: string, categoryId?: number) {
    const response = await searchProducts(searchText, categoryId);
    if (response.success) {
      useProductStore.setState({ productPageList: response.data });
    }
  }
  useEffect(() => {
    if (categoryId != undefined) {
      fetchProductList(undefined, Number(categoryId));
    }
  }, [categoryId]);

  if (productList == null) {
    return <>Loading...</>;
  }
  if (categoryId == undefined) {
    return <>url invalid...</>;
  }

  if (user == null) {
    return <>Loading...</>;
  }

  return (
    <div style={{ padding: 10 }}>
      <Flex
        align="center"
        style={{ margin: '0px 10px 20px 10px' }}
        justify="space-between"
      >
        <div>
          {
            useProductStore.getState().categoryMap?.get(Number(categoryId))
              ?.name
          }
        </div>
        <Search
          placeholder="this search ignore category"
          style={{ width: 400, marginRight: '100px' }}
          onSearch={(val) => {
            if (val != '') {
              fetchProductList(val, undefined);
            } else {
              fetchProductList(undefined, Number(categoryId));
            }
          }}
          allowClear
        />
        <CartButton />
      </Flex>
      {/* <div className="absolute top-4 right-4"> */}
      {/* </div> */}

      <ProductDisplayPanel productList={productList} />
    </div>
  );
};

export default ProductPage;
