/**
 * 产品管理API
 */
import { http } from '../utils/request';
import type { Product, PageParams, PageResponse } from '../types';

/**
 * 获取产品列表（分页）
 */
export const getProductList = (params: PageParams & { keyword?: string }): Promise<PageResponse<Product>> => {
  return http.get('/product/list', { params });
};

/**
 * 获取产品详情
 */
export const getProductDetail = (id: string): Promise<Product> => {
  return http.get(`/product/detail/${id}`);
};

/**
 * 创建产品
 */
export const createProduct = (data: Partial<Product>): Promise<Product> => {
  return http.post('/product/create', data);
};

/**
 * 更新产品
 */
export const updateProduct = (data: Product): Promise<Product> => {
  return http.put('/product/update', data);
};

/**
 * 删除产品
 */
export const deleteProduct = (id: string): Promise<void> => {
  return http.delete(`/product/delete/${id}`);
};
