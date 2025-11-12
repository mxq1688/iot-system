USE iot_platform;

-- ==================== 初始化租户 ====================

-- 插入默认租户
INSERT INTO sys_tenant (id, tenant_name, tenant_code, contact_name, contact_phone, status, max_device_count) 
VALUES 
('default_tenant', 'Default Tenant', 'DEFAULT', 'Admin', '13800138000', 1, 10000);

-- ==================== 初始化用户 ====================

-- 插入管理员用户（密码：admin123，BCrypt加密）
INSERT INTO sys_user (id, tenant_id, username, password, real_name, email, phone, status) 
VALUES 
('admin_user', 'default_tenant', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6V7Mm', 'Administrator', 'admin@iot-platform.com', '13800138000', 1);

-- ==================== 初始化角色 ====================

-- 插入管理员角色
INSERT INTO sys_role (id, tenant_id, role_name, role_code, description, status) 
VALUES 
('admin_role', 'default_tenant', '系统管理员', 'ADMIN', '拥有系统所有权限', 1),
('user_role', 'default_tenant', '普通用户', 'USER', '普通用户权限', 1);

-- 关联用户和角色
INSERT INTO sys_user_role (id, user_id, role_id) 
VALUES 
('admin_user_role', 'admin_user', 'admin_role');

-- ==================== 初始化设备产品（示例）====================

-- 插入示例产品
INSERT INTO device_product (id, tenant_id, product_name, product_key, device_type, protocol_type, data_format, description) 
VALUES 
('light_product', 'default_tenant', '智能灯', 'LIGHT_001', 'LIGHT', 'MQTT', 'JSON', '智能LED灯产品'),
('switch_product', 'default_tenant', '智能开关', 'SWITCH_001', 'SWITCH', 'MQTT', 'JSON', '智能开关产品'),
('sensor_product', 'default_tenant', '温湿度传感器', 'SENSOR_001', 'SENSOR', 'MQTT', 'JSON', '温湿度传感器产品');