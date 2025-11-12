# IoT Platform - 企业级智能物联网平台

## 📖 项目介绍

企业级IoT设备管理平台，支持海量设备接入、实时数据采集、规则引擎、告警管理、数据分析等功能。采用前后端完全分离架构，后端基于Spring Boot微服务，前端基于Vue3 + TypeScript。

## 🏗️ 项目架构

```
前后端分离架构

┌─────────────────────────────────────────────────────────┐
│                    前端 (Vue 3)                          │
│  iot-platform-frontend/                                 │
│  ├── 登录认证                                            │
│  ├── 设备管理（产品、设备、分组）                          │
│  ├── 数据可视化（实时监控、历史数据）                       │
│  ├── 规则引擎（场景联动）                                  │
│  ├── 告警中心                                            │
│  └── 系统管理                                            │
└─────────────────────────────────────────────────────────┘
                          ↓ HTTP/WebSocket
┌─────────────────────────────────────────────────────────┐
│                  后端 (Spring Boot)                      │
│  iot-platform-backend/                                  │
│  ├── iot-gateway (API网关)                              │
│  ├── iot-auth (认证服务) ✅                              │
│  ├── iot-device (设备服务) ✅                            │
│  ├── iot-rule-engine (规则引擎)                          │
│  ├── iot-alarm (告警服务)                                │
│  └── iot-common (公共模块) ✅                            │
└─────────────────────────────────────────────────────────┘
                          ↓ MQTT
┌─────────────────────────────────────────────────────────┐
│                   物联网设备层                            │
│  智能灯、传感器、网关设备等                                │
└─────────────────────────────────────────────────────────┘
```

## 📁 项目结构

```
iot-platform/
├── iot-platform-backend/          # 后端项目（Spring Boot）
│   ├── iot-common/                # 公共模块
│   │   ├── iot-common-core/      # 核心工具
│   │   ├── iot-common-security/  # 安全组件
│   │   └── iot-common-redis/     # Redis组件
│   ├── iot-auth/                  # 认证服务 ✅
│   ├── iot-device/                # 设备服务 ✅
│   ├── iot-gateway/               # API网关 (待开发)
│   ├── iot-rule-engine/           # 规则引擎 (待开发)
│   ├── scripts/                   # SQL脚本
│   ├── docker-compose.yml         # Docker编排
│   ├── pom.xml                    # Maven父POM
│   └── README.md                  # 后端文档
│
├── iot-platform-frontend/         # 前端项目（Vue 3）(待初始化)
│   ├── src/
│   │   ├── api/                  # API接口
│   │   ├── views/                # 页面组件
│   │   ├── components/           # 公共组件
│   │   ├── router/               # 路由配置
│   │   ├── store/                # 状态管理
│   │   └── utils/                # 工具函数
│   ├── package.json
│   └── README.md                  # 前端文档
│
├── docs/                          # 项目文档
│   └── development-plan.md        # 开发计划
├── .gitignore
└── README.md                      # 项目总览
```

## 🎯 核心功能

### 已完成功能 ✅

**后端服务**:
1. **认证授权**（iot-auth）
   - JWT Token认证
   - 用户登录/登出
   - 权限管理
   - 多租户支持

2. **设备管理**（iot-device）
   - 产品管理（设备模板）
   - 设备CRUD
   - 设备激活/注册
   - 设备状态监控
   - MQTT通信
   - 时序数据存储（InfluxDB）

3. **公共模块**（iot-common）
   - 统一响应格式
   - 统一异常处理
   - JWT工具类
   - Redis缓存工具

### 待开发功能 ⏳

**后端服务**:
- 规则引擎服务（场景联动、自动化）
- API网关（路由、鉴权、限流）
- 告警服务（规则配置、告警推送）
- 数据分析服务（统计、报表）
- 三方集成服务（钉钉、微信、邮件）

**前端项目**:
- 项目初始化（Vue3 + TypeScript + Vite）
- 登录页面
- 设备管理界面
- 数据可视化大屏
- 规则配置界面
- 系统管理

## 🚀 快速开始

