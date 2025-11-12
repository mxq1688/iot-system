package com.iot.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.device.domain.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备Mapper接口
 *
 * @author IoT Platform
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    /**
     * 根据产品ID统计设备数量
     */
    Integer countByProductId(@Param("productId") String productId);

    /**
     * 根据分组ID统计设备数量
     */
    Integer countByGroupId(@Param("groupId") String groupId);

    /**
     * 查询在线设备列表
     */
    List<Device> selectOnlineDevices(@Param("tenantId") String tenantId);
}