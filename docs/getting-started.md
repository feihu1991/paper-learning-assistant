# 快速开始

本文档将指导你快速设置和运行论文学习助手网站。

## 🚀 5分钟快速启动

### 使用Docker (推荐)

如果你已经安装了Docker和Docker Compose，这是最简单的方式：

```bash
# 克隆项目
git clone https://github.com/yourusername/paper-learning-assistant.git
cd paper-learning-assistant

# 启动所有服务
make docker-up

# 访问应用
# 前端: http://localhost:80
# 后端API: http://localhost:3000
```

### 手动安装

如果你更喜欢手动安装：

```bash
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
```

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

```bash
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
```

### 获取API密钥

1. **OpenAI API密钥**
   - 访问 [OpenAI平台](https://platform.openai.com/api-keys)
   - 创建新的API密钥
   - 设置到 `OPENAI_API_KEY` 环境变量

2. **arXiv API密钥** (可选)
   - arXiv API不需要密钥，但建议注册获取更高限制
   - 访问 [arXiv API文档](https://arxiv.org/help/api)

## 🎯 验证安装

安装完成后，验证所有组件是否正常工作：

```bash
# 检查后端健康状态
curl http://localhost:3000/health

# 预期响应
{"status":"healthy","timestamp":"2026-03-06T01:10:00.000Z"}

# 检查前端是否可访问
# 在浏览器中访问 http://localhost:5173
```

## 🐛 常见问题

### 1. 端口被占用
如果端口3000或5173被占用，可以修改环境变量：
```bash
# 在 .env 文件中修改
PORT=3001  # 修改后端端口
# 同时修改前端配置中的API地址
VITE_API_URL=http://localhost:3001/api
```

### 2. 数据库连接失败
确保PostgreSQL服务正在运行：
```bash
# 检查PostgreSQL状态
sudo systemctl status postgresql

# 启动PostgreSQL
sudo systemctl start postgresql
```

### 3. 依赖安装失败
尝试清理缓存后重新安装：
```bash
# 清理npm缓存
npm cache clean --force

# 重新安装依赖
make clean
make install
```

## 📖 下一步

安装完成后，你可以：

1. **查看用户指南** - 了解如何使用应用
2. **阅读开发文档** - 了解如何贡献代码
3. **查看API文档** - 了解如何集成API
4. **尝试部署** - 将应用部署到生产环境

---

*如有问题，请查看[常见问题](./faq.md)或[提交问题](https://github.com/yourusername/paper-learning-assistant/issues)*