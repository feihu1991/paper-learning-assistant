#!/usr/bin/env node
/**
 * 部署项目到GitHub的脚本
 * 创建完整的项目结构，包括前后端和部署配置
 */

const fs = require('fs');
const path = require('path');

// 配置
const SHARED_DIR = path.join(__dirname, '..');
const PROJECT_NAME = 'paper-learning-assistant';
const PROJECT_DIR = path.join(SHARED_DIR, 'github_deploy', PROJECT_NAME);

// 确保项目目录存在
if (!fs.existsSync(PROJECT_DIR)) {
  fs.mkdirSync(PROJECT_DIR, { recursive: true });
}

console.log(`开始创建GitHub项目: ${PROJECT_NAME}`);

// 1. 创建项目结构
const projectStructure = [
  '.github/workflows/',
  '.github/workflows/deploy.yml',
  'backend/',
  'frontend/',
  'docs/',
  'docker/',
  'docker/docker-compose.yml',
  'docker/Dockerfile.backend',
  'docker/Dockerfile.frontend',
  'scripts/',
  '.gitignore',
  'README.md',
  'package.json',
  'LICENSE',
  '.env.example',
  'docker-compose.yml',
  'Makefile'
];

// 创建目录结构
projectStructure.forEach(item => {
  const itemPath = path.join(PROJECT_DIR, item);
  if (item.endsWith('/')) {
    if (!fs.existsSync(itemPath)) {
      fs.mkdirSync(itemPath, { recursive: true });
    }
  }
});

console.log('项目结构创建完成');

// 2. 复制后端代码
const backendSource = path.join(SHARED_DIR, 'output', 'be', 'paper_learning_website');
const backendTarget = path.join(PROJECT_DIR, 'backend');

if (fs.existsSync(backendSource)) {
  copyDirectory(backendSource, backendTarget);
  console.log('后端代码复制完成');
}

// 3. 复制前端代码
const frontendSource = path.join(SHARED_DIR, 'output', 'fe', 'paper_learning_website');
const frontendTarget = path.join(PROJECT_DIR, 'frontend');

if (fs.existsSync(frontendSource)) {
  copyDirectory(frontendSource, frontendTarget);
  console.log('前端代码复制完成');
}

// 4. 创建根目录 package.json
const rootPackageJson = {
  name: PROJECT_NAME,
  version: "1.0.0",
  description: "论文学习助手网站 - 完整的全栈应用",
  "private": true,
  "workspaces": [
    "backend",
    "frontend"
  ],
  "scripts": {
    "dev": "concurrently \"npm run dev:backend\" \"npm run dev:frontend\"",
    "dev:backend": "cd backend && npm run dev",
    "dev:frontend": "cd frontend && npm run dev",
    "build": "npm run build:backend && npm run build:frontend",
    "build:backend": "cd backend && npm run build",
    "build:frontend": "cd frontend && npm run build",
    "start": "npm run start:backend",
    "start:backend": "cd backend && npm start",
    "test": "npm run test:backend && npm run test:frontend",
    "test:backend": "cd backend && npm test",
    "test:frontend": "cd frontend && npm test",
    "docker:build": "docker-compose build",
    "docker:up": "docker-compose up",
    "docker:down": "docker-compose down"
  },
  "devDependencies": {
    "concurrently": "^8.2.1"
  },
  "engines": {
    "node": ">=18.0.0",
    "npm": ">=9.0.0"
  },
  "keywords": [
    "paper-learning",
    "academic",
    "ai",
    "education",
    "research"
  ],
  "author": "Paper Learning Assistant Team",
  "license": "MIT"
};

fs.writeFileSync(
  path.join(PROJECT_DIR, 'package.json'),
  JSON.stringify(rootPackageJson, null, 2)
);
console.log('根目录 package.json 创建完成');

