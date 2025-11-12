# IoT Device Service - 设备管理服务

## 📖 服务介绍

设备管理服务是IoT平台的核心服务，负责设备的全生命周期管理、产品管理、设备数据处理和MQTT通信。

## 🎯 核心功能

### 1. 产品管理
- 产品CRUD操作
- 产品类型定义（智能灯、插座、传感器等）
- 产品功能定义（属性、事件、服务）
- 产品统计（关联设备数量）

### 2. 设备管理
- 设备CRUD操作
- 设备激活/注册
- 设备分组管理
- 设备状态监控（在线/离线）
- 设备密钥管理

### 3. 设备通信
- MQTT协议支持
- 设备数据上报接收
- 设备状态变化监听
- 设备控制指令下发

### 4. 数据存储
- 时序数据存储（InfluxDB）
- 历史数据查询
- 数据统计分析
- 最新数据快速查询

## 🏗️ 技术架构

### 技术栈
- **框架**: Spring Boot 3.2.0
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **时序数据库**: InfluxDB 2.7
- **缓存**: Redis 7.0
- **消息协议**: MQTT (EMQX 5.3.2)
- **API文档**: SpringDoc OpenAPI 2.3.0

### 端口配置
- **服务端口**: 8082
- **MQTT Broker**: 1883
- **InfluxDB**: 8086

## 📁 项目结构

```
iot-device/
├── src/main/
│   ├── java/com/iot/device/
│   │   ├── controller/          # 控制器层
│   │   │   ├── ProductController.java
│   │   │   ├── DeviceController.java
│   │   │   └── DeviceDataController.java
│   │   ├── service/             # 服务层
│   │   │   ├── ProductService.java
│   │   │   ├── DeviceService.java
│   │   │   ├── MqttMessageService.java
│   │   │   └── InfluxDbService.java
│   │   ├── mapper/              # 数据访问层
│   │   │   ├── ProductMapper.java
│   │   │   ├── DeviceMapper.java
│   │   │   └── DeviceGroupMapper.java
│   │   ├── domain/              # 实体类
│   │   │   ├── Product.java
│   │   │   ├── Device.java
│   │   │   └── DeviceGroup.java
│   │   ├── config/              # 配置类
│   │   │   └── MqttConfig.java
│   │   └── DeviceApplication.java
│   └── resources/
│       ├── application.yml
│       └── mapper/              # MyBatis XML
└── pom.xml
```

## 🚀 快速开始

### 前置条件
- JDK 17+
- Maven 3.8+
- MySQL 8.0
- Redis 7.0
- EMQX 5.3.2
- InfluxDB 2.7

### 启动步骤

#### 1. 启动基础设施
```bash
# 在项目根目录启动Docker Compose
cd ..
docker-compose up -d

# 验证服务状态
docker-compose ps
```

#### 2. 配置文件
确认 `application.yml` 中的配置正确：
```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/iot_platform
    username: root
    password: root123456
  redis:
    host: localhost
    port: 6379
    password: redis123456

mqtt:
  broker:
    url: tcp://localhost:1883
    username: admin
    password: public

influxdb:
  url: http://localhost:8086
  token: influxdb-token-123456
  org: iot-platform
  bucket: device-data
```

#### 3. 编译启动
```bash
# 编译项目
mvn clean install -DskipTests

# 启动服务
mvn spring-boot:run

# 或使用JAR包
java -jar target/iot-device-1.0.0-SNAPSHOT.jar
```

#### 4. 验证服务
```bash
# 检查服务是否启动
curl http://localhost:8082/actuator/health

# 访问API文档
http://localhost:8082/swagger-ui.html
```

## 📋 API接口

### 产品管理

#### 创建产品
```bash
POST /product/create
Content-Type: application/json
X-Tenant-Id: default_tenant
X-User-Id: admin

{
  "name": "智能灯泡",
  "code": "SMART_BULB_001",
  "type": 1,
  "protocol": 1,
  "deviceType": 1,
  "description": "智能RGB灯泡",
  "features": "{\"properties\":[{\"identifier\":\"power\",\"name\":\"开关\",\"dataType\":\"bool\"}]}"
}
```

#### 产品列表
```bash
GET /product/list?pageNum=1&pageSize=10&keyword=智能
X-Tenant-Id: default_tenant
```

### 设备管理

#### 创建设备
```bash
POST /device/create
Content-Type: application/json
X-Tenant-Id: default_tenant
X-User-Id: admin

{
  "productId": "产品ID",
  "name": "客厅灯泡",
  "code": "DEVICE_001",
  "location": "客厅",
  "description": "客厅主灯"
}
```

