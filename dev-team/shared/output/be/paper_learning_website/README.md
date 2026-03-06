# 论文学习助手网站 - 后端API

## 概述
这是论文学习助手网站的后端API服务，提供论文搜索、内容生成和用户管理功能。

## 功能特性
- 多源论文搜索 (arXiv、PubMed、IEEE等)
- AI内容生成和解释
- 用户系统 (收藏、历史、进度)
- 缓存和性能优化
- RESTful API设计

## 技术栈
- Node.js + Express.js
- PostgreSQL + Redis
- OpenAI GPT API
- JWT认证
- 容器化部署

## 快速开始

### 环境要求
- Node.js 18+
- PostgreSQL 14+
- Redis 6+

### 安装
```bash
npm install
```

### 配置
复制环境变量文件：
```bash
cp .env.example .env
```

编辑 .env 文件，设置数据库连接和API密钥。

### 运行
```bash
# 开发模式
npm run dev

# 生产模式
npm start
```

## API文档

### 论文搜索
```http
POST /api/papers/search
Content-Type: application/json

{
  "query": "large language models",
  "source": "arxiv",
  "page": 1,
  "limit": 10
}
```

### 获取论文详情
```http
GET /api/papers/{id}
```

### 生成内容
```http
POST /api/generate/summary
Content-Type: application/json

{
  "paper_id": "arxiv:2301.12345",
  "length": "medium"
}
```

## 部署
```bash
# 使用Docker
docker build -t paper-learning-backend .
docker run -p 3000:3000 paper-learning-backend

# 使用Docker Compose
docker-compose up -d
```

## 测试
```bash
npm test
```

## 项目结构
```
src/
├── config/          # 配置文件
├── controllers/     # 控制器
├── routes/         # 路由定义
├── services/       # 业务逻辑
├── models/         # 数据模型
├── middleware/     # 中间件
└── utils/          # 工具函数
```

## 许可证
MIT

---
*由 Backend Agent 自动生成*