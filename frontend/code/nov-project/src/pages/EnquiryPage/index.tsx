import React, { useEffect, useState } from 'react';
import { Collapse, Input, Button, Image, message } from 'antd';
import { useEnquiryStore } from '@/store/enquiry';
import { getAllEnquiries, replyEnquiry } from '@/api/api';
import { Enquiry } from '@/types/Enquiry';

const { Panel } = Collapse;
const { TextArea } = Input;

const statusColor: Record<Enquiry['status'], string> = {
  PENDING: 'text-orange-500',
  ANSWERED: 'text-green-600',
};

const EnquiryCardHeader: React.FC<Enquiry> = ({ question, answer, status }) => {
  return (
    <div className="bg-gray-200 rounded-md">
      <div className="p-4">
        {/* Column Headers */}
        <div className="flex flex-wrap text-sm font-bold mb-2 gap-x-12">
          <div className="w-full sm:w-1/6">Status</div>
          <div className="w-full sm:w-1/6">Image</div>
          <div className="w-full sm:flex-1">Question</div>
          <div className="w-full sm:flex-1">Answer</div>
        </div>
        {/* Content */}
        <div className="flex flex-wrap text-sm bg-gray-200 rounded-md gap-x-12">
          <div
            className={`w-full sm:w-1/6 font-semibold ${statusColor[status]}`}
          >
            {status}
          </div>
          <div className="w-full sm:w-1/6 flex">
            <div className="bg-white-700 text-white text-xs w-20 h-24 flex items-center justify-center rounded">
              <Image src="sample1.jpg" />
            </div>
          </div>
          <div className="w-full sm:flex-1 text-sm leading-relaxed">
            {question}
          </div>
          <div className="w-full sm:flex-1 text-sm leading-relaxed">
            {answer ?? <i>Not answered yet</i>}
          </div>
        </div>
      </div>
    </div>
  );
};

const EnquiryPage: React.FC = () => {
  const enquiryList = useEnquiryStore((s) => s.enquiryPageList);
  const [replyContent, setReplyContent] = useState<Record<number, string>>({});

  const fetchEnquiryList = async () => {
    const response = await getAllEnquiries();
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
  };

  const handleReplyEnquiry = async (id: number) => {
    const reply = replyContent[id];
    if (!reply) {
      message.warning('Reply cannot be empty!');
    } else {
      const response = await replyEnquiry(id, reply);
      if (response.success) {
        message.success('Enquiry replied!');
        setReplyContent('');
      }
      // refresh the enquiries
      await fetchEnquiryList();
    }
  };

  useEffect(() => {
    fetchEnquiryList();
  }, []);

  if (enquiryList == null) {
    return <>Loading...</>;
  }

  return (
    <div className="p-6 space-y-4 bg-white">
      <Collapse ghost>
        {enquiryList.map((enquiry) => (
          <Panel
            key={enquiry.id}
            header={<EnquiryCardHeader {...enquiry} />}
            showArrow
          >
            <div className="flex">
              <div className="w-1/6" />
              <div className="flex-1 p-4 space-y-4">
                <div>
                  <TextArea
                    rows={6}
                    defaultValue={enquiry.answer ?? ''} // show current answer
                    onChange={(e) =>
                      setReplyContent((d) => ({
                        ...d,
                        [enquiry.id]: e.target.value,
                      }))
                    }
                    className="text-sm"
                    placeholder="Type your reply here..."
                  />
                </div>
                <div className="flex justify-end">
                  <Button
                    type="primary"
                    onClick={() => handleReplyEnquiry(enquiry.id)}
                  >
                    Submit
                  </Button>
                </div>
              </div>
            </div>
          </Panel>
        ))}
      </Collapse>
    </div>
  );
};

export default EnquiryPage;
