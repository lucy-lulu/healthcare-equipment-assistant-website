import { login } from '@/api/api';
import BubblyBg from '@/components/Back';
import Version from '@/components/Version';
import useCommonStore from '@/store/common';
import { useUserStore } from '@/store/user';
import { LoginRequestParam } from '@/types/User';
import { Button, Form, FormProps, Input, message, Image } from 'antd';

export default function LoginPage() {
  const onFinishFailed: FormProps<LoginRequestParam>['onFinishFailed'] = (
    errorInfo
  ) => {
    console.log('Failed:', errorInfo);
  };

  const onFinish: FormProps<LoginRequestParam>['onFinish'] = (values) => {
    const loginOperation = async () => {
      try {
        const response = await login(values);
        console.log('response = ', response);
        if (!response.success) {
          message.error('login fail , err =' + response.message, 3);
        } else {
          message.success('login success', 3);
          useUserStore.setState({ user: response.data });
          useCommonStore.getState().navigate('/known', { replace: true });
        }
      } catch (error: any) {
        //handle unexpected
        message.error('Error code : ' + error, 3);
      }
      // if (values.username == 'partner_jane' && values.password == 'password') {
      //   message.success('login success', 3);
      //   useUserStore.setState({
      //     user: {
      //       id: 'sdhakoshikv-sdhkj4ehk2',
      //       username: 'partner_jane',
      //       email: 'testemail@gmail.com',
      //       role: 'partner',
      //       token: 'sdskdh-cccasjdbwsadl',
      //       type: 'Bearer',
      //     } as UserDetail,
      //   });
      //   useCommonStore.getState().navigate('/home', { replace: true });
      // } else if (
      //   values.username == 'admin_jane' &&
      //   values.password == 'password'
      // ) {
      //   message.success('login success', 3);
      //   useUserStore.setState({
      //     user: {
      //       id: 'sdhakoshikv-sdhkj4ehk2',
      //       username: 'admin_jane',
      //       email: 'testemail@gmail.com',
      //       role: 'admin',
      //       token: 'sdskdh-cccasjdbwsadl',
      //       type: 'Bearer',
      //     } as UserDetail,
      //   });
      //   useCommonStore.getState().navigate('/home', { replace: true });
      // } else {
      //   message.error('invalid username or password', 3);
      // }
    };
    loginOperation();
  };

  return (
    <div className="relative w-full h-screen overflow-hidden ">
      <BubblyBg
        className="z-0"
        options={{
          compose: 'lighter',
          background: (ctx) => {
            const g = ctx.createLinearGradient(
              0,
              0,
              ctx.canvas.width,
              ctx.canvas.height
            );
            g.addColorStop(0, '#ffffff');
            g.addColorStop(0.8, '#00509F');
            g.addColorStop(1, '#002B65');

            return g;
          },
          bubbles: {
            count: 20,
            radius: () => 4 + (Math.random() * window.innerWidth) / 15,
            fill: () => `hsla(0, 0%, 100%, ${Math.random() * 0.1})`,
            angle: () => Math.random() * Math.PI * 2,
            velocity: () => 0.1 + Math.random() * 0.5,
            shadow: () => null,
            stroke: () => ({
              color: 'rgba(255,255,255,0.1)',
              width: Math.random() * 5,
            }),
          },
          animate: true,
        }}
      />
      <div className="grid place-items-center h-full relative z-10">
        <Image src="/logo.png" preview={false} />
        <div>
          <Form
            name="basic"
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 16 }}
            className="max-w-[600px]"
            initialValues={{ remember: true }}
            onFinish={onFinish}
            onFinishFailed={onFinishFailed}
            autoComplete="off"
          >
            <Form.Item<LoginRequestParam>
              label="Username"
              name="username"
              rules={[{ required: true, message: 'enter username' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item<LoginRequestParam>
              label="Password"
              name="password"
              rules={[{ required: true, message: 'enter password' }]}
            >
              <Input.Password />
            </Form.Item>

            <Form.Item wrapperCol={{ offset: 8, span: 16 }}>
              <Button type="primary" htmlType="submit">
                Login
              </Button>
            </Form.Item>
          </Form>
        </div>
        <div></div>
        <Version color="black" />
      </div>
    </div>
  );
}