// 5. 创建 README.md
const readme = `# 论文学习助手网站

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
   \`\`\`bash
   git clone https://github.com/yourusername/${PROJECT_NAME}.git
   cd ${PROJECT_NAME}
   \`\`\`

2. **安装依赖**
   \`\`\`bash
   npm install
   \`\`\`

3. **配置环境变量**
   \`\`\`bash
   cp .env.example .env
   # 编辑 .env 文件，设置数据库连接和API密钥
   \`\`\`

4. **启动开发服务器**
   \`\`\`bash
   npm run dev
   \`\`\`

5. **访问应用**
   - 前端: http://localhost:5173
   - 后端API: http://localhost:3000
   - API文档: http://localhost:3000/api-docs

### Docker部署

1. **构建并启动容器**
   \`\`\`bash
   docker-compose up -d
   \`\`\`

2. **访问应用**
   - 前端: http://localhost:80
   - 后端API: http://localhost:3000

## 📦 项目结构

\`\`\`
${PROJECT_NAME}/
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
\`\`\`

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

\`\`\`bash
# 运行所有测试
npm test

# 运行后端测试
npm run test:backend

# 运行前端测试
npm run test:frontend
\`\`\`

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
\`\`\`bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d

# 停止服务
docker-compose down
\`\`\`

## 📄 API文档

### 论文搜索
\`\`\`http
POST /api/papers/search
Content-Type: application/json

{
  "query": "large language models",
  "source": "arxiv",
  "page": 1,
  "limit": 10
}
\`\`\`

### 内容生成
\`\`\`http
POST /api/generate/summary
Content-Type: application/json

{
  "paper_id": "arxiv:2301.12345",
  "length": "medium"
}
\`\`\`

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支 (\`git checkout -b feature/amazing-feature\`)
3. 提交更改 (\`git commit -m 'Add amazing feature'\`)
4. 推送到分支 (\`git push origin feature/amazing-feature\`)
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
- GitHub Issues: [项目Issues](https://github.com/yourusername/${PROJECT_NAME}/issues)
- 邮箱: team@paperlearning.com

---

*由Paper Learning Assistant Team开发*`;

fs.writeFileSync(path.join(PROJECT_DIR, 'README.md'), readme);
console.log('README.md 创建完成');

// 6. 创建 .gitignore
const gitignore = `# Dependencies
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*
.pnpm-debug.log*

# Environment variables
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Build outputs
dist/
build/
out/
.next/
.nuxt/

# Coverage
coverage/
.nyc_output/

# Logs
logs/
*.log

# Runtime data
pids/
*.pid
*.seed
*.pid.lock

# IDE
.vscode/
.idea/
*.swp
*.swo
*~

# OS
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Docker
.docker/

# Database
*.db
*.sqlite
*.sqlite3

# Testing
jest.config.js
cypress/

# Misc
*.tgz
*.tar.gz
*.zip
*.rar

# Project specific
backend/.env
frontend/.env
docker-compose.override.yml`;

fs.writeFileSync(path.join(PROJECT_DIR, '.gitignore'), gitignore);
console.log('.gitignore 创建完成');

// 7. 创建 LICENSE
const license = `MIT License

Copyright (c) 2026 Paper Learning Assistant Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.`;

fs.writeFileSync(path.join(PROJECT_DIR, 'LICENSE'), license);
console.log('LICENSE 创建完成');

// 8. 创建 .env.example
const envExample = `# Backend Configuration
NODE_ENV=development
PORT=3000

# Database
DATABASE_URL=postgresql://username:password@localhost:5432/paper_learning_db
REDIS_URL=redis://localhost:6379

# API Keys
OPENAI_API_KEY=your_openai_api_key_here
ARXIV_API_KEY=your_arxiv_api_key_here

# JWT
JWT_SECRET=your_jwt_secret_key_here
JWT_EXPIRES_IN=7d

# Frontend
VITE_API_URL=http://localhost:3000/api
VITE_APP_NAME=Paper Learning Assistant

# Email (optional)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your_email@gmail.com
SMTP_PASS=your_email_password

# Analytics (optional)
GOOGLE_ANALYTICS_ID=UA-XXXXXXXXX-X`;

fs.writeFileSync(path.join(PROJECT_DIR, '.env.example'), envExample);
console.log('.env.example 创建完成');

// 9. 创建 docker-compose.yml
const dockerCompose = `version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: paper_learning_db
      POSTGRES_USER: paper_user
      POSTGRES_PASSWORD: paper_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U paper_user"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: ../docker/Dockerfile.backend
    environment:
      NODE_ENV: production
      DATABASE_URL: postgresql://paper_user:paper_password@postgres:5432/paper_learning_db
      REDIS_URL: redis://redis:6379
      PORT: 3000
    ports:
      - "3000:3000"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    volumes:
      - ./backend:/app
      - /app/node_modules

  frontend:
    build:
      context: ./frontend
      dockerfile: ../docker/Dockerfile.frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    environment:
      VITE_API_URL: http://localhost:3000/api

  nginx:
    image: nginx:alpine
    ports:
      - "8080:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - frontend
      - backend

volumes:
  postgres_data:
  redis_data:`;

