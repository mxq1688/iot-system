# IoT智能物联网平台 - 前后端分离开发计划

## 📑 目录

- [1. 项目概述](#1-项目概述)
- [2. 技术架构](#2-技术架构)
- [3. 项目结构](#3-项目结构)
- [4. 开发阶段](#4-开发阶段)
- [5. 接口设计规范](#5-接口设计规范)
- [6. 部署方案](#6-部署方案)

---

## 1. 项目概述

### 1.1 项目定位

基于前后端完全分离架构的企业级智能物联网管理平台，支持多租户SaaS模式，集成主流智能家居平台。

### 1.2 核心功能模块

| 模块 | 功能描述 | 优先级 |
|------|---------|--------|
| 用户管理 | 登录、注册、权限管理、租户管理 | P0 |
| 设备管理 | 设备接入、监控、控制、分组、产品管理 | P0 |
| 规则引擎 | 场景联动、自动化规则、条件触发 | P1 |
| 告警系统 | 设备告警、规则告警、通知管理 | P1 |
| 数据分析 | 设备数据统计、趋势分析、报表 | P2 |
| 三方集成 | 天猫精灵、小度音箱、Home Assistant | P2 |
| 系统管理 | 系统配置、日志管理、监控面板 | P2 |

---

## 2. 技术架构

### 2.1 后端技术栈

```yaml
项目结构: iot-platform-backend/

核心框架:
  - Spring Boot: 3.2.0
  - Spring Cloud: 2023.0.0
  - Spring Cloud Alibaba: 2022.0.0.0
  - Spring Security: 3.2.0

微服务组件:
  - 注册中心: Nacos 2.3.0
  - 配置中心: Nacos Config
  - 网关: Spring Cloud Gateway
  - 负载均衡: Spring Cloud LoadBalancer

数据存储:
  - 关系数据库: MySQL 8.0
  - 时序数据库: InfluxDB 2.7
  - 日志数据库: MongoDB 6.0
  - 缓存: Redis 7.0
  - 分布式锁: Redisson 3.25.0

消息中间件:
  - 业务消息: RabbitMQ 3.12
  - 物联网协议: EMQX 5.3.2 (MQTT Broker)

ORM框架:
  - MyBatis-Plus: 3.5.5
  - 连接池: Druid 1.2.20

安全认证:
  - JWT: 0.12.3
  - BCrypt密码加密

文档工具:
  - SpringDoc OpenAPI: 2.3.0 (Swagger)

工具类:
  - Hutool: 5.8.24
  - Guava: 33.0.0-jre
  - FastJson2: 2.0.43
```

### 2.2 前端技术栈

```yaml
项目结构: iot-platform-frontend/

核心框架:
  - Vue: 3.4.x
  - TypeScript: 5.x
  - Vite: 5.x

UI框架:
  - Element Plus: 2.5.x
  - TailwindCSS: 3.4.x

状态管理:
  - Pinia: 2.1.x

路由:
  - Vue Router: 4.x

HTTP客户端:
  - Axios: 1.6.x

图表可视化:
  - ECharts: 5.4.x
  - Apache ECharts Vue: 6.x

实时通信:
  - Socket.io-client: 4.x (WebSocket)
  - MQTT.js: 5.x (设备通信)

工具库:
  - dayjs: 1.11.x (日期处理)
  - lodash-es: 4.17.x (工具函数)
  - nprogress: 0.2.x (进度条)

代码规范:
  - ESLint: 8.x
  - Prettier: 3.x
  - Stylelint: 16.x

构建工具:
  - Vite Plugin Vue: 5.x
  - Vite Plugin Compression: 压缩
  - Rollup Plugin Visualizer: 打包分析
```

### 2.3 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                          前端应用层                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │  管理后台     │  │  移动端H5    │  │  第三方集成   │         │
│  │  Vue3+TS     │  │  Vue3+Vant  │  │  开放API     │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTPS/WSS
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                      API Gateway (网关层)                        │
│  - 路由转发  - 鉴权  - 限流  - 熔断  - 日志                      │
│  Spring Cloud Gateway                                           │
└────────────────────────┬────────────────────────────────────────┘
                         │
         ┌───────────────┼───────────────┬───────────────┐
         ↓               ↓               ↓               ↓
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  iot-auth    │ │ iot-device   │ │iot-rule-eng  │ │ iot-third-   │
│  认证服务     │ │  设备服务     │ │  规则引擎     │ │  party       │
│  :8081       │ │  :8082       │ │  :8083       │ │  三方集成     │
│              │ │              │ │              │ │  :8084       │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
         │               │               │               │
         └───────────────┼───────────────┴───────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                         服务治理层                                │
│  Nacos (注册中心 + 配置中心)                                      │
└─────────────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                        基础设施层                                 │
│  MySQL  │  Redis  │  RabbitMQ  │  EMQX  │  InfluxDB  │  MinIO  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. 项目结构

### 3.1 后端项目结构

```
iot-platform-backend/
├── iot-common/                    # 公共模块
│   ├── iot-common-core/          # 核心工具类 ✅
│   ├── iot-common-security/      # 安全组件 ✅
│   ├── iot-common-redis/         # Redis组件 ✅
│   ├── iot-common-mq/            # MQ组件 (待开发)
│   └── iot-common-log/           # 日志组件 (待开发)
├── iot-gateway/                   # API网关 (待开发)
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/iot/gateway/
│   │   │       ├── filter/       # 过滤器
│   │   │       ├── config/       # 配置类
│   │   │       └── GatewayApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── pom.xml
├── iot-auth/                      # 认证授权服务 ✅
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/iot/auth/
│   │   │       ├── controller/   # 控制器
│   │   │       ├── service/      # 业务层
│   │   │       ├── mapper/       # 数据访问层
│   │   │       ├── domain/       # 实体类
│   │   │       ├── dto/          # 数据传输对象
│   │   │       └── config/       # 配置类
│   │   └── resources/
│   │       ├── mapper/           # MyBatis XML
│   │       └── application.yml
│   └── pom.xml
├── iot-device/                    # 设备管理服务 (待开发)
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/iot/device/
│   │   │       ├── controller/
│   │   │       │   ├── DeviceController.java
│   │   │       │   ├── ProductController.java
│   │   │       │   └── GroupController.java
│   │   │       ├── service/
│   │   │       │   ├── DeviceService.java
│   │   │       │   ├── ProductService.java
│   │   │       │   └── MqttService.java
│   │   │       ├── mapper/
│   │   │       ├── domain/
│   │   │       └── dto/
│   │   └── resources/
│   └── pom.xml
├── iot-rule-engine/               # 规则引擎服务 (待开发)
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/iot/rule/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── engine/       # 规则引擎核心
│   │   │       └── executor/     # 动作执行器
│   │   └── resources/
│   └── pom.xml
├── iot-third-party/               # 三方平台集成 (待开发)
│   ├── src/main/
│   │   ├── java/
│   │   │   └── com/iot/third/
│   │   │       ├── tmall/        # 天猫精灵
│   │   │       ├── baidu/        # 小度音箱
│   │   │       └── homeassistant/# Home Assistant
│   │   └── resources/
│   └── pom.xml
├── iot-alarm/                     # 告警服务 (待开发)
├── scripts/
│   └── sql/
│       ├── schema.sql            # 数据库结构 ✅
│       └── data.sql              # 初始数据 ✅
├── docker-compose.yml             # Docker编排 ✅
├── pom.xml                        # 父POM ✅
└── README.md                      # 项目文档 ✅
```

### 3.2 前端项目结构

```
iot-platform-frontend/
├── src/
│   ├── api/                      # API接口
│   │   ├── auth.ts              # 认证接口
│   │   ├── device.ts            # 设备接口
│   │   ├── rule.ts              # 规则接口
│   │   └── index.ts             # 统一导出
│   ├── assets/                   # 静态资源
│   │   ├── images/
│   │   ├── icons/
│   │   └── styles/
│   │       ├── index.scss       # 全局样式
│   │       └── variables.scss   # 样式变量
│   ├── components/               # 公共组件
│   │   ├── Layout/              # 布局组件
│   │   │   ├── Sidebar.vue
│   │   │   ├── Navbar.vue
│   │   │   └── AppMain.vue
│   │   ├── Charts/              # 图表组件
│   │   │   ├── LineChart.vue
│   │   │   └── PieChart.vue
│   │   └── Device/              # 设备组件
│   │       ├── DeviceCard.vue
│   │       └── DeviceControl.vue
│   ├── views/                    # 页面视图
│   │   ├── login/               # 登录页
│   │   │   └── index.vue
│   │   ├── dashboard/           # 仪表盘
│   │   │   └── index.vue
│   │   ├── device/              # 设备管理
│   │   │   ├── list.vue        # 设备列表
│   │   │   ├── detail.vue      # 设备详情
│   │   │   └── product.vue     # 产品管理
│   │   ├── rule/                # 规则引擎
│   │   │   ├── scene.vue       # 场景列表
│   │   │   └── create.vue      # 创建场景
│   │   ├── alarm/               # 告警中心
│   │   │   └── index.vue
│   │   ├── data/                # 数据分析
│   │   │   └── statistics.vue
│   │   └── system/              # 系统管理
│   │       ├── user.vue
│   │       └── tenant.vue
│   ├── router/                   # 路由配置
│   │   ├── index.ts
│   │   └── modules/
│   │       ├── device.ts
│   │       └── rule.ts
│   ├── stores/                   # Pinia状态管理
│   │   ├── auth.ts              # 认证状态
│   │   ├── device.ts            # 设备状态
│   │   └── app.ts               # 应用状态
│   ├── utils/                    # 工具函数
│   │   ├── request.ts           # Axios封装
│   │   ├── auth.ts              # Token处理
│   │   ├── date.ts              # 日期处理
│   │   └── mqtt.ts              # MQTT工具
│   ├── types/                    # TypeScript类型定义
│   │   ├── api.d.ts
│   │   ├── device.d.ts
│   │   └── common.d.ts
│   ├── App.vue                   # 根组件
│   └── main.ts                   # 入口文件
├── public/
│   ├── favicon.ico
│   └── index.html
├── .env.development              # 开发环境变量
├── .env.production               # 生产环境变量
├── vite.config.ts                # Vite配置
├── tsconfig.json                 # TypeScript配置
├── package.json
└── README.md
```

---

## 4. 开发阶段

### 阶段一：基础架构搭建 ✅ (已完成)

**后端**:
- [x] Maven父项目配置
- [x] 公共模块开发（core, security, redis）
- [x] 认证服务开发
- [x] Docker Compose基础设施
- [x] 数据库设计和初始化

**前端**:
- [ ] Vue3 + Vite项目初始化
- [ ] 项目结构搭建
- [ ] 基础配置（路由、状态管理、HTTP）
- [ ] UI框架集成（Element Plus）

**预计时间**: 3天

---

### 阶段二：用户认证模块 (当前阶段)

**后端**: ✅ 已完成
- [x] 用户登录/登出API
- [x] JWT Token生成和验证
- [x] 权限控制

**前端**: 待开发
- [ ] 登录页面UI
- [ ] Token存储和管理
- [ ] 路由鉴权
- [ ] Axios拦截器配置
- [ ] 用户信息全局状态

**接口清单**:
- POST `/auth/login` - 用户登录
- POST `/auth/logout` - 用户登出
- GET `/auth/validate` - Token验证
- POST `/auth/refresh` - Token刷新

**预计时间**: 2天

---

### 阶段三：设备管理模块

**后端**: 待开发
- [ ] 设备CRUD接口
- [ ] 产品管理接口
- [ ] 设备分组接口
- [ ] 设备上下线状态监听
- [ ] MQTT消息处理
- [ ] 设备数据上报和存储

**前端**: 待开发
- [ ] 设备列表页面
- [ ] 设备详情页面
- [ ] 设备控制面板
- [ ] 产品管理页面
- [ ] 设备分组管理
- [ ] 实时状态更新（WebSocket）

**接口清单**:
- GET `/device/list` - 设备列表
- POST `/device/create` - 创建设备
- PUT `/device/update` - 更新设备
- DELETE `/device/delete` - 删除设备
- GET `/device/detail/{id}` - 设备详情
- POST `/device/control` - 设备控制
- GET `/product/list` - 产品列表
- POST `/device/group` - 分组管理

**预计时间**: 5天

---

### 阶段四：规则引擎模块

**后端**: 待开发
- [ ] 场景规则CRUD
- [ ] 规则执行引擎
- [ ] 条件判断逻辑
- [ ] 动作执行器
- [ ] 规则调度器

**前端**: 待开发
- [ ] 场景列表页面
- [ ] 场景创建/编辑器
- [ ] 条件配置界面
- [ ] 动作配置界面
- [ ] 规则日志查看

**接口清单**:
- GET `/rule/scene/list` - 场景列表
- POST `/rule/scene/create` - 创建场景
- PUT `/rule/scene/update` - 更新场景
- DELETE `/rule/scene/delete` - 删除场景
- POST `/rule/scene/execute` - 手动执行
- GET `/rule/log` - 执行日志

**预计时间**: 5天

---

### 阶段五：数据分析模块

**后端**: 待开发
- [ ] 设备数据统计接口
- [ ] 趋势分析接口
- [ ] 报表生成接口
- [ ] 数据导出接口

**前端**: 待开发
- [ ] 仪表盘页面
- [ ] 数据图表（折线图、饼图、柱状图）
- [ ] 时间范围选择器
- [ ] 数据导出功能

**预计时间**: 3天

---

### 阶段六：告警系统模块

**后端**: 待开发
- [ ] 告警规则配置
- [ ] 告警触发逻辑
- [ ] 通知发送（邮件、短信、微信）
- [ ] 告警记录管理

**前端**: 待开发
- [ ] 告警中心页面
- [ ] 告警规则配置
- [ ] 告警历史查询
- [ ] 实时告警推送

**预计时间**: 3天

---

### 阶段七：三方平台集成

**后端**: 待开发
- [ ] 天猫精灵OAuth认证
- [ ] 天猫精灵设备同步
- [ ] 小度音箱集成
- [ ] Home Assistant集成

**前端**: 待开发
- [ ] 三方平台配置页面
- [ ] 设备同步界面
- [ ] 授权绑定流程

**预计时间**: 4天

---

### 阶段八：API网关开发

**后端**: 待开发
- [ ] 路由配置
- [ ] 鉴权过滤器
- [ ] 限流配置
- [ ] 熔断降级
- [ ] 日志记录

**预计时间**: 2天

---

### 阶段九：系统管理模块

**后端**: 待开发
- [ ] 租户管理
- [ ] 用户管理
- [ ] 角色权限管理
- [ ] 系统日志
- [ ] 操作审计

**前端**: 待开发
- [ ] 租户管理页面
- [ ] 用户管理页面
- [ ] 角色权限配置
- [ ] 日志查询页面

**预计时间**: 3天

---

### 阶段十：测试和优化

- [ ] 单元测试
- [ ] 集成测试
- [ ] 性能测试
- [ ] 安全测试
- [ ] UI/UX优化
- [ ] 代码优化

**预计时间**: 5天

---

## 5. 接口设计规范

### 5.1 RESTful API规范

**URL设计**:
```
GET    /api/v1/devices          # 获取设备列表
GET    /api/v1/devices/{id}     # 获取设备详情
POST   /api/v1/devices          # 创建设备
PUT    /api/v1/devices/{id}     # 更新设备
DELETE /api/v1/devices/{id}     # 删除设备
```

**统一响应格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1699999999999
}
```

**状态码规范**:
- 200: 成功
- 400: 参数错误
- 401: 未认证
- 403: 无权限
- 404: 资源不存在
- 500: 服务器错误

### 5.2 分页查询规范

**请求参数**:
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "",
  "sortField": "createdAt",
  "sortOrder": "desc"
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "list": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 5.3 认证规范

**请求头**:
```
Authorization: Bearer <token>
```

**Token刷新**:
- Token过期时间: 7天
- 刷新机制: 前端检测到401自动调用刷新接口

---

## 6. 部署方案

### 6.1 开发环境

**后端**:
```bash
# 启动基础设施
docker-compose up -d

# 启动各个微服务
cd iot-auth && mvn spring-boot:run
cd iot-device && mvn spring-boot:run
cd iot-rule-engine && mvn spring-boot:run
```

**前端**:
```bash
npm install
npm run dev
```

### 6.2 生产环境

**后端部署**:
```yaml
# 使用Docker Compose或K8s部署
- 网关: 1个实例
- 认证服务: 2个实例
- 设备服务: 3个实例（高负载）
- 规则引擎: 2个实例
- 三方集成: 1个实例
```

**前端部署**:
```bash
# 构建
npm run build

# Nginx配置
server {
    listen 80;
    server_name iot.example.com;
    
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://gateway:8080;
    }
}
```

### 6.3 监控方案

- **应用监控**: Spring Boot Admin
- **链路追踪**: SkyWalking
- **日志收集**: ELK Stack
- **指标监控**: Prometheus + Grafana

---

## 7. 开发规范

### 7.1 代码规范

**后端**:
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 接口文档Swagger注解

**前端**:
- 使用ESLint + Prettier
- 组件命名采用PascalCase
- 变量命名采用camelCase
- 使用TypeScript类型定义

### 7.2 Git提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

### 7.3 分支管理

- `main`: 主分支（生产环境）
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复

---

## 8. 总结

### 当前进度

✅ **已完成**:
- 后端基础架构（公共模块、认证服务）
- 数据库设计
- Docker环境
- 项目文档

🔄 **进行中**:
- 前端项目初始化
- 认证模块前端开发

📅 **待开发**:
- 设备管理服务
- 规则引擎服务
- 其他业务模块

### 预计总工期

**约35-40个工作日**（单人开发）

### 下一步行动

1. 创建前端Vue3项目
2. 完成登录页面开发
3. 对接后端认证接口
4. 开发设备管理服务后端
5. 开发设备管理页面前端

---

**文档更新时间**: 2024-11-10  
**文档版本**: v1.0