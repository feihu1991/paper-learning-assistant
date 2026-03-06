# OpenClaw 安装指南后端服务

## 快速启动

```bash
# 进入后端目录
cd output/be

# 安装依赖
npm install

# 启动服务器
npm start
```

服务器将在 http://localhost:3000 启动。

## 功能

- 静态文件托管（服务于 `../fe` 目录）
- 代码高亮（highlight.js）
- SPA 回退支持

## 配置

- 端口: 3000（可通过环境变量 PORT 修改）
- 静态文件目录: `../fe`