fs.writeFileSync(path.join(PROJECT_DIR, 'docker-compose.yml'), dockerCompose);
console.log('docker-compose.yml 创建完成');

// 10. 创建 Dockerfile
const dockerfileBackend = `FROM node:18-alpine AS builder

WORKDIR /app

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 生产阶段
FROM node:18-alpine

WORKDIR /app

# 复制生产依赖
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/dist ./dist

# 创建非root用户
RUN addgroup -g 1001 -S nodejs
RUN adduser -S nodejs -u 1001

# 更改文件所有权
RUN chown -R nodejs:nodejs /app

USER nodejs

# 暴露端口
EXPOSE 3000

# 启动应用
CMD ["node", "dist/index.js"]`;

fs.writeFileSync(path.join(PROJECT_DIR, 'docker', 'Dockerfile.backend'), dockerfileBackend);
console.log('Dockerfile.backend 创建完成');

const dockerfileFrontend = `FROM node:18-alpine AS builder

WORKDIR /app

# 复制 package.json 和 package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm ci

# 复制源代码
COPY . .

# 构建应用
RUN npm run build

# 生产阶段 - 使用nginx服务静态文件
FROM nginx:alpine

# 复制构建产物到nginx目录
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制nginx配置
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]`;

fs.writeFileSync(path.join(PROJECT_DIR, 'docker', 'Dockerfile.frontend'), dockerfileFrontend);
console.log('Dockerfile.frontend 创建完成');

// 11. 创建 GitHub Actions 工作流
const githubWorkflow = `name: Deploy to GitHub Pages

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]
  workflow_dispatch:

permissions:
  contents: read
  pages: write
  id-token: write

concurrency:
  group: "pages"
  cancel-in-progress: false

jobs:
  build-and-deploy:
    environment:
      name: github-pages
      url: \${{ steps.deployment.outputs.page_url }}
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: 'frontend/package-lock.json'

      - name: Install Dependencies
        working-directory: ./frontend
        run: npm ci

      - name: Build
        working-directory: ./frontend
        run: npm run build
        env:
          VITE_API_URL: \${{ secrets.VITE_API_URL }}

      - name: Setup Pages
        uses: actions/configure-pages@v4

      - name: Upload artifact
        uses: actions/upload-pages-artifact@v3
        with:
          path: './frontend/dist'

      - name: Deploy to GitHub Pages
        id: deployment
        uses: actions/deploy-pages@v4

  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: 'backend/package-lock.json'

      - name: Install Dependencies
        working-directory: ./backend
        run: npm ci

      - name: Run Tests
        working-directory: ./backend
        run: npm test

  docker-build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Build and push backend
        uses: docker/build-push-action@v5
        with:
          context: ./backend
          file: ./docker/Dockerfile.backend
          push: false
          tags: paper-learning-backend:latest

      - name: Build and push frontend
        uses: docker/build-push-action@v5
        with:
          context: ./frontend
          file: ./docker/Dockerfile.frontend
          push: false
          tags: paper-learning-frontend:latest`;

fs.writeFileSync(path.join(PROJECT_DIR, '.github', 'workflows', 'deploy.yml'), githubWorkflow);
console.log('GitHub Actions 工作流创建完成');