#### 设备列表
```bash
GET /device/list?pageNum=1&pageSize=10&status=1
X-Tenant-Id: default_tenant
```

#### 激活设备
```bash
POST /device/activate/{deviceId}
```

### 设备数据

#### 查询历史数据
```bash
GET /device/data/history?deviceId=xxx&startTime=-1h&endTime=now()
```

#### 查询最新数据
```bash
GET /device/data/latest?deviceId=xxx
```

#### 数据统计
```bash
GET /device/data/statistics?deviceId=xxx&field=temperature&startTime=-24h&endTime=now()&windowPeriod=1h
```

## 🔌 MQTT通信

### Topic规范

#### 设备上报数据
```
Topic: device/{deviceId}/data
Payload: {
  "temperature": 25.5,
  "humidity": 60,
  "timestamp": 1699999999999
}
```

#### 设备状态变化
```
Topic: device/{deviceId}/status
Payload: {
  "status": 1,  // 1在线 0离线
  "timestamp": 1699999999999
}
```

#### 设备控制指令
```
Topic: device/{deviceId}/control
Payload: {
  "command": "power",
  "value": true,
  "timestamp": 1699999999999
}
```

### 测试MQTT

使用MQTT客户端工具（如MQTTX）连接到EMQX：
```
Broker: localhost:1883
Username: admin
Password: public
```

发布测试消息：
```bash
# 设备上报数据
mosquitto_pub -h localhost -p 1883 -u admin -P public \
  -t "device/test001/data" \
  -m '{"temperature":25.5,"humidity":60}'

# 设备状态变化
mosquitto_pub -h localhost -p 1883 -u admin -P public \
  -t "device/test001/status" \
  -m '{"status":1}'
```

## 📊 数据库表结构

### iot_product - 产品表
```sql
CREATE TABLE iot_product (
  id VARCHAR(32) PRIMARY KEY,
  tenant_id VARCHAR(32),
  name VARCHAR(100),
  code VARCHAR(50) UNIQUE,
  type INT,
  protocol INT,
  device_type INT,
  features TEXT,
  status INT DEFAULT 1,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

### iot_device - 设备表
```sql
CREATE TABLE iot_device (
  id VARCHAR(32) PRIMARY KEY,
  tenant_id VARCHAR(32),
  product_id VARCHAR(32),
  name VARCHAR(100),
  code VARCHAR(50) UNIQUE,
  secret VARCHAR(64),
  status INT DEFAULT 0,
  activated INT DEFAULT 0,
  last_online_time TIMESTAMP,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

## 🔧 配置说明

### MQTT配置
```yaml
mqtt:
  broker:
    url: tcp://localhost:1883      # MQTT Broker地址
    username: admin                # MQTT用户名
    password: public               # MQTT密码
    client-id: iot-device-service  # 客户端ID
  topics:
    device-data: device/+/data     # 设备数据主题
    device-status: device/+/status # 设备状态主题
    device-control: device/+/control # 设备控制主题
```

### InfluxDB配置
```yaml
influxdb:
  url: http://localhost:8086         # InfluxDB地址
  token: influxdb-token-123456       # 访问令牌
  org: iot-platform                  # 组织名称
  bucket: device-data                # 数据桶名称
```

## 📈 监控和日志

### 日志位置
```
logs/iot-device.log
```

### Druid监控
```
http://localhost:8082/druid
```

## 🐛 常见问题

### 1. MQTT连接失败
- 检查EMQX是否启动：`docker ps | grep emqx`
- 检查用户名密码是否正确
- 检查防火墙端口1883是否开放

### 2. InfluxDB写入失败
- 检查InfluxDB是否启动
- 验证Token是否正确
- 确认Bucket已创建

### 3. 设备状态未更新
- 检查Redis连接
- 查看MQTT消息是否正确接收
- 检查设备ID是否匹配

## 📝 待开发功能

- [ ] 设备分组管理完善
- [ ] 设备影子（Device Shadow）
- [ ] OTA固件升级
- [ ] 设备日志收集
- [ ] 批量设备操作
- [ ] 设备模拟器

## 👥 联系方式

- 项目地址: [GitHub](https://github.com/your-repo/iot-platform)
- 问题反馈: [Issues](https://github.com/your-repo/iot-platform/issues)

---

**IoT Device Service** v1.0.0