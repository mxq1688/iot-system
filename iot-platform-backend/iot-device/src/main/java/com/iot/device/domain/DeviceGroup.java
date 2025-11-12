package com.iot.device.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备分组实体类
 *
 * @author IoT Platform
 */
@Data
@TableName("iot_device_group")
public class DeviceGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分组ID
     */
    @TableId
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 父分组ID
     */
    private String parentId;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 设备数量
     */
    private Integer deviceCount;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}