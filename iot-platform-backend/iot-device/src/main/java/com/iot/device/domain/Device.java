package com.iot.device.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备实体类
 *
 * @author IoT Platform
 */
@Data
@TableName("iot_device")
public class Device implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备编码（唯一标识）
     */
    private String code;

    /**
     * 设备密钥
     */
    private String secret;

    /**
     * 设备分组ID
     */
    private String groupId;

    /**
     * 设备状态：1在线 0离线
     */
    private Integer status;

    /**
     * 激活状态：1已激活 0未激活
     */
    private Integer activated;

    /**
     * 设备位置
     */
    private String location;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 设备描述
     */
    private String description;

    /**
     * 设备标签（JSON数组）
     */
    private String tags;

    /**
     * 最后在线时间
     */
    private LocalDateTime lastOnlineTime;

    /**
     * 最后离线时间
     */
    private LocalDateTime lastOfflineTime;

    /**
     * 激活时间
     */
    private LocalDateTime activatedAt;

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