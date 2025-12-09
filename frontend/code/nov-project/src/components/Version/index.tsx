import React from 'react';

interface Props {
  color?: string;
}

const Version: React.FC<Props> = (props) => {
  return (
    <div
      className="fixed bottom-4"
      style={{ color: props.color ? props.color : 'white' }}
    >
      v0.0.4
    </div>
  );
};

export default Version;
