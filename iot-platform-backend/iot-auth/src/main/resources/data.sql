-- 插入默认租户
INSERT INTO sys_tenant (id, tenant_name, tenant_code, contact_name, status, max_device_count, created_at, updated_at)
VALUES ('default_tenant', '默认租户', 'DEFAULT', '管理员', 1, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入管理员用户 (密码: admin123，BCrypt加密)
INSERT INTO sys_user (id, tenant_id, username, password, real_name, email, status, created_at, updated_at)
VALUES ('admin', 'default_tenant', 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@iot.com', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入管理员角色
INSERT INTO sys_role (id, tenant_id, role_name, role_code, description, status, created_at, updated_at)
VALUES ('admin_role', 'default_tenant', '管理员', 'ADMIN', '系统管理员角色', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 绑定用户角色
INSERT INTO sys_user_role (id, user_id, role_id, created_at)
VALUES ('1', 'admin', 'admin_role', CURRENT_TIMESTAMP);

-- 插入测试产品
INSERT INTO device_product (id, tenant_id, product_name, product_key, device_type, protocol_type, data_format, node_type, description, created_at, updated_at)
VALUES 
('prod_001', 'default_tenant', '智能灯泡', 'SMART_BULB_001', 'LIGHT', 'MQTT', 'JSON', 0, '智能LED灯泡，支持调光调色', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('prod_002', 'default_tenant', '智能开关', 'SMART_SWITCH_001', 'SWITCH', 'MQTT', 'JSON', 0, '智能墙壁开关，双路控制', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('prod_003', 'default_tenant', '温湿度传感器', 'TEMP_SENSOR_001', 'SENSOR', 'MQTT', 'JSON', 0, '温湿度监测传感器', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试设备
INSERT INTO device (id, tenant_id, product_id, device_name, device_key, device_secret, status, online_status, created_at, updated_at)
VALUES 
('dev_001', 'default_tenant', 'prod_001', '客厅灯泡', 'DEVICE_BULB_001', '$2a$10$secretkey001', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('dev_002', 'default_tenant', 'prod_002', '卧室开关', 'DEVICE_SWITCH_001', '$2a$10$secretkey002', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('dev_003', 'default_tenant', 'prod_003', '客厅温湿度', 'DEVICE_SENSOR_001', '$2a$10$secretkey003', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
