# 论文学习助手网站

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Node.js](https://img.shields.io/badge/node-%3E%3D18.0.0-green.svg)
![React](https://img.shields.io/badge/react-18.2.0-blue.svg)

## 📚 项目简介

论文学习助手网站是一个帮助用户理解和学习学术论文的智能平台。通过输入论文名称，系统会自动生成学习内容、解释和可视化图表，帮助用户快速掌握论文核心内容。

## ✨ 功能特性

- 🔍 **智能论文搜索** - 支持arXiv、PubMed、IEEE等多源搜索
- 🤖 **AI内容生成** - 自动生成摘要、解释和学习路径
- 📊 **可视化图表** - 概念图、流程图、时间线等可视化工具
- 👤 **用户系统** - 收藏、历史记录、学习进度跟踪
- 📱 **响应式设计** - 支持桌面端和移动端
- 🌙 **夜间模式** - 舒适的用户体验

## 🏗️ 技术架构

### 后端技术栈
- **运行时**: Node.js 18+
- **框架**: Express.js
- **数据库**: PostgreSQL + Redis
- **认证**: JWT + OAuth 2.0
- **API风格**: RESTful
- **部署**: Docker + Kubernetes

### 前端技术栈
- **框架**: React 18 + TypeScript
- **构建工具**: Vite
- **状态管理**: Redux Toolkit
- **样式**: Tailwind CSS
- **图表**: Chart.js + Mermaid.js
- **路由**: React Router

## 🚀 快速开始

### 环境要求
- Node.js 18+
- PostgreSQL 14+
- Redis 6+
- Git

### 本地开发

1. **克隆项目**
   ```bash
   git clone https://github.com/yourusername/paper-learning-assistant.git
   cd paper-learning-assistant
   ```

2. **安装依赖**
   ```bash
   npm install
   ```

3. **配置环境变量**
   ```bash
   cp .env.example .env
   # 编辑 .env 文件，设置数据库连接和API密钥
   ```

4. **启动开发服务器**
   ```bash
   npm run dev
   ```

5. **访问应用**
   - 前端: http://localhost:5173
   - 后端API: http://localhost:3000
   - API文档: http://localhost:3000/api-docs

### Docker部署

1. **构建并启动容器**
   ```bash
   docker-compose up -d
   ```

2. **访问应用**
   - 前端: http://localhost:80
   - 后端API: http://localhost:3000

## 📦 项目结构

```
paper-learning-assistant/
├── backend/                    # 后端代码
│   ├── src/                   # 源代码
│   │   ├── config/           # 配置文件
│   │   ├── controllers/      # 控制器
│   │   ├── routes/          # 路由定义
│   │   ├── services/        # 业务逻辑
│   │   ├── models/          # 数据模型
│   │   └── middleware/      # 中间件
│   ├── tests/               # 测试文件
│   ├── package.json         # 后端依赖
│   └── README.md            # 后端文档
├── frontend/                 # 前端代码
│   ├── src/                 # 源代码
│   │   ├── components/      # 可复用组件
│   │   ├── pages/          # 页面组件
│   │   ├── services/       # API服务
│   │   ├── store/          # 状态管理
│   │   └── utils/          # 工具函数
│   ├── public/              # 静态资源
│   ├── package.json         # 前端依赖
│   └── README.md            # 前端文档
├── docker/                  # Docker配置
├── docs/                   # 项目文档
├── .github/workflows/      # GitHub Actions
├── docker-compose.yml      # Docker Compose配置
├── package.json            # 根项目配置
└── README.md               # 项目说明
```

## 🔧 开发指南

### 添加新功能
1. 在对应目录创建新组件/服务
2. 编写测试用例
3. 更新文档
4. 提交Pull Request

### API开发
- 后端API遵循RESTful设计原则
- 使用Swagger/OpenAPI进行文档化
- 所有API都需要单元测试

### 前端开发
- 使用TypeScript确保类型安全
- 遵循组件化设计原则
- 使用Redux管理全局状态

## 🧪 测试

```bash
# 运行所有测试
npm test

# 运行后端测试
npm run test:backend

# 运行前端测试
npm run test:frontend
```

## 📈 部署

### GitHub Pages (前端)
项目已配置GitHub Actions，自动部署前端到GitHub Pages。

### Vercel/Netlify (前端)
- 连接GitHub仓库
- 自动部署每次提交
- 支持预览部署

### Railway/Render (后端)
- 支持Node.js应用部署
- 自动配置数据库
- 持续部署

### Docker部署
```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 停止服务
docker-compose down
```

## 📄 API文档

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

### 内容生成
```http
POST /api/generate/summary
Content-Type: application/json

{
  "paper_id": "arxiv:2301.12345",
  "length": "medium"
}
```

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull Request

## 📝 许可证

本项目基于MIT许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- OpenAI GPT API
- arXiv API
- React社区
- Node.js社区

## 📞 联系方式

如有问题或建议，请通过以下方式联系我们：
- GitHub Issues: [项目Issues](https://github.com/yourusername/paper-learning-assistant/issues)
- 邮箱: team@paperlearning.com

---

*由Paper Learning Assistant Team开发*