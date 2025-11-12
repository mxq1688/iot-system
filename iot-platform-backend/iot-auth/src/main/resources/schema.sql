-- 租户表
CREATE TABLE IF NOT EXISTS sys_tenant (
    id VARCHAR(64) PRIMARY KEY,
    tenant_name VARCHAR(100) NOT NULL,
    tenant_code VARCHAR(50) NOT NULL UNIQUE,
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    status TINYINT DEFAULT 1,
    expire_time TIMESTAMP,
    max_device_count INT DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    status TINYINT DEFAULT 1,
    last_login_time TIMESTAMP,
    last_login_ip VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    role_id VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 设备产品表
CREATE TABLE IF NOT EXISTS device_product (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_key VARCHAR(50) NOT NULL UNIQUE,
    device_type VARCHAR(50) NOT NULL,
    protocol_type VARCHAR(20) NOT NULL,
    data_format VARCHAR(20) DEFAULT 'JSON',
    node_type TINYINT DEFAULT 0,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 设备表
CREATE TABLE IF NOT EXISTS device (
    id VARCHAR(64) PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    product_id VARCHAR(64) NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    device_key VARCHAR(50) NOT NULL UNIQUE,
    device_secret VARCHAR(64) NOT NULL,
    status TINYINT DEFAULT 1,
    online_status TINYINT DEFAULT 0,
    last_online_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
