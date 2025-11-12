# IoT智能物联网平台 - 前端项目

基于 React 18 + TypeScript + Vite + Ant Design 构建的现代化物联网管理平台前端应用。

## 技术栈

- **框架**: React 18.3
- **构建工具**: Vite 6.0
- **语言**: TypeScript 5.6
- **UI组件库**: Ant Design 5.x
- **路由**: React Router 7.1
- **状态管理**: Zustand 5.0
- **HTTP客户端**: Axios 1.7
- **日期处理**: Day.js 1.11

## 项目结构

```
iot-platform-frontend/
├── src/
│   ├── api/                # API接口定义
│   │   ├── auth.ts        # 认证API
│   │   ├── product.ts     # 产品API
│   │   └── device.ts      # 设备API
│   ├── components/        # 公共组件
│   │   └── layout/        # 布局组件
│   │       ├── MainLayout.tsx
│   │       └── MainLayout.css
│   ├── views/             # 页面组件
│   │   ├── login/         # 登录页面
│   │   ├── dashboard/     # 仪表盘
│   │   └── device/        # 设备管理
│   ├── router/            # 路由配置
│   ├── store/             # 状态管理
│   │   └── userStore.ts   # 用户状态
│   ├── utils/             # 工具函数
│   │   └── request.ts     # HTTP请求封装
│   ├── types/             # TypeScript类型定义
│   │   └── index.ts
│   ├── App.tsx            # 应用入口
│   └── main.tsx           # 主入口
├── .env.development       # 开发环境配置
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 功能特性

### 已实现功能
- ✅ 用户登录认证
- ✅ 路由权限控制
- ✅ 统一的HTTP请求封装
- ✅ 用户状态管理（Zustand）
- ✅ 响应式主布局（侧边栏+顶部导航）
- ✅ 仪表盘数据展示
- ✅ 产品管理页面框架
- ✅ 设备管理页面框架

### 待开发功能
- ⏳ 产品CRUD完整功能
- ⏳ 设备CRUD完整功能
- ⏳ 设备数据可视化（ECharts集成）
- ⏳ 规则引擎配置界面
- ⏳ 告警管理界面
- ⏳ 系统设置界面

## 快速开始

### 前置要求

- Node.js >= 18.0.0
- npm >= 9.0.0

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

启动后访问: http://localhost:5173

### 构建生产版本

```bash
npm run build
```

### 预览生产构建

```bash
npm run preview
```

## 环境配置

### 开发环境 (.env.development)

```env
VITE_API_BASE_URL=http://localhost:8080
```

### 生产环境 (.env.production)

```env
VITE_API_BASE_URL=https://api.yourdomain.com
```

## 默认账号

- **用户名**: admin
- **密码**: admin123

## API接口文档

后端API基础路径: `http://localhost:8080`

### 认证接口
- `POST /auth/login` - 用户登录
- `POST /auth/logout` - 用户登出
- `GET /auth/current-user` - 获取当前用户信息

### 产品接口
- `GET /product/list` - 获取产品列表
- `GET /product/detail/:id` - 获取产品详情
- `POST /product/create` - 创建产品
- `PUT /product/update` - 更新产品
- `DELETE /product/delete/:id` - 删除产品

### 设备接口
- `GET /device/list` - 获取设备列表
- `GET /device/detail/:id` - 获取设备详情
- `POST /device/create` - 创建设备
- `PUT /device/update` - 更新设备
- `DELETE /device/delete/:id` - 删除设备
- `POST /device/activate/:id` - 激活设备
- `GET /device/data/latest` - 获取设备最新数据
- `GET /device/data/history` - 获取设备历史数据

## 开发规范

### 代码风格
- 使用 TypeScript 严格模式
- 遵循 ESLint 规则
- 使用函数式组件 + Hooks
- 组件文件使用 PascalCase 命名
- 工具函数使用 camelCase 命名

### Git提交规范
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建/工具链更新

## 常见问题

### Q: 登录后页面空白？
A: 检查后端服务是否启动，确认 `.env.development` 中的 `VITE_API_BASE_URL` 配置正确。

### Q: API请求失败？
A: 检查网络请求控制台，确认后端API地址正确，查看具体错误信息。

### Q: 如何添加新页面？
A: 
1. 在 `src/views/` 下创建新页面组件
2. 在 `src/router/index.tsx` 中添加路由配置
3. 在 `src/components/layout/MainLayout.tsx` 中添加菜单项

## 许可证

MIT License