// 12. 创建 Makefile
const makefile = `.PHONY: help install dev build test docker-up docker-down docker-build deploy

help: ## 显示帮助信息
	@echo "可用命令:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \\033[36m%-20s\\033[0m %s\\n", $$1, $$2}'

install: ## 安装所有依赖
	@echo "安装根目录依赖..."
	npm install
	@echo "安装后端依赖..."
	cd backend && npm install
	@echo "安装前端依赖..."
	cd frontend && npm install

dev: ## 启动开发服务器
	@echo "启动开发服务器..."
	npm run dev

build: ## 构建生产版本
	@echo "构建后端..."
	cd backend && npm run build
	@echo "构建前端..."
	cd frontend && npm run build

test: ## 运行所有测试
	@echo "运行后端测试..."
	cd backend && npm test
	@echo "运行前端测试..."
	cd frontend && npm test

docker-up: ## 使用Docker启动服务
	@echo "启动Docker服务..."
	docker-compose up -d

docker-down: ## 停止Docker服务
	@echo "停止Docker服务..."
	docker-compose down

docker-build: ## 构建Docker镜像
	@echo "构建Docker镜像..."
	docker-compose build

deploy: build ## 部署到生产环境
	@echo "部署到生产环境..."
	# 这里可以添加实际的部署命令
	@echo "部署完成!"

clean: ## 清理构建文件和依赖
	@echo "清理构建文件..."
	rm -rf backend/dist
	rm -rf frontend/dist
	rm -rf node_modules
	rm -rf backend/node_modules
	rm -rf frontend/node_modules

setup: install ## 完整设置项目
	@echo "复制环境变量文件..."
	cp .env.example .env
	@echo "请编辑 .env 文件并设置必要的环境变量"
	@echo "设置完成!"

db-migrate: ## 运行数据库迁移
	@echo "运行数据库迁移..."
	cd backend && npm run migrate

db-seed: ## 运行数据库种子
	@echo "运行数据库种子..."
	cd backend && npm run seed

lint: ## 运行代码检查
	@echo "检查后端代码..."
	cd backend && npm run lint
	@echo "检查前端代码..."
	cd frontend && npm run lint

format: ## 格式化代码
	@echo "格式化后端代码..."
	cd backend && npm run format
	@echo "格式化前端代码..."
	cd frontend && npm run format

logs: ## 查看Docker日志
	@echo "查看Docker容器日志..."
	docker-compose logs -f

status: ## 查看服务状态
	@echo "查看Docker容器状态..."
	docker-compose ps

restart: docker-down docker-up ## 重启服务
	@echo "服务已重启"

update: ## 更新依赖
	@echo "更新后端依赖..."
	cd backend && npm update
	@echo "更新前端依赖..."
	cd frontend && npm update
	@echo "更新根目录依赖..."
	npm update

version: ## 显示版本信息
	@echo "项目版本: \$$(node -p "require('./package.json').version")"
	@echo "Node.js版本: \$$(node --version)"
	@echo "npm版本: \$$(npm --version)"
	@echo "Docker版本: \$$(docker --version)"
	@echo "Docker Compose版本: \$$(docker-compose --version)"`;

fs.writeFileSync(path.join(PROJECT_DIR, 'Makefile'), makefile);
console.log('Makefile 创建完成');

// 13. 创建文档目录
const docsIndex = `# 论文学习助手网站 - 文档

## 📚 文档目录

### 用户文档
- [快速开始](./getting-started.md)
- [用户指南](./user-guide.md)
- [API参考](./api-reference.md)
- [常见问题](./faq.md)

### 开发者文档
- [开发环境设置](./development-setup.md)
- [项目架构](./architecture.md)
- [代码规范](./coding-standards.md)
- [测试指南](./testing-guide.md)
- [部署指南](./deployment-guide.md)

### 运维文档
- [系统要求](./system-requirements.md)
- [监控告警](./monitoring.md)
- [故障排除](./troubleshooting.md)
- [备份恢复](./backup-recovery.md)

### API文档
- [REST API](./rest-api.md)
- [GraphQL API](./graphql-api.md) (可选)
- [WebSocket API](./websocket-api.md) (可选)

## 🔗 相关链接

- [GitHub仓库](https://github.com/yourusername/paper-learning-assistant)
- [在线演示](https://yourusername.github.io/paper-learning-assistant)
- [API文档](https://api.paperlearning.com/docs)
- [问题反馈](https://github.com/yourusername/paper-learning-assistant/issues)

## 📞 支持

如有问题，请通过以下方式联系我们：
- GitHub Issues: [提交问题](https://github.com/yourusername/paper-learning-assistant/issues)
- 邮箱: support@paperlearning.com
- Discord: [加入社区](https://discord.gg/paper-learning)

---

*最后更新: ${new Date().toISOString()}*`;

fs.writeFileSync(path.join(PROJECT_DIR, 'docs', 'index.md'), docsIndex);
console.log('文档目录创建完成');

