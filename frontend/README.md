# 论文学习助手网站 - 前端应用

## 概述
这是论文学习助手网站的前端应用，提供用户友好的界面来搜索、学习和理解学术论文。

## 功能特性
- 现代化的响应式设计
- 智能论文搜索和筛选
- 交互式图表和可视化
- 用户系统（收藏、历史、进度）
- 夜间模式和多语言支持

## 技术栈
- React 18 + TypeScript
- Vite 构建工具
- Redux Toolkit 状态管理
- Tailwind CSS 样式
- Chart.js + Mermaid.js 图表
- React Router 路由

## 快速开始

### 环境要求
- Node.js 18+
- npm 或 yarn

### 安装
```bash
npm install
```

### 配置
复制环境变量文件：
```bash
cp .env.example .env
```

编辑 .env 文件，设置API地址和其他配置。

### 运行
```bash
# 开发模式
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 项目结构
```
src/
├── components/     # 可复用组件
│   ├── SearchBar/ # 搜索栏组件
│   ├── PaperCard/ # 论文卡片组件
│   └── ...
├── pages/         # 页面组件
│   ├── Home/      # 首页
│   ├── PaperDetail/ # 论文详情页
│   └── ...
├── services/      # API服务
├── store/         # 状态管理
├── hooks/         # 自定义Hook
├── utils/         # 工具函数
├── types/         # TypeScript类型定义
└── layouts/       # 布局组件
```

## 主要页面

### 首页
- 论文搜索功能
- 热门论文推荐
- 功能特性展示

### 论文详情页
- 论文基本信息展示
- AI生成的内容解释
- 交互式图表
- 学习路径导航

### 用户个人页
- 搜索历史记录
- 收藏的论文
- 学习进度跟踪

## 设计系统
- 使用Tailwind CSS进行样式设计
- 响应式布局支持移动端和桌面端
- 可访问性优化
- 一致的组件设计

## 开发指南

### 添加新组件
1. 在 `src/components/` 创建组件目录
2. 创建组件文件 (`.tsx`) 和样式文件 (`.css`)
3. 导出组件并在需要的地方使用

### API集成
1. 在 `src/services/` 中添加API服务
2. 使用Redux管理API状态
3. 在组件中调用API服务

### 状态管理
- 使用Redux Toolkit管理全局状态
- 每个功能模块有自己的slice
- 使用TypeScript确保类型安全

## 部署
```bash
# 构建生产版本
npm run build

# 部署到静态托管服务
# 例如: Vercel, Netlify, GitHub Pages
```

## 测试
```bash
# 添加测试后运行
npm test
```

## 许可证
MIT

---
*由 Frontend Agent 自动生成*