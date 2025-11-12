/**
 * 认证相关API
 */
import { http } from '../utils/request';
import type { LoginRequest, LoginResponse, UserInfo } from '../types';

/**
 * 用户登录
 */
export const login = (data: LoginRequest): Promise<LoginResponse> => {
  return http.post('/auth/login', data);
};

/**
 * 用户登出
 */
export const logout = (): Promise<void> => {
  return http.post('/auth/logout');
};

/**
 * 获取当前用户信息
 */
export const getCurrentUser = (): Promise<UserInfo> => {
  return http.get('/auth/current-user');
};

/**
 * 刷新Token
 */
export const refreshToken = (): Promise<{ token: string }> => {
  return http.post('/auth/refresh-token');
};
