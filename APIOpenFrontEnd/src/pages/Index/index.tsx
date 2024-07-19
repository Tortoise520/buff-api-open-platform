import {PageContainer} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {List, message} from 'antd';
import {listInterfaceInfoByPageUsingPost} from "@/services/APIOpenBackEnd/interfaceInfoController";

const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  const [total, setTotal] = useState<number>(0);

  const loadData = async (current: number = 1, pageSize: number = 10) => {
    setLoading(true);
    try {
      const res = await listInterfaceInfoByPageUsingPost({
        current, pageSize
      });
      setList(res.data?.records ?? []);
      setTotal(res.data?.total ?? 0);
    } catch (e: any) {
      message.error("请求失败", e.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <PageContainer title="在线接口开放平台">
      <>

        <List
          pagination={{
            position: 'bottom',
            align: 'start',
            // total:total,
            pageSize: 5,
            onChange: (current) => {
              loadData(current);
            }
          }}
          dataSource={list}
          loading={loading}
          renderItem={(item, index) => {
            const link = `/interfaceInfo/${item.id}`;
            return (
              <List.Item
              actions={[<a key={index} href={link}>查看</a>]}
            >
              <List.Item.Meta
                title={<a href={link}>{item.name}</a>}
                description={item.description}
              />
            </List.Item>
            )
          }
        }
        />
      </>

    </PageContainer>
  );
}

export default Index;
