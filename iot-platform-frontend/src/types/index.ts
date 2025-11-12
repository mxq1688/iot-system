/**
 * 通用类型定义
 */

// API响应结构
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

// 分页参数
export interface PageParams {
  pageNum: number;
  pageSize: number;
}

// 分页响应
export interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// 用户信息
export interface UserInfo {
  id: string;
  username: string;
  nickname: string;
  email?: string;
  phone?: string;
  avatar?: string;
  tenantId: string;
  roles: string[];
  permissions: string[];
}

// 登录请求
export interface LoginRequest {
  username: string;
  password: string;
}

// 登录响应
export interface LoginResponse {
  token: string;
  userInfo: UserInfo;
}

// 产品信息
export interface Product {
  id: string;
  tenantId: string;
  name: string;
  code: string;
  type: number;
  protocol: number;
  deviceType: number;
  features?: string;
  description?: string;
  status: number;
  deviceCount?: number;
  createdAt: string;
  updatedAt: string;
  createdBy?: string;
}

// 设备信息
export interface Device {
  id: string;
  tenantId: string;
  productId: string;
  productName?: string;
  name: string;
  code: string;
  secret?: string;
  status: number;
  activated: number;
  location?: string;
  description?: string;
  lastOnlineTime?: string;
  lastOfflineTime?: string;
  activatedAt?: string;
  createdAt: string;
  updatedAt: string;
  createdBy?: string;
}

// 设备分组
export interface DeviceGroup {
  id: string;
  tenantId: string;
  name: string;
  description?: string;
  deviceCount?: number;
  createdAt: string;
  updatedAt: string;
}

// 设备数据
export interface DeviceData {
  time: string;
  field: string;
  value: any;
}

// 设备最新数据
export interface DeviceLatestData {
  [key: string]: any;
}

// 设备数据统计
export interface DeviceDataStatistics {
  data: Array<{
    time: string;
    value: number;
  }>;
  count: number;
}

// 菜单项
export interface MenuItem {
  key: string;
  label: string;
  icon?: React.ReactNode;
  children?: MenuItem[];
  path?: string;
}

// 路由配置
export interface RouteConfig {
  path: string;
  element: React.ReactNode;
  children?: RouteConfig[];
}
