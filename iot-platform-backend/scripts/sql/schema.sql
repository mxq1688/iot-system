-- 创建数据库
CREATE DATABASE IF NOT EXISTS iot_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS nacos_config DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE iot_platform;

-- ==================== 租户相关 ====================

-- 租户表
CREATE TABLE sys_tenant (
    id VARCHAR(64) PRIMARY KEY COMMENT '租户ID',
    tenant_name VARCHAR(100) NOT NULL COMMENT '租户名称',
    tenant_code VARCHAR(50) UNIQUE NOT NULL COMMENT '租户编码',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    expire_time DATETIME COMMENT '过期时间',
    max_device_count INT DEFAULT 100 COMMENT '设备数量上限',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tenant_code (tenant_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ==================== 用户相关 ====================

-- 用户表
CREATE TABLE sys_user (
    id VARCHAR(64) PRIMARY KEY COMMENT '用户ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    FOREIGN KEY (tenant_id) REFERENCES sys_tenant(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE sys_role (
    id VARCHAR(64) PRIMARY KEY COMMENT '角色ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id),
    UNIQUE KEY uk_tenant_role (tenant_id, role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    role_id VARCHAR(64) NOT NULL COMMENT '角色ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ==================== 设备相关 ====================

-- 设备产品表
CREATE TABLE device_product (
    id VARCHAR(64) PRIMARY KEY COMMENT '产品ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    product_key VARCHAR(50) UNIQUE NOT NULL COMMENT '产品Key',
    device_type VARCHAR(50) NOT NULL COMMENT '设备类型：LIGHT/SWITCH/SENSOR',
    protocol_type VARCHAR(20) NOT NULL COMMENT '协议类型：MQTT/HTTP/TCP',
    data_format VARCHAR(20) DEFAULT 'JSON' COMMENT '数据格式',
    node_type TINYINT DEFAULT 0 COMMENT '节点类型：0直连 1网关 2子设备',
    description TEXT COMMENT '产品描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_product_key (product_key),
    FOREIGN KEY (tenant_id) REFERENCES sys_tenant(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备产品表';

-- 设备表
CREATE TABLE device (
    id VARCHAR(64) PRIMARY KEY COMMENT '设备ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    product_id VARCHAR(64) NOT NULL COMMENT '产品ID',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    device_key VARCHAR(50) UNIQUE NOT NULL COMMENT '设备Key',
    device_secret VARCHAR(64) NOT NULL COMMENT '设备密钥（BCrypt加密）',
    device_source VARCHAR(20) DEFAULT 'SELF' COMMENT '设备来源：SELF/TMALL/BAIDU/HA',
    third_party_id VARCHAR(100) COMMENT '三方平台设备ID',
    online_status TINYINT DEFAULT 0 COMMENT '在线状态：1在线 0离线',
    active_status TINYINT DEFAULT 0 COMMENT '激活状态：1已激活 0未激活',
    location VARCHAR(255) COMMENT '设备位置',
    tags JSON COMMENT '设备标签',
    last_online_time DATETIME COMMENT '最后在线时间',
    last_offline_time DATETIME COMMENT '最后离线时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_product_id (product_id),
    INDEX idx_device_key (device_key),
    INDEX idx_online_status (online_status),
    INDEX idx_device_source (device_source),
    FOREIGN KEY (tenant_id) REFERENCES sys_tenant(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES device_product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';

-- 设备分组表
CREATE TABLE device_group (
    id VARCHAR(64) PRIMARY KEY COMMENT '分组ID',
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    group_name VARCHAR(100) NOT NULL COMMENT '分组名称',
    parent_id VARCHAR(64) COMMENT '父分组ID',
    description VARCHAR(255) COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备分组表';

-- 设备分组关联表
CREATE TABLE device_group_mapping (
    id VARCHAR(64) PRIMARY KEY,
    device_id VARCHAR(64) NOT NULL COMMENT '设备ID',
    group_id VARCHAR(64) NOT NULL COMMENT '分组ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_device_group (device_id, group_id),
    INDEX idx_device_id (device_id),
    INDEX idx_group_id (group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备分组关联表';

-- ==================== 三方平台 ====================

-- 三方平台配置表
CREATE TABLE third_party_config (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    platform_type VARCHAR(20) NOT NULL COMMENT '平台类型：TMALL/BAIDU/HA',
    platform_name VARCHAR(100) COMMENT '平台名称',
    app_key VARCHAR(100) COMMENT 'App Key',
    app_secret TEXT COMMENT 'App Secret（加密）',
    access_token TEXT COMMENT '访问令牌（加密）',
    refresh_token TEXT COMMENT '刷新令牌（加密）',
    token_expire_time DATETIME COMMENT '令牌过期时间',
    config_data JSON COMMENT '其他配置',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_platform (tenant_id, platform_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='三方平台配置表';

-- Home Assistant实例表
CREATE TABLE ha_instance (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL COMMENT '租户ID',
    instance_name VARCHAR(100) NOT NULL COMMENT '实例名称',
    base_url VARCHAR(255) NOT NULL COMMENT 'HA地址',
    access_token TEXT NOT NULL COMMENT '访问令牌（加密）',
    status VARCHAR(20) DEFAULT 'OFFLINE' COMMENT '状态：ONLINE/OFFLINE',
    device_count INT DEFAULT 0 COMMENT '设备数量',
    last_sync_time DATETIME COMMENT '最后同步时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Home Assistant实例表';

-- ==================== 规则引擎 ====================

-- 场景规则表
CREATE TABLE rule_scene (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    scene_name VARCHAR(100) NOT NULL COMMENT '场景名称',
    scene_type VARCHAR(20) NOT NULL COMMENT '场景类型：AUTO/MANUAL',
    trigger_conditions JSON NOT NULL COMMENT '触发条件（JSON）',
    execution_actions JSON NOT NULL COMMENT '执行动作（JSON）',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    execute_count INT DEFAULT 0 COMMENT '执行次数',
    last_execute_time DATETIME COMMENT '最后执行时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景规则表';

-- 规则执行日志表
CREATE TABLE rule_execute_log (
    id VARCHAR(64) PRIMARY KEY,
    scene_id VARCHAR(64) NOT NULL,
    execute_status VARCHAR(20) COMMENT '执行状态：SUCCESS/FAILED',
    execute_result TEXT COMMENT '执行结果',
    error_message TEXT COMMENT '错误信息',
    execute_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_scene_id (scene_id),
    INDEX idx_execute_time (execute_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则执行日志表';

-- ==================== 告警相关 ====================

-- 告警规则表
CREATE TABLE alarm_rule (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(20) NOT NULL COMMENT '规则类型：DEVICE_OFFLINE/DATA_ABNORMAL',
    target_type VARCHAR(20) COMMENT '目标类型：DEVICE/GROUP/PRODUCT',
    target_id VARCHAR(64) COMMENT '目标ID',
    condition_expr TEXT NOT NULL COMMENT '条件表达式',
    alarm_level VARCHAR(20) DEFAULT 'INFO' COMMENT '告警级别：INFO/WARNING/ERROR/CRITICAL',
    notify_channels JSON COMMENT '通知渠道：["EMAIL","SMS","WECHAT"]',
    notify_users JSON COMMENT '通知用户列表',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警规则表';

-- 告警记录表
CREATE TABLE alarm_record (
    id VARCHAR(64) PRIMARY KEY,
    rule_id VARCHAR(64) NOT NULL,
    tenant_id VARCHAR(64) NOT NULL,
    alarm_title VARCHAR(200) NOT NULL COMMENT '告警标题',
    alarm_content TEXT COMMENT '告警内容',
    alarm_level VARCHAR(20) COMMENT '告警级别',
    device_id VARCHAR(64) COMMENT '关联设备',
    alarm_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '处理状态：PENDING/CONFIRMED/RESOLVED',
    confirmed_by VARCHAR(64) COMMENT '确认人',
    confirmed_time DATETIME COMMENT '确认时间',
    alarm_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '告警时间',
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_alarm_time (alarm_time),
    INDEX idx_alarm_status (alarm_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';