# Paper Learning Assistant 📚

一个基于 AI 的论文学习与解析平台，帮助学生、科研工作者和终身学习者循序渐进地理解学术论文。

## 功能特性

- 🔍 **论文查询**: 支持 arXiv、Crossref 等公开数据库搜索
- 📄 **论文解析**: 自动提取摘要、方法、实验、结论等关键信息
- 🎯 **学习路径**: 循序渐进的引导式学习，配有动图和图示
- 📊 **深度分析**: 公式提取、数据集识别、创新性评分
- 💾 **本地存储**: MySQL 数据库持久化

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17 + Spring Boot 3.x |
| 数据库 | MySQL 8.0+ |
| 前端 | React 18 + TypeScript |
| PDF 解析 | Apache PDFBox / Grobid |
| 部署 | Docker + Docker Compose |

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Docker & Docker Compose (可选)

### Docker 部署 (推荐)

```bash
# 克隆仓库
git clone https://github.com/feihu1991/paper-learning-assistant.git
cd paper-learning-assistant

# 启动服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

访问 http://localhost:3000

### 本地开发

#### 后端

```bash
cd backend
./mvnw spring-boot:run
```

#### 前端

```bash
cd frontend
npm install
npm run dev
```

## 项目结构

```
paper-learning-assistant/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/papertutor/
│   │       ├── config/      # 配置类
│   │       ├── controller/  # REST API
│   │       ├── service/     # 业务逻辑
│   │       ├── repository/  # 数据访问
│   │       ├── entity/      # JPA 实体
│   │       ├── dto/         # 数据传输对象
│   │       └── parser/      # PDF 解析
│   └── pom.xml
├── frontend/                # React 前端
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   └── services/
│   └── package.json
├── docs/                    # 文档
│   ├── API.md
│   └── DEPLOY.md
├── media/                   # 媒体文件
├── docker-compose.yml
└── README.md
```

## 核心 API

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/v1/papers/search` | GET | 搜索论文 |
| `/api/v1/papers/{id}` | GET | 获取论文详情 |
| `/api/v1/papers/{id}/analyze` | POST | 执行论文分析 |
| `/api/v1/papers/{id}/learning-path` | GET | 获取学习路径 |
| `/api/v1/users/{id}/progress` | POST | 提交学习进度 |

详细 API 文档见 [docs/API.md](docs/API.md)

## 数据库设计

主要数据表：

- `users` - 用户信息
- `papers` - 论文元数据
- `paper_analyses` - 论文分析结果
- `learning_steps` - 学习步骤
- `user_paper_progress` - 用户学习进度

详细 ER 图见 [docs/DATABASE.md](docs/DATABASE.md)

## 开发计划

| 阶段 | 内容 | 预计时间 |
|------|------|----------|
| Phase 1 | 项目搭建 + 基础框架 | Week 1-2 |
| Phase 2 | 论文查询模块 | Week 3-4 |
| Phase 3 | 论文解析模块 | Week 5-6 |
| Phase 4 | 学习路径模块 | Week 7-8 |
| Phase 5 | 前端开发 | Week 9 |
| Phase 6 | 测试与部署 | Week 10 |

## 测试论文

使用经典论文进行测试：

- **Attention Is All You Need** (arXiv:1706.03762)
- BERT: Pre-training of Deep Bidirectional Transformers (arXiv:1810.04805)

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License

## 联系方式

- 项目主页：https://github.com/feihu1991/paper-learning-assistant
- 问题反馈：https://github.com/feihu1991/paper-learning-assistant/issues
