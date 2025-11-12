import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios';
import { message } from 'antd';
import type { ApiResponse } from '../types';
import { mockApi } from './mock';

// 开发模式：使用真实后端
const USE_MOCK = false; // 禁用Mock，使用真实API

// 创建axios实例（仅在生产环境使用）
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    const tenantId = localStorage.getItem('tenantId') || 'default_tenant';
    const userId = localStorage.getItem('userId');
    
    config.headers['X-Tenant-Id'] = tenantId;
    if (userId) {
      config.headers['X-User-Id'] = userId;
    }

    return config;
  },
  (error) => {
    console.error('请求错误：', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, message: msg, data } = response.data;

    if (code === 200) {
      return data;
    }

    message.error(msg || '请求失败');
    return Promise.reject(new Error(msg || '请求失败'));
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;

      switch (status) {
        case 401:
          message.error('未授权，请重新登录');
          localStorage.removeItem('token');
          localStorage.removeItem('userInfo');
          window.location.href = '/login';
          break;
        case 403:
          message.error('没有权限访问该资源');
          break;
        case 404:
          message.error('请求的资源不存在');
          break;
        case 500:
          message.error(data?.message || '服务器错误');
          break;
        default:
          message.error(data?.message || '请求失败');
      }
    } else if (error.request) {
      message.error('网络错误，请检查网络连接');
    } else {
      message.error(error.message || '请求失败');
    }

    return Promise.reject(error);
  }
);

export default request;

// Mock请求包装函数
function mockRequest<T = any>(mockFn: () => Promise<ApiResponse>): Promise<T> {
  return mockFn().then(response => {
    if (response.code === 200) {
      return response.data as T;
    }
    message.error(response.message || '请求失败');
    return Promise.reject(new Error(response.message));
  });
}

// 封装常用请求方法
export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    // 🚨 强制使用Mock - 不发送真实请求
    if (USE_MOCK) {
      console.log('🔵 [MOCK] GET:', url, config?.params);
      
      // 用户信息
      if (url === '/auth/info' || url === '/auth/current-user') {
        return mockRequest(() => mockApi.getUserInfo());
      }
      
      // 产品列表
      if (url === '/product/list') {
        return mockRequest(() => mockApi.getProducts(config?.params || {}));
      }
      
      // 设备列表
      if (url === '/device/list') {
        return mockRequest(() => mockApi.getDevices(config?.params || {}));
      }
      
      // 设备详情
      if (url.startsWith('/device/detail/')) {
        const id = url.split('/').pop()!;
        return mockRequest(() => mockApi.getDeviceDetail(id));
      }
      
      // 设备数据
      if (url.match(/\/device\/\w+\/data/)) {
        const id = url.split('/')[2];
        return mockRequest(() => mockApi.getDeviceData(id, config?.params || {}));
      }
      
      // 未匹配的GET请求返回空数据
      console.warn('⚠️ [MOCK] 未匹配的GET请求:', url);
      return Promise.resolve({ list: [], total: 0 } as T);
    }
    
    return request.get(url, config);
  },

  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    // 🚨 强制使用Mock - 不发送真实请求
    if (USE_MOCK) {
      console.log('🔵 [MOCK] POST:', url, data);
      
      // 登录
      if (url === '/auth/login') {
        return mockRequest(() => mockApi.login(data.username, data.password));
      }
      
      // 登出
      if (url === '/auth/logout') {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        return Promise.resolve({ message: '登出成功' } as T);
      }
      
      // 刷新Token
      if (url === '/auth/refresh-token') {
        const newToken = 'mock_token_' + Date.now();
        localStorage.setItem('token', newToken);
        return Promise.resolve({ token: newToken } as T);
      }
      
      // 创建产品
      if (url === '/product/create') {
        return mockRequest(() => mockApi.createProduct(data));
      }
      
      // 创建设备
      if (url === '/device/create') {
        return mockRequest(() => mockApi.createDevice(data));
      }
      
      // 未匹配的POST请求返回成功
      console.warn('⚠️ [MOCK] 未匹配的POST请求:', url);
      return Promise.resolve({ message: '操作成功' } as T);
    }
    
    return request.post(url, data, config);
  },

  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    if (USE_MOCK) {
      console.log('🔵 [MOCK] PUT:', url, data);
      return Promise.resolve({ message: '更新成功' } as T);
    }
    return request.put(url, data, config);
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    if (USE_MOCK) {
      console.log('🔵 [MOCK] DELETE:', url);
      return Promise.resolve({ message: '删除成功' } as T);
    }
    return request.delete(url, config);
  },
};
