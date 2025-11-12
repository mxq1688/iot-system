package com.iot.device.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iot.common.core.exception.BusinessException;
import com.iot.common.redis.service.RedisService;
import com.iot.device.domain.Device;
import com.iot.device.domain.Product;
import com.iot.device.mapper.DeviceMapper;
import com.iot.device.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 设备管理服务
 *
 * @author IoT Platform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceMapper deviceMapper;
    private final ProductMapper productMapper;
    private final RedisService redisService;

    private static final String DEVICE_STATUS_KEY_PREFIX = "device:status:";
    private static final String DEVICE_DATA_KEY_PREFIX = "device:data:";

    /**
     * 分页查询设备列表
     */
    public IPage<Device> getDevicePage(int pageNum, int pageSize, String keyword, String productId, 
                                        Integer status, String tenantId) {
        Page<Device> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Device::getTenantId, tenantId);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Device::getName, keyword)
                    .or()
                    .like(Device::getCode, keyword));
        }
        
        if (StringUtils.hasText(productId)) {
            queryWrapper.eq(Device::getProductId, productId);
        }
        
        if (status != null) {
            queryWrapper.eq(Device::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Device::getCreatedAt);
        return deviceMapper.selectPage(page, queryWrapper);
    }

    /**
     * 获取设备详情
     */
    public Device getDeviceById(String id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }
        
        // 从Redis获取实时状态
        String statusKey = DEVICE_STATUS_KEY_PREFIX + id;
        Integer realtimeStatus = redisService.get(statusKey, Integer.class);
        if (realtimeStatus != null) {
            device.setStatus(realtimeStatus);
        }
        
        return device;
    }

    /**
     * 创建设备
     */
    @Transactional(rollbackFor = Exception.class)
    public Device createDevice(Device device) {
        // 校验产品是否存在
        Product product = productMapper.selectById(device.getProductId());
        if (product == null) {
            throw new BusinessException("产品不存在");
        }
        
        // 校验设备编码唯一性
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Device::getCode, device.getCode());
        queryWrapper.eq(Device::getTenantId, device.getTenantId());
        if (deviceMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("设备编码已存在");
        }
        
        device.setId(UUID.randomUUID().toString().replace("-", ""));
        device.setSecret(generateDeviceSecret());
        device.setStatus(0); // 默认离线
        device.setActivated(0); // 默认未激活
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        
        deviceMapper.insert(device);
        log.info("创建设备成功，deviceId: {}, code: {}", device.getId(), device.getCode());
        
        return device;
    }

    /**
     * 更新设备
     */
    @Transactional(rollbackFor = Exception.class)
    public Device updateDevice(Device device) {
        Device existDevice = deviceMapper.selectById(device.getId());
        if (existDevice == null) {
            throw new BusinessException("设备不存在");
        }
        
        // 校验设备编码唯一性（排除自己）
        if (StringUtils.hasText(device.getCode()) && !device.getCode().equals(existDevice.getCode())) {
            LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Device::getCode, device.getCode());
            queryWrapper.eq(Device::getTenantId, existDevice.getTenantId());
            queryWrapper.ne(Device::getId, device.getId());
            if (deviceMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("设备编码已存在");
            }
        }
        
        device.setUpdatedAt(LocalDateTime.now());
        deviceMapper.updateById(device);
        log.info("更新设备成功，deviceId: {}", device.getId());
        
        return deviceMapper.selectById(device.getId());
    }

    /**
     * 删除设备
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDevice(String id) {
        Device device = deviceMapper.selectById(id);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }
        
        deviceMapper.deleteById(id);
        
        // 清除Redis缓存
        redisService.delete(DEVICE_STATUS_KEY_PREFIX + id);
        redisService.delete(DEVICE_DATA_KEY_PREFIX + id);
        
        log.info("删除设备成功，deviceId: {}", id);
    }

    /**
     * 更新设备在线状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDeviceStatus(String deviceId, Integer status) {
        Device device = new Device();
        device.setId(deviceId);
        device.setStatus(status);
        
        if (status == 1) {
            device.setLastOnlineTime(LocalDateTime.now());
        } else {
            device.setLastOfflineTime(LocalDateTime.now());
        }
        
        deviceMapper.updateById(device);
        
        // 更新Redis缓存
        String statusKey = DEVICE_STATUS_KEY_PREFIX + deviceId;
        redisService.set(statusKey, status, 1, TimeUnit.HOURS);
        
        log.info("更新设备状态成功，deviceId: {}, status: {}", deviceId, status == 1 ? "在线" : "离线");
    }

    /**
     * 激活设备
     */
    @Transactional(rollbackFor = Exception.class)
    public void activateDevice(String deviceId) {
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException("设备不存在");
        }
        
        if (device.getActivated() == 1) {
            throw new BusinessException("设备已激活");
        }
        
        Device updateDevice = new Device();
        updateDevice.setId(deviceId);
        updateDevice.setActivated(1);
        updateDevice.setActivatedAt(LocalDateTime.now());
        
        deviceMapper.updateById(updateDevice);
        log.info("激活设备成功，deviceId: {}", deviceId);
    }

    /**
     * 查询在线设备列表
     */
    public List<Device> getOnlineDevices(String tenantId) {
        return deviceMapper.selectOnlineDevices(tenantId);
    }

    /**
     * 生成设备密钥
     */
    private String generateDeviceSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}