### 环境要求
- **后端**: JDK 17+, Maven 3.8+
- **前端**: Node.js 18+, npm 9+
- **基础设施**: Docker & Docker Compose

### 启动后端

```bash
# 1. 进入后端目录
cd iot-platform-backend

# 2. 启动基础设施（MySQL, Redis, EMQX等）
docker-compose up -d

# 3. 编译项目
mvn clean install -DskipTests

# 4. 启动服务
cd iot-auth && mvn spring-boot:run
cd iot-device && mvn spring-boot:run
```

详细说明请查看：[后端README](iot-platform-backend/README.md)

### 启动前端

```bash
# 1. 进入前端目录
cd iot-platform-frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev
```

详细说明请查看：[前端README](iot-platform-frontend/README.md) (待创建)

## 🔌 服务端口

### 后端服务
| 服务 | 端口 | 描述 | 状态 |
|------|------|------|------|
| iot-auth | 8081 | 认证服务 | ✅ |
| iot-device | 8082 | 设备服务 | ✅ |
| iot-gateway | 8080 | API网关 | ⏳ |
| iot-rule-engine | 8083 | 规则引擎 | ⏳ |

### 基础设施
| 服务 | 端口 | 描述 |
|------|------|------|
| MySQL | 3306 | 关系数据库 |
| Redis | 6379 | 缓存 |
| EMQX | 1883/18083 | MQTT Broker |
| InfluxDB | 8086 | 时序数据库 |
| Nacos | 8848 | 注册配置中心 |

### 前端应用
| 应用 | 端口 | 描述 | 状态 |
|------|------|------|------|
| Frontend Dev | 5173 | 开发服务器 | ⏳ |
| Frontend Prod | 80 | 生产环境 | ⏳ |

## 📊 技术栈

### 后端技术
- **框架**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **微服务**: Spring Cloud Alibaba, Nacos
- **数据库**: MySQL 8.0, InfluxDB 2.7
- **缓存**: Redis 7.0
- **消息**: RabbitMQ 3.12, MQTT (EMQX 5.3.2)
- **ORM**: MyBatis-Plus 3.5.5
- **安全**: Spring Security, JWT

### 前端技术
- **框架**: Vue 3.3+ (Composition API)
- **语言**: TypeScript 5.0+
- **构建**: Vite 5.0
- **UI框架**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP**: Axios
- **图表**: ECharts 5

## 📖 文档

- [开发计划](docs/development-plan.md)
- [后端文档](iot-platform-backend/README.md)
- [前端文档](iot-platform-frontend/README.md) (待创建)
- [API接口文档](http://localhost:8080/swagger-ui.html) (需启动服务)

## 🔐 默认账号

### 系统管理员
```
用户名: admin
密码: admin123
```

### EMQX Dashboard
```
URL: http://localhost:18083
用户名: admin
密码: public
```

### Nacos控制台
```
URL: http://localhost:8848/nacos
用户名: nacos
密码: nacos
```

## 📈 开发进度

### 后端 (60%)
- ✅ 基础架构（100%）
- ✅ 公共模块（100%）
- ✅ 认证服务（100%）
- ✅ 设备服务（100%）
- ⏳ 规则引擎（0%）
- ⏳ API网关（0%）
- ⏳ 告警服务（0%）

### 前端 (0%)
- ⏳ 项目初始化（0%）
- ⏳ 登录模块（0%）
- ⏳ 设备管理（0%）
- ⏳ 数据可视化（0%）
- ⏳ 规则配置（0%）

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📝 代码规范

### 后端
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 接口添加Swagger注解

### 前端
- 遵循Vue3官方风格指南
- 使用TypeScript类型定义
- 组件命名采用PascalCase
- 使用ESLint + Prettier格式化

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

## 📄 许可证

MIT License

## 👥 联系方式

- 项目地址: [GitHub](https://github.com/your-repo/iot-platform)
- 问题反馈: [Issues](https://github.com/your-repo/iot-platform/issues)
- 文档中心: [Wiki](https://github.com/your-repo/iot-platform/wiki)

---

**IoT Platform** v1.0.0 - 企业级智能物联网平台