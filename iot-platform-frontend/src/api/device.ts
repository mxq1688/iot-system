/**
 * 设备管理API
 */
import { http } from '../utils/request';
import type { Device, PageParams, PageResponse, DeviceData, DeviceLatestData, DeviceDataStatistics } from '../types';

/**
 * 获取设备列表（分页）
 */
export const getDeviceList = (params: PageParams & {
  keyword?: string;
  productId?: string;
  status?: number;
}): Promise<PageResponse<Device>> => {
  return http.get('/device/list', { params });
};

/**
 * 获取设备详情
 */
export const getDeviceDetail = (id: string): Promise<Device> => {
  return http.get(`/device/detail/${id}`);
};

/**
 * 创建设备
 */
export const createDevice = (data: Partial<Device>): Promise<Device> => {
  return http.post('/device/create', data);
};

/**
 * 更新设备
 */
export const updateDevice = (data: Device): Promise<Device> => {
  return http.put('/device/update', data);
};

/**
 * 删除设备
 */
export const deleteDevice = (id: string): Promise<void> => {
  return http.delete(`/device/delete/${id}`);
};

/**
 * 激活设备
 */
export const activateDevice = (id: string): Promise<void> => {
  return http.post(`/device/activate/${id}`);
};

/**
 * 更新设备状态
 */
export const updateDeviceStatus = (deviceId: string, status: number): Promise<void> => {
  return http.post('/device/status', null, {
    params: { deviceId, status },
  });
};

/**
 * 获取在线设备列表
 */
export const getOnlineDevices = (): Promise<Device[]> => {
  return http.get('/device/online');
};

/**
 * 获取设备历史数据
 */
export const getDeviceHistory = (params: {
  deviceId: string;
  startTime: string;
  endTime: string;
}): Promise<DeviceData[]> => {
  return http.get('/device/data/history', { params });
};

/**
 * 获取设备最新数据
 */
export const getDeviceLatestData = (deviceId: string): Promise<DeviceLatestData> => {
  return http.get('/device/data/latest', {
    params: { deviceId },
  });
};

/**
 * 获取设备数据统计
 */
export const getDeviceStatistics = (params: {
  deviceId: string;
  field: string;
  startTime: string;
  endTime: string;
  windowPeriod?: string;
}): Promise<DeviceDataStatistics> => {
  return http.get('/device/data/statistics', { params });
};
