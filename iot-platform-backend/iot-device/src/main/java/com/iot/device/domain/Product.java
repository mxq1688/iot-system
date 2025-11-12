package com.iot.device.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品实体类（设备模板）
 *
 * @author IoT Platform
 */
@Data
@TableName("iot_product")
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    @TableId
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品编码
     */
    private String code;

    /**
     * 产品类型：1智能灯 2智能插座 3温湿度传感器 4门窗传感器 5其他
     */
    private Integer type;

    /**
     * 通信协议：1MQTT 2HTTP 3CoAP 4Modbus
     */
    private Integer protocol;

    /**
     * 设备类型：1直连设备 2网关设备 3子设备
     */
    private Integer deviceType;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 产品图标
     */
    private String icon;

    /**
     * 功能定义（JSON格式）
     * 示例：{"properties":[{"identifier":"power","name":"开关","dataType":"bool"}]}
     */
    private String features;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

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