// 14. 创建快速开始文档
const gettingStarted = `# 快速开始

本文档将指导你快速设置和运行论文学习助手网站。

## 🚀 5分钟快速启动

### 使用Docker (推荐)

如果你已经安装了Docker和Docker Compose，这是最简单的方式：

\`\`\`bash
# 克隆项目
git clone https://github.com/yourusername/paper-learning-assistant.git
cd paper-learning-assistant

# 启动所有服务
make docker-up

# 访问应用
# 前端: http://localhost:80
# 后端API: http://localhost:3000
\`\`\`

### 手动安装

如果你更喜欢手动安装：

\`\`\`bash
# 1. 克隆项目
git clone https://github.com/yourusername/paper-learning-assistant.git
cd paper-learning-assistant

# 2. 安装依赖
make install

# 3. 配置环境变量
cp .env.example .env
# 编辑 .env 文件，设置必要的配置

# 4. 启动开发服务器
make dev

# 5. 访问应用
# 前端: http://localhost:5173
# 后端API: http://localhost:3000
\`\`\`

## 📋 系统要求

### 最低要求
- **操作系统**: Windows 10 / macOS 10.15+ / Ubuntu 20.04+
- **内存**: 4GB RAM
- **存储**: 2GB可用空间
- **网络**: 稳定的互联网连接

### 开发环境
- **Node.js**: 18.x 或更高版本
- **npm**: 9.x 或更高版本
- **Git**: 2.x 或更高版本
- **Docker**: 20.x 或更高版本 (可选)
- **Docker Compose**: 2.x 或更高版本 (可选)

### 生产环境
- **服务器**: 2核CPU, 4GB RAM, 20GB存储
- **数据库**: PostgreSQL 14+ 或兼容数据库
- **缓存**: Redis 6+ 或兼容缓存服务
- **网络**: 公网IP, SSL证书

## 🔧 环境配置

### 必需的环境变量

\`\`\`bash
# 后端配置
NODE_ENV=development
PORT=3000

# 数据库配置
DATABASE_URL=postgresql://username:password@localhost:5432/paper_learning_db
REDIS_URL=redis://localhost:6379

# API密钥
OPENAI_API_KEY=your_openai_api_key_here
ARXIV_API_KEY=your_arxiv_api_key_here

# 前端配置
VITE_API_URL=http://localhost:3000/api
\`\`\`

### 获取API密钥

1. **OpenAI API密钥**
   - 访问 [OpenAI平台](https://platform.openai.com/api-keys)
   - 创建新的API密钥
   - 设置到 \`OPENAI_API_KEY\` 环境变量

2. **arXiv API密钥** (可选)
   - arXiv API不需要密钥，但建议注册获取更高限制
   - 访问 [arXiv API文档](https://arxiv.org/help/api)

## 🎯 验证安装

安装完成后，验证所有组件是否正常工作：

\`\`\`bash
# 检查后端健康状态
curl http://localhost:3000/health

# 预期响应
{"status":"healthy","timestamp":"2026-03-06T01:10:00.000Z"}

# 检查前端是否可访问
# 在浏览器中访问 http://localhost:5173
\`\`\`

## 🐛 常见问题

### 1. 端口被占用
如果端口3000或5173被占用，可以修改环境变量：
\`\`\`bash
# 在 .env 文件中修改
PORT=3001  # 修改后端端口
# 同时修改前端配置中的API地址
VITE_API_URL=http://localhost:3001/api
\`\`\`

### 2. 数据库连接失败
确保PostgreSQL服务正在运行：
\`\`\`bash
# 检查PostgreSQL状态
sudo systemctl status postgresql

# 启动PostgreSQL
sudo systemctl start postgresql
\`\`\`

### 3. 依赖安装失败
尝试清理缓存后重新安装：
\`\`\`bash
# 清理npm缓存
npm cache clean --force

# 重新安装依赖
make clean
make install
\`\`\`

## 📖 下一步

安装完成后，你可以：

1. **查看用户指南** - 了解如何使用应用
2. **阅读开发文档** - 了解如何贡献代码
3. **查看API文档** - 了解如何集成API
4. **尝试部署** - 将应用部署到生产环境

---

*如有问题，请查看[常见问题](./faq.md)或[提交问题](https://github.com/yourusername/paper-learning-assistant/issues)*`;

