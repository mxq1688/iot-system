# IoT Platform Backend - 后端服务

## 📖 项目介绍

IoT智能物联网平台后端服务，基于Spring Boot微服务架构，提供设备管理、数据处理、规则引擎等核心功能。

## 🏗️ 技术架构

### 核心技术栈
- **框架**: Spring Boot 3.2.0
- **微服务**: Spring Cloud 2023.0.0 + Spring Cloud Alibaba 2022.0.0.0
- **注册中心**: Nacos 2.3.0
- **网关**: Spring Cloud Gateway
- **数据库**: MySQL 8.0
- **时序数据库**: InfluxDB 2.7
- **缓存**: Redis 7.0
- **消息队列**: RabbitMQ 3.12
- **物联网协议**: MQTT (EMQX 5.3.2)
- **ORM框架**: MyBatis-Plus 3.5.5
- **API文档**: SpringDoc OpenAPI 2.3.0

## 📁 项目结构

```
iot-platform-backend/
├── iot-common/                    # 公共模块
│   ├── iot-common-core/          # 核心工具类
│   ├── iot-common-security/      # 安全组件
│   ├── iot-common-redis/         # Redis组件
│   ├── iot-common-mq/            # MQ组件 (待开发)
│   └── iot-common-log/           # 日志组件 (待开发)
├── iot-auth/                      # 认证授权服务 ✅
│   ├── src/main/
│   │   ├── java/com/iot/auth/
│   │   │   ├── controller/       # 登录、登出接口
│   │   │   ├── service/          # 认证业务逻辑
│   │   │   └── config/           # Security配置
│   │   └── resources/
│   │       └── application.yml
│   └── pom.xml
├── iot-device/                    # 设备管理服务 ✅
│   ├── src/main/
│   │   ├── java/com/iot/device/
│   │   │   ├── controller/       # 产品、设备、数据接口
│   │   │   ├── service/          # 设备业务逻辑
│   │   │   ├── mapper/           # 数据访问层
│   │   │   └── config/           # MQTT配置
│   │   └── resources/
│   │       └── application.yml
│   ├── README.md
│   └── pom.xml
├── iot-gateway/                   # API网关 (待开发)
├── iot-rule-engine/               # 规则引擎 (待开发)
├── iot-third-party/               # 三方集成 (待开发)
├── iot-alarm/                     # 告警服务 (待开发)
├── scripts/
│   └── sql/
│       ├── schema.sql            # 数据库结构
│       └── data.sql              # 初始数据
├── docker-compose.yml             # Docker编排
├── pom.xml                        # 父POM
└── README.md                      # 项目文档
```

## 🚀 快速开始

### 前置条件
- JDK 17+
- Maven 3.8+
- Docker & Docker Compose

### 启动步骤

#### 1. 启动基础设施
```bash
# 启动MySQL、Redis、EMQX、InfluxDB等
docker-compose up -d

# 验证服务状态
docker-compose ps
```

#### 2. 初始化数据库
```bash
# 执行数据库脚本
mysql -h localhost -u root -proot123456 < scripts/sql/schema.sql
mysql -h localhost -u root -proot123456 < scripts/sql/data.sql
```

#### 3. 编译项目
```bash
# 在iot-platform-backend目录下执行
mvn clean install -DskipTests
```

#### 4. 启动服务

**方式1：逐个启动（推荐开发环境）**
```bash
# 启动认证服务 (端口8081)
cd iot-auth
mvn spring-boot:run

# 启动设备服务 (端口8082)
cd iot-device
mvn spring-boot:run
```

**方式2：JAR包启动**
```bash
# 认证服务
java -jar iot-auth/target/iot-auth-1.0.0-SNAPSHOT.jar

# 设备服务
java -jar iot-device/target/iot-device-1.0.0-SNAPSHOT.jar
```

#### 5. 验证服务
```bash
# 认证服务
curl http://localhost:8081/actuator/health

# 设备服务
curl http://localhost:8082/actuator/health

# API文档
http://localhost:8081/swagger-ui.html  # 认证服务
http://localhost:8082/swagger-ui.html  # 设备服务
```

## 🔌 服务端口

