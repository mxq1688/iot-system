package com.iot.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.device.domain.DeviceGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备分组Mapper接口
 *
 * @author IoT Platform
 */
@Mapper
public interface DeviceGroupMapper extends BaseMapper<DeviceGroup> {
}