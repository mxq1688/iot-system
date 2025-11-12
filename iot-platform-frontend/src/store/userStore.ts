/**
 * 用户状态管理
 */
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { UserInfo } from '../types';
import * as authApi from '../api/auth';

interface UserState {
  token: string | null;
  userInfo: UserInfo | null;
  isLogin: boolean;
  
  // Actions
  setToken: (token: string) => void;
  setUserInfo: (userInfo: UserInfo) => void;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  clearUserInfo: () => void;
}

export const useUserStore = create<UserState>()(
  persist(
    (set) => ({
      token: null,
      userInfo: null,
      isLogin: false,

      setToken: (token: string) => {
        localStorage.setItem('token', token);
        set({ token, isLogin: true });
      },

      setUserInfo: (userInfo: UserInfo) => {
        localStorage.setItem('userInfo', JSON.stringify(userInfo));
        localStorage.setItem('tenantId', userInfo.tenantId);
        localStorage.setItem('userId', userInfo.id);
        set({ userInfo });
      },

      login: async (username: string, password: string) => {
        try {
          const response = await authApi.login({ username, password });
          const { token, userInfo } = response;
          
          // 保存token
          localStorage.setItem('token', token);
          localStorage.setItem('userInfo', JSON.stringify(userInfo));
          localStorage.setItem('tenantId', userInfo.tenantId);
          localStorage.setItem('userId', userInfo.id);
          
          set({ 
            token, 
            userInfo, 
            isLogin: true 
          });
        } catch (error) {
          console.error('登录失败：', error);
          throw error;
        }
      },

      logout: async () => {
        try {
          await authApi.logout();
        } catch (error) {
          console.error('登出失败：', error);
        } finally {
          // 清除本地存储
          localStorage.removeItem('token');
          localStorage.removeItem('userInfo');
          localStorage.removeItem('tenantId');
          localStorage.removeItem('userId');
          
          set({ 
            token: null, 
            userInfo: null, 
            isLogin: false 
          });
        }
      },

      clearUserInfo: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userInfo');
        localStorage.removeItem('tenantId');
        localStorage.removeItem('userId');
        
        set({ 
          token: null, 
          userInfo: null, 
          isLogin: false 
        });
      },
    }),
    {
      name: 'user-storage',
      partialize: (state) => ({
        token: state.token,
        userInfo: state.userInfo,
        isLogin: state.isLogin,
      }),
    }
  )
);
