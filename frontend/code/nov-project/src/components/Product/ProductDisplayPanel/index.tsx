import { sendEnquiry } from '@/api/api';
import PriceLines from '@/components/Product/PriceLine';
import { useCartStore } from '@/store/cart';
import { useUserStore } from '@/store/user';
import { Product } from '@/types/Product';
import {
  Button,
  Card,
  Col,
  Drawer,
  Image,
  Input,
  InputNumber,
  message,
  Modal,
  Row,
} from 'antd';
import React, { useState } from 'react';

interface Props {
  productList: Product[];
}

const ProductDisplayPanel: React.FC<Props> = ({ productList }) => {
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [orderQuantity, setOrderQuantity] = useState(1);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [enquiryVisible, setEnquiryVisible] = useState(false);
  const [enquiryContent, setEnquiryContent] = useState('');
  const user = useUserStore((s) => s.user);

  const showDrawer = (product: any) => {
    setSelectedProduct(product);
    setOrderQuantity(1);
    setDrawerVisible(true);
  };

  const handleAddToCart = () => {
    if (!selectedProduct) return;
    useCartStore.getState().addItem(selectedProduct, orderQuantity);
    message.success(`Add ${selectedProduct.name} x ${orderQuantity}  `);
    setDrawerVisible(false);
  };

  const handleSendEnquiry = async () => {
    const response = await sendEnquiry(selectedProduct?.name + enquiryContent);
    if (response.success) {
      message.success('Enquiry sent!');
      setEnquiryVisible(false);
      setEnquiryContent('');
    }
  };

  if (user == null) {
    return <>Loading...</>;
  }

  const renderSize = (value?: number | null) =>
    value != null && value > 0 ? `${value} cm` : '';

  return (
    <div>
      <Row gutter={[16, 16]}>
        {productList.map((product) => (
          <Col span={6} key={product.id}>
            <Card
              hoverable
              cover={
                <Image
                  preview={false}
                  alt={product.name}
                  style={{
                    marginLeft: '20px',
                    maxHeight: '200px', // 限制最大高度
                    width: 'auto', // 宽度自动，根据高度缩放
                    objectFit: 'contain', // 保持比例（不会裁剪）
                  }}
                  src={product.images?.split(',')[0]}
                />
              }
              onClick={() => showDrawer(product)}
            >
              <Card.Meta
                title={product.name}
                // description={`$${product.price1}`}
                description={
                  user.level === 4
                    ? product.price1
                    : user.level === 3
                    ? product.price2
                    : user.level === 2
                    ? product.price3
                    : user.level === 1
                    ? product.price4
                    : product.price1
                }
              />
            </Card>
          </Col>
        ))}
      </Row>

      <Drawer
        title="Product Detail"
        placement="right"
        onClose={() => setDrawerVisible(false)}
        open={drawerVisible}
        width={'50%'}
        footer={
          <div className="flex justify-around">
            <Button onClick={() => setEnquiryVisible(true)}>Enquiry</Button>
            <Button type="primary" onClick={handleAddToCart}>
              Add to Cart
            </Button>
          </div>
        }
      >
        {selectedProduct && (
          <div>
            <Image
              preview={false}
              width={200}
              src={selectedProduct.images?.split(',')[0]}
            />
            <h3 className="text-lg font-semibold mt-4">
              {selectedProduct.name}
            </h3>
            <h4 className="text-base text-gray-600 mb-2">
              Price: $
              <PriceLines product={selectedProduct} userLevel={user.level} />
            </h4>
            <p>SKU: {selectedProduct.sku} </p>
            <br />
            <strong> Description:</strong>
            <p>{selectedProduct.shortDescription}</p>
            <br />
            {/<\/?[a-z][\s\S]*>/i.test(selectedProduct.description ?? '') ? (
              <div
                dangerouslySetInnerHTML={{
                  __html: selectedProduct.description || '',
                }}
              />
            ) : (
              <p>{selectedProduct.description}</p>
            )}
            <br />
            <strong>Specification: </strong>
            {selectedProduct.weightKg != null &&
              selectedProduct.weightKg > 0 && (
                <p> Weight: {selectedProduct.weightKg}Kg</p>
              )}
            {(selectedProduct.widthCm ||
              selectedProduct.lengthCm ||
              selectedProduct.heightCm) && (
              <p>
                Size: {renderSize(selectedProduct.widthCm)}
                {renderSize(selectedProduct.lengthCm) &&
                  ` x ${renderSize(selectedProduct.lengthCm)}`}
                {renderSize(selectedProduct.heightCm) &&
                  ` x ${renderSize(selectedProduct.heightCm)}`}
              </p>
            )}
            <Row justify={'end'} align={'middle'}>
              <p className="text-sm text-gray-500">Quantity:</p>
              <InputNumber
                min={1}
                max={10}
                value={orderQuantity}
                onChange={(value) => setOrderQuantity(value || 1)}
                style={{ marginBlock: 10, marginInline: 10 }}
              />
            </Row>
          </div>
        )}
      </Drawer>

      <Modal
        title="Enquiry"
        open={enquiryVisible}
        onOk={handleSendEnquiry}
        onCancel={() => setEnquiryVisible(false)}
        okText="Send"
      >
        <Input.TextArea
          rows={4}
          placeholder="Type your enquiry..."
          value={enquiryContent}
          onChange={(e) => setEnquiryContent(e.target.value)}
        />
      </Modal>
    </div>
  );
};

export default ProductDisplayPanel;