| 服务 | 端口 | 描述 |
|------|------|------|
| iot-auth | 8081 | 认证授权服务 |
| iot-device | 8082 | 设备管理服务 |
| iot-gateway | 8080 | API网关 (待开发) |
| iot-rule-engine | 8083 | 规则引擎 (待开发) |
| MySQL | 3306 | 关系数据库 |
| Redis | 6379 | 缓存 |
| EMQX | 1883/18083 | MQTT Broker |
| InfluxDB | 8086 | 时序数据库 |
| RabbitMQ | 5672/15672 | 消息队列 |
| Nacos | 8848 | 注册配置中心 |

## 📋 API接口

### 认证服务 (iot-auth:8081)

#### 用户登录
```bash
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

# 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {...}
  }
}
```

### 设备服务 (iot-device:8082)

#### 创建产品
```bash
POST /product/create
Content-Type: application/json
Authorization: Bearer {token}
X-Tenant-Id: default_tenant
X-User-Id: admin

{
  "name": "智能灯泡",
  "code": "SMART_BULB_001",
  "type": 1,
  "protocol": 1
}
```

#### 创建设备
```bash
POST /device/create
Content-Type: application/json
Authorization: Bearer {token}
X-Tenant-Id: default_tenant

{
  "productId": "产品ID",
  "name": "客厅灯泡",
  "code": "DEVICE_001"
}
```

## 🗄️ 数据库配置

### MySQL配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/iot_platform
    username: root
    password: root123456
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: redis123456
    database: 0
```

### InfluxDB配置
```yaml
influxdb:
  url: http://localhost:8086
  token: influxdb-token-123456
  org: iot-platform
  bucket: device-data
```

## 🔧 配置管理

### Nacos配置中心

访问Nacos控制台：http://localhost:8848/nacos
- 用户名：nacos
- 密码：nacos

配置文件命名规范：
```
${spring.application.name}-${profile}.${file-extension}

示例：
iot-auth-dev.yml
iot-device-dev.yml
```

## 📊 监控和管理

### 1. Druid数据源监控
```
http://localhost:8081/druid  # 认证服务
http://localhost:8082/druid  # 设备服务
```

### 2. EMQX Dashboard
```
http://localhost:18083
用户名: admin
密码: public
```

### 3. RabbitMQ管理界面
```
http://localhost:15672
用户名: admin
密码: admin123456
```

## 🧪 测试

### 单元测试
```bash
mvn test
```

### 集成测试
```bash
mvn verify
```

## 📦 打包部署

### 打包所有模块
```bash
mvn clean package -DskipTests
```

### Docker镜像构建
```bash
# 认证服务
cd iot-auth
docker build -t iot-auth:1.0.0 .

# 设备服务
cd iot-device
docker build -t iot-device:1.0.0 .
```

## 🐛 常见问题

### 1. 端口被占用
```bash
# 查看端口占用
netstat -tuln | grep 8081

# 修改application.yml中的端口
server:
  port: 8091
```

### 2. MySQL连接失败
- 检查Docker容器是否启动
- 验证用户名密码
- 确认数据库已创建

### 3. Redis连接失败
- 检查Redis服务状态
- 验证密码配置
- 检查防火墙设置

### 4. MQTT连接问题
- 验证EMQX是否启动：`docker ps | grep emqx`
- 检查Topic权限配置
- 查看EMQX日志：`docker logs emqx`

## 📝 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 接口添加Swagger注解

### Git提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

## 📈 开发进度

- ✅ 基础架构搭建
- ✅ 公共模块（core, security, redis）
- ✅ 认证授权服务（iot-auth）
- ✅ 设备管理服务（iot-device）
- ⏳ 规则引擎服务（iot-rule-engine）
- ⏳ API网关（iot-gateway）
- ⏳ 告警服务（iot-alarm）
- ⏳ 三方集成服务（iot-third-party）

## 🔗 相关链接

- [前端项目](../iot-platform-frontend/README.md)
- [开发计划](../docs/development-plan.md)
- [API文档](http://localhost:8080/swagger-ui.html)

## 👥 联系方式

- 项目地址: [GitHub](https://github.com/your-repo/iot-platform)
- 问题反馈: [Issues](https://github.com/your-repo/iot-platform/issues)

---

**IoT Platform Backend** v1.0.0