fs.writeFileSync(path.join(PROJECT_DIR, 'docs', 'getting-started.md'), gettingStarted);
console.log('快速开始文档创建完成');

// 15. 创建Git初始化脚本
const gitInitScript = `#!/usr/bin/env node
/**
 * Git初始化脚本
 * 初始化Git仓库并创建初始提交
 */

const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const PROJECT_DIR = path.join(__dirname, '..');
const GITHUB_USERNAME = 'yourusername'; // 请替换为你的GitHub用户名
const REPO_NAME = 'paper-learning-assistant';

console.log('开始初始化Git仓库...');

try {
  // 进入项目目录
  process.chdir(PROJECT_DIR);
  
  // 初始化Git仓库
  console.log('1. 初始化Git仓库...');
  execSync('git init', { stdio: 'inherit' });
  
  // 添加所有文件
  console.log('2. 添加文件到暂存区...');
  execSync('git add .', { stdio: 'inherit' });
  
  // 创建初始提交
  console.log('3. 创建初始提交...');
  execSync('git commit -m "Initial commit: Paper Learning Assistant website"', { stdio: 'inherit' });
  
  // 创建GitHub仓库URL
  const githubRepoUrl = \`https://github.com/\${GITHUB_USERNAME}/\${REPO_NAME}.git\`;
  
  console.log('\\n✅ Git仓库初始化完成！');
  console.log('\\n📋 下一步操作：');
  console.log('\\n1. 在GitHub上创建新仓库:');
  console.log(\`   访问 https://github.com/new\`);
  console.log(\`   仓库名称: \${REPO_NAME}\`);
  console.log(\`   描述: Paper Learning Assistant website\`);
  console.log(\`   选择: Public (公开)\`);
  console.log(\`   不要初始化README、.gitignore或LICENSE\`);
  
  console.log('\\n2. 添加远程仓库并推送代码:');
  console.log(\`   git remote add origin \${githubRepoUrl}\`);
  console.log(\`   git branch -M main\`);
  console.log(\`   git push -u origin main\`);
  
  console.log('\\n3. 设置GitHub Pages (可选):');
  console.log(\`   访问 https://github.com/\${GITHUB_USERNAME}/\${REPO_NAME}/settings/pages\`);
  console.log(\`   分支: gh-pages 或 main\`);
  console.log(\`   文件夹: /frontend/dist\`);
  
  console.log('\\n4. 配置GitHub Secrets (用于CI/CD):');
  console.log(\`   访问 https://github.com/\${GITHUB_USERNAME}/\${REPO_NAME}/settings/secrets/actions\`);
  console.log(\`   添加以下secrets:\`);
  console.log(\`   - VITE_API_URL: 生产环境API地址\`);
  console.log(\`   - OPENAI_API_KEY: OpenAI API密钥\`);
  
  console.log('\\n🎉 完成！你的项目现在可以部署到GitHub了。');
  
} catch (error) {
  console.error('初始化Git仓库时出错:', error.message);
  process.exit(1);
}`;

fs.writeFileSync(path.join(PROJECT_DIR, 'scripts', 'git-init.js'), gitInitScript);
console.log('Git初始化脚本创建完成');

// 辅助函数：复制目录
function copyDirectory(source, target) {
  if (!fs.existsSync(target)) {
    fs.mkdirSync(target, { recursive: true });
  }
  
  const items = fs.readdirSync(source);
  
  for (const item of items) {
    const sourcePath = path.join(source, item);
    const targetPath = path.join(target, item);
    
    const stat = fs.statSync(sourcePath);
    
    if (stat.isDirectory()) {
      copyDirectory(sourcePath, targetPath);
    } else {
      fs.copyFileSync(sourcePath, targetPath);
    }
  }
}

console.log('\\n✅ GitHub部署项目创建完成！');
console.log('\\n📁 项目位置:', PROJECT_DIR);
console.log('\\n🚀 下一步操作:');
console.log('1. 进入项目目录:', PROJECT_DIR);
console.log('2. 运行Git初始化: node scripts/git-init.js');
console.log('3. 按照提示在GitHub上创建仓库');
console.log('4. 推送代码到GitHub');
console.log('5. 配置GitHub Pages部署');
console.log('\\n🎉 你的项目将自动部署到GitHub Pages！');