import React, { useEffect } from 'react';
import { Collapse } from 'antd';
import { useEnquiryStore } from '@/store/enquiry';
import { getMyEnquiries } from '@/api/api';
import { Enquiry } from '@/types/Enquiry';

const { Panel } = Collapse;

const statusColor: Record<Enquiry['status'], string> = {
  PENDING: 'text-orange-500',
  ANSWERED: 'text-green-600',
};

const EnquiryCardHeader: React.FC<Enquiry> = ({ question, status }) => {
  return (
    <div className="bg-gray-200 rounded-md">
      <div className="p-4">
        {/* Column Headers */}
        <div className="flex flex-wrap text-sm font-bold mb-2 gap-x-12">
          <div className="w-full sm:w-1/6">Status</div>
          <div className="w-full sm:flex-1">Question</div>
        </div>
        {/* Content */}
        <div className="flex flex-wrap text-sm bg-gray-200 rounded-md gap-x-12">
          <div
            className={`w-full sm:w-1/6 font-semibold ${statusColor[status]}`}
          >
            {status}
          </div>
          <div className="w-full sm:flex-1 text-sm leading-relaxed">
            {question}
          </div>
        </div>
      </div>
    </div>
  );
};

const MyEnquiry: React.FC = () => {
  const enquiryList = useEnquiryStore((s) => s.enquiryPageList);

  async function fetchMyEnquiryList() {
    const response = await getMyEnquiries();
    if (response.success) {
      const raw = (response.data as any)?.content ?? [];

      // Normalize status to match 'PENDING' | 'ANSWERED'
      const list = raw.map((e: any) => ({
        ...e,
        answer: e.answer ?? null,
        status:
          String(e.status).toUpperCase() === 'ANSWERED'
            ? 'ANSWERED'
            : 'PENDING',
      }));

      useEnquiryStore.setState({ enquiryPageList: list });
    }
  }

  useEffect(() => {
    fetchMyEnquiryList();
  }, []);

  if (enquiryList == null) {
    return <>Loading...</>;
  }
  if (enquiryList.length === 0) {
    return <>No enquiries</>;
  }

  return (
    <div className="p-6 space-y-4 bg-white">
      <Collapse
        ghost
        defaultActiveKey={enquiryList
          .filter((enquiry) => enquiry.answer)
          .map((enquiry) => enquiry.id)}
      >
        {enquiryList.map((enquiry) => (
          <Panel
            key={enquiry.id}
            header={<EnquiryCardHeader {...enquiry} />}
            showArrow
          >
            <div className="flex">
              <div className="w-1/6" />
              <div className="flex-1 p-4 space-y-4">
                <div>{enquiry.answer}</div>
                <div className="flex justify-end"></div>
              </div>
            </div>
          </Panel>
        ))}
      </Collapse>
    </div>
  );
};

export default MyEnquiry;
