/**
 * 路由配置
 */
import { createBrowserRouter, Navigate } from 'react-router-dom';
import Login from '../views/login';
import Layout from '../components/layout/MainLayout';
import Dashboard from '../views/dashboard';
import ProductList from '../views/device/ProductList';
import DeviceList from '../views/device/DeviceList';

// 路由守卫：检查是否登录
const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const token = localStorage.getItem('token');
  
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  
  return <>{children}</>;
};

const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: <Dashboard />,
      },
      {
        path: 'device',
        children: [
          {
            path: 'product',
            element: <ProductList />,
          },
          {
            path: 'device',
            element: <DeviceList />,
          },
        ],
      },
    ],
  },
  {
    path: '*',
    element: <Navigate to="/dashboard" replace />,
  },
]);

export default router;
