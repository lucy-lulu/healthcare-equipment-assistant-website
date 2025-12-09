import { aiSearch } from '@/api/api';
import CartButton from '@/components/CartButton';
import ProductDisplayPanel from '@/components/Product/ProductDisplayPanel';
import { AiResponse } from '@/types/CommonHttp';
import { LoadingOutlined } from '@ant-design/icons';
import { Flex, Image, message } from 'antd';
import React, { useEffect, useRef, useState } from 'react';

const AiPage: React.FC = () => {
  const [query, setQuery] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!query.trim()) return;
    handleAiSearch();
    // TODO: 替换为实际搜索逻辑
  }

  const [resultList, setResultList] = useState<AiResponse[]>([]);

  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.scrollTo({
        top: containerRef.current.scrollHeight,
        behavior: 'smooth', // 平滑滚动
      });
    }
  }, [resultList]); // 当 messages 变化时触发

  const handleAiSearch = async () => {
    setIsLoading(true);
    const uuid = Date.now();
    const oldList = resultList;
    setResultList([
      ...oldList,
      {
        session_id: uuid.toString(),
        reply: '',
        products: [],
        questionText: query,
      } as AiResponse,
    ]);

    const response = await aiSearch(uuid.toString(), query, 10);
    if (response.session_id != '') {
      response.questionText = query;
      message.success('ai search success!');
      setIsLoading(false);
      setQuery('');
      setResultList([...oldList, response]);
    }
  };

  return (
    <>
      <div className="absolute top-4 right-4">
        <CartButton />
      </div>
      <Flex vertical justify="space-between" style={{ height: '95vh' }}>
        {resultList.length > 0 && (
          <div
            ref={containerRef}
            style={{
              marginTop: '70px',
              height: '80vh',
              maxHeight: '80vh',
              overflowY: 'auto',
              overflowX: 'hidden',
            }}
          >
            {resultList.map((searchResItem) => {
              return (
                <>
                  <div style={{ textAlign: 'right' }}>
                    <span
                      style={{
                        margin: '50px',
                        padding: '10px 20px',
                        borderRadius: '15px',
                        fontSize: '1.6em',
                        lineHeight: '2em',
                        backgroundColor: 'white',
                      }}
                    >
                      {searchResItem.questionText}
                    </span>
                  </div>
                  {searchResItem.reply != '' && (
                    <div
                      style={{
                        marginTop: '40px',
                        width: '90%',
                        backgroundColor: 'white',
                        padding: '0px 20px 20px 20px',
                        marginBottom: '80px',
                        borderRadius: '15px',
                      }}
                    >
                      <div
                        style={{
                          padding: '0px 20px 10px',
                          fontSize: '1.4em',
                          lineHeight: '1.8em',
                          backgroundColor: 'white',
                        }}
                      >
                        {searchResItem.reply}
                      </div>
                      <ProductDisplayPanel
                        productList={searchResItem.products}
                      />
                    </div>
                  )}
                  {searchResItem.reply == '' && (
                    <div
                      style={{
                        marginTop: '40px',
                        width: '80px',
                        backgroundColor: 'white',
                        padding: '20px 30px',
                        marginBottom: '40vh',
                        borderRadius: '15px',
                      }}
                    >
                      <LoadingOutlined style={{ fontSize: '20px' }} />
                    </div>
                  )}
                </>
              );
            })}
            {/* <ProductDisplayPanel productList={productList} /> */}
          </div>
        )}

        <Flex
          style={{
            marginTop: resultList.length == 0 ? '300px' : undefined,
            marginRight: '100px',
            // marginBottom: '20px',
          }}
          vertical
          align="center"
        >
          {resultList.length == 0 && (
            <Image
              src="logo.png"
              height={110}
              width={310}
              preview={false}
            ></Image>
          )}

          <form
            onSubmit={handleSubmit}
            className="w-full max-w-2xl flex items-center gap-2 pt-2"
          >
            <input
              type="text"
              placeholder="Enter anything here..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="flex-1 px-4 py-3 rounded-2xl border text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 shadow-sm"
            />
            <button
              type="submit"
              style={{
                borderRadius: '18px',
                backgroundColor: 'black',
                color: 'white',
                height: '45px',
              }}
            >
              {isLoading ? (
                <LoadingOutlined style={{ fontSize: '2em' }} />
              ) : (
                'Search'
              )}
            </button>
          </form>
        </Flex>
      </Flex>
    </>
  );
};

export default AiPage;
