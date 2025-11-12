/**
 * 主布局组件
 */
import { useState } from 'react';
import { Layout, Menu, Avatar, Dropdown, Button } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  AppstoreOutlined,
  ThunderboltOutlined,
  BellOutlined,
  SettingOutlined,
  LogoutOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useUserStore } from '../../store/userStore';
import type { MenuProps } from 'antd';
import './MainLayout.css';

const { Header, Sider, Content } = Layout;

const MainLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { userInfo, logout } = useUserStore();
  const [collapsed, setCollapsed] = useState(false);

  // 侧边栏菜单项
  const menuItems: MenuProps['items'] = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '仪表盘',
    },
    {
      key: '/device',
      icon: <AppstoreOutlined />,
      label: '设备管理',
      children: [
        {
          key: '/device/product',
          label: '产品管理',
        },
        {
          key: '/device/device',
          label: '设备列表',
        },
      ],
    },
    {
      key: '/rule',
      icon: <ThunderboltOutlined />,
      label: '规则引擎',
    },
    {
      key: '/alarm',
      icon: <BellOutlined />,
      label: '告警中心',
    },
    {
      key: '/system',
      icon: <SettingOutlined />,
      label: '系统管理',
    },
  ];

  // 用户下拉菜单
  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人信息',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      danger: true,
    },
  ];

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  const handleUserMenuClick = async ({ key }: { key: string }) => {
    if (key === 'logout') {
      await logout();
      navigate('/login');
    } else if (key === 'profile') {
      // TODO: 跳转到个人信息页面
    }
  };

  return (
    <Layout className="main-layout">
      <Sider trigger={null} collapsible collapsed={collapsed} theme="light">
        <div className="logo">
          <h2>{collapsed ? 'IoT' : 'IoT Platform'}</h2>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          defaultOpenKeys={['/device']}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header className="layout-header">
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            className="trigger-btn"
          />
          <div className="header-right">
            <Dropdown
              menu={{ items: userMenuItems, onClick: handleUserMenuClick }}
              placement="bottomRight"
            >
              <div className="user-info">
                <Avatar icon={<UserOutlined />} src={userInfo?.avatar} />
                <span className="username">{userInfo?.nickname || userInfo?.username}</span>
              </div>
            </Dropdown>
          </div>
        </Header>
        <Content className="layout-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
