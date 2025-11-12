// Mock数据服务 - 模拟后端API响应
import type { ApiResponse } from '../types';

// 模拟延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// Mock用户数据
const mockUsers = [
  {
    id: 'admin',
    username: 'admin',
    realName: '系统管理员',
    email: 'admin@iot.com',
    phone: '13800138000',
    tenantId: 'default_tenant',
    roles: ['ADMIN']
  }
];

// Mock设备产品数据
const mockProducts = [
  {
    id: 'prod_001',
    name: '智能灯泡',
    code: 'SMART_BULB_001',
    type: 1,
    protocol: 1,
    deviceCount: 5,
    onlineCount: 3,
    status: 1,
    createdAt: '2025-01-01 10:00:00'
  },
  {
    id: 'prod_002',
    name: '智能开关',
    code: 'SMART_SWITCH_001',
    type: 2,
    protocol: 1,
    deviceCount: 8,
    onlineCount: 6,
    status: 1,
    createdAt: '2025-01-02 11:00:00'
  },
  {
    id: 'prod_003',
    name: '温湿度传感器',
    code: 'TEMP_SENSOR_001',
    type: 3,
    protocol: 1,
    deviceCount: 12,
    onlineCount: 10,
    status: 1,
    createdAt: '2025-01-03 09:00:00'
  }
];

// Mock设备数据
const mockDevices = [
  {
    id: 'dev_001',
    productId: 'prod_001',
    name: '客厅灯泡',
    code: 'DEVICE_BULB_001',
    status: 1,
    onlineStatus: 1,
    lastOnlineTime: '2025-11-11 00:30:00'
  },
  {
    id: 'dev_002',
    productId: 'prod_002',
    name: '卧室开关',
    code: 'DEVICE_SWITCH_001',
    status: 1,
    onlineStatus: 0,
    lastOnlineTime: '2025-11-10 23:50:00'
  },
  {
    id: 'dev_003',
    productId: 'prod_003',
    name: '客厅温湿度',
    code: 'DEVICE_SENSOR_001',
    status: 1,
    onlineStatus: 1,
    lastOnlineTime: '2025-11-11 00:35:00'
  }
];

export const mockApi = {
  // 登录
  async login(username: string, password: string): Promise<ApiResponse> {
    await delay(500);
    
    if (username === 'admin' && password === 'admin123') {
      const user = mockUsers[0];
      const token = 'mock_token_' + Date.now();
      
      // 保存到localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('userInfo', JSON.stringify(user));
      localStorage.setItem('tenantId', user.tenantId);
      localStorage.setItem('userId', user.id);
      
      return {
        code: 200,
        message: '登录成功',
        data: {
          token,
          userInfo: user
        }
      };
    }
    
    return {
      code: 401,
      message: '用户名或密码错误',
      data: null
    };
  },

  // 获取用户信息
  async getUserInfo(): Promise<ApiResponse> {
    await delay(300);
    const userInfo = localStorage.getItem('userInfo');
    
    if (userInfo) {
      return {
        code: 200,
        message: '成功',
        data: JSON.parse(userInfo)
      };
    }
    
    return {
      code: 401,
      message: '未登录',
      data: null
    };
  },

  // 获取产品列表
  async getProducts(params: any): Promise<ApiResponse> {
    await delay(400);
    
    let filtered = [...mockProducts];
    
    // 搜索过滤
    if (params.keyword) {
      filtered = filtered.filter(p => 
        p.name.includes(params.keyword) || p.code.includes(params.keyword)
      );
    }
    
    return {
      code: 200,
      message: '成功',
      data: {
        list: filtered,
        total: filtered.length
      }
    };
  },

  // 获取设备列表
  async getDevices(params: any): Promise<ApiResponse> {
    await delay(400);
    
    let filtered = [...mockDevices];
    
    // 产品过滤
    if (params.productId) {
      filtered = filtered.filter(d => d.productId === params.productId);
    }
    
    // 搜索过滤
    if (params.keyword) {
      filtered = filtered.filter(d => 
        d.name.includes(params.keyword) || d.code.includes(params.keyword)
      );
    }
    
    return {
      code: 200,
      message: '成功',
      data: {
        list: filtered,
        total: filtered.length
      }
    };
  },

  // 创建产品
  async createProduct(data: any): Promise<ApiResponse> {
    await delay(500);
    
    const newProduct = {
      id: 'prod_' + Date.now(),
      ...data,
      deviceCount: 0,
      onlineCount: 0,
      status: 1,
      createdAt: new Date().toISOString()
    };
    
    mockProducts.push(newProduct);
    
    return {
      code: 200,
      message: '创建成功',
      data: newProduct
    };
  },

  // 创建设备
  async createDevice(data: any): Promise<ApiResponse> {
    await delay(500);
    
    const newDevice = {
      id: 'dev_' + Date.now(),
      ...data,
      status: 1,
      onlineStatus: 0,
      lastOnlineTime: new Date().toISOString()
    };
    
    mockDevices.push(newDevice);
    
    return {
      code: 200,
      message: '创建成功',
      data: newDevice
    };
  },

  // 获取设备详情
  async getDeviceDetail(id: string): Promise<ApiResponse> {
    await delay(300);
    
    const device = mockDevices.find(d => d.id === id);
    
    if (device) {
      return {
        code: 200,
        message: '成功',
        data: device
      };
    }
    
    return {
      code: 404,
      message: '设备不存在',
      data: null
    };
  },

  // 获取设备数据
  async getDeviceData(deviceId: string, params: any): Promise<ApiResponse> {
    await delay(400);
    
    // 生成模拟数据
    const data = [];
    const now = Date.now();
    
    for (let i = 0; i < 20; i++) {
      data.push({
        timestamp: new Date(now - i * 60000).toISOString(),
        temperature: (20 + Math.random() * 10).toFixed(1),
        humidity: (50 + Math.random() * 30).toFixed(1),
        value: Math.random() > 0.5 ? 1 : 0
      });
    }
    
    return {
      code: 200,
      message: '成功',
      data: {
        list: data.reverse(),
        total: data.length
      }
    };
  }
};
