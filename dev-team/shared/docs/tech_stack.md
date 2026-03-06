# 技术栈文档

## 后端技术栈

### 核心框架
- **Node.js**: v18+ LTS
- **Express.js**: Web 应用框架
- **TypeScript**: 类型安全的 JavaScript

### 数据库
- **PostgreSQL**: 主关系型数据库
- **Redis**: 缓存和会话存储
- **MongoDB**: 文档数据库 (可选)

### ORM/ODM
- **Prisma**: 现代数据库工具包
- **Mongoose**: MongoDB ODM (如使用 MongoDB)

### 认证和授权
- **JWT**: JSON Web Tokens
- **bcrypt**: 密码哈希
- **Passport.js**: 认证中间件

### API 文档
- **Swagger/OpenAPI**: API 文档生成
- **Postman**: API 测试和文档

### 测试框架
- **Jest**: 测试框架
- **Supertest**: HTTP 断言库
- **Faker.js**: 测试数据生成

### 代码质量
- **ESLint**: 代码检查
- **Prettier**: 代码格式化
- **Husky**: Git hooks
- **lint-staged**: 暂存文件检查

## 前端技术栈

### 核心框架
- **React**: 18+
- **Next.js**: 全栈 React 框架
- **TypeScript**: 类型安全

### 状态管理
- **Zustand**: 轻量级状态管理
- **React Query**: 服务器状态管理
- **Redux Toolkit**: 复杂状态管理 (可选)

### UI 组件库
- **Ant Design**: 企业级 UI 组件
- **Tailwind CSS**: 实用优先的 CSS 框架
- **Styled Components**: CSS-in-JS

### 表单处理
- **React Hook Form**: 高性能表单
- **Zod**: 表单验证模式
- **Yup**: 表单验证 (备选)

### 路由
- **Next.js Router**: 文件系统路由
- **React Router**: 客户端路由 (如不使用 Next.js)

### 测试框架
- **Jest**: 单元测试
- **React Testing Library**: React 组件测试
- **Cypress**: 端到端测试
- **Playwright**: 现代浏览器自动化

### 构建工具
- **Vite**: 下一代前端工具
- **Webpack**: 模块打包 (如需要)
- **Babel**: JavaScript 编译器

## DevOps 技术栈

### 容器化
- **Docker**: 容器化平台
- **Docker Compose**: 多容器应用

### 编排和部署
- **Kubernetes**: 容器编排
- **Helm**: Kubernetes 包管理
- **Terraform**: 基础设施即代码

### CI/CD
- **GitHub Actions**: CI/CD 流水线
- **Jenkins**: 自动化服务器 (可选)
- **ArgoCD**: GitOps 持续交付

### 监控和日志
- **Prometheus**: 监控系统
- **Grafana**: 监控仪表板
- **ELK Stack**: 日志管理
- **Sentry**: 错误跟踪

### 云平台
- **AWS**: 亚马逊云服务
- **Azure**: 微软云
- **Google Cloud**: 谷歌云
- **阿里云**: 国内云服务

## QA 技术栈

### 测试框架
- **Jest**: JavaScript 测试
- **Mocha**: Node.js 测试框架
- **Cypress**: 端到端测试
- **Playwright**: 浏览器自动化

### 性能测试
- **k6**: 现代负载测试
- **Artillery**: 分布式负载测试
- **Lighthouse**: Web 性能测试

### 代码质量
- **ESLint**: 代码检查
- **SonarQube**: 代码质量平台
- **CodeClimate**: 代码质量分析

### 安全测试
- **OWASP ZAP**: 安全测试工具
- **Snyk**: 漏洞扫描
- **npm audit**: npm 依赖检查

### 报告工具
- **Allure**: 测试报告框架
- **JUnit**: XML 测试报告
- **HTML Reporter**: 自定义报告

## 数据库技术

### PostgreSQL 扩展
- **PostGIS**: 地理空间数据
- **pgcrypto**: 加密函数
- **TimescaleDB**: 时序数据 (可选)

### Redis 使用场景
- **会话存储**: 用户会话
- **缓存层**: 热点数据缓存
- **消息队列**: 异步任务
- **速率限制**: API 限流

### 数据库迁移
- **Prisma Migrate**: 数据库迁移
- **Flyway**: 数据库版本控制
- **Liquibase**: 数据库重构

## 消息队列和流处理

### 消息队列
- **RabbitMQ**: 消息代理
- **Apache Kafka**: 分布式流平台
- **Redis Streams**: 基于 Redis 的流

### 任务队列
- **Bull**: Redis 任务队列
- **Celery**: 分布式任务队列 (Python)
- **Sidekiq**: Ruby 任务队列

## 搜索和索引

### 搜索引擎
- **Elasticsearch**: 分布式搜索和分析
- **Algolia**: 搜索即服务
- **MeiliSearch**: 轻量级搜索引擎

### 对象存储
- **Amazon S3**: 对象存储服务
- **MinIO**: S3 兼容的对象存储
- **Google Cloud Storage**: 谷歌对象存储

## 开发工具

### 版本控制
- **Git**: 分布式版本控制
- **GitHub**: 代码托管平台
- **GitLab**: DevOps 平台

### IDE 和编辑器
- **VS Code**: 轻量级代码编辑器
- **WebStorm**: JetBrains IDE
- **IntelliJ IDEA**: Java IDE

### 包管理
- **npm**: Node.js 包管理
- **Yarn**: 快速、可靠的依赖管理
- **pnpm**: 高效的包管理

### 文档工具
- **Markdown**: 文档格式
- **Docusaurus**: 文档网站生成
- **GitBook**: 现代文档平台

## 协作工具

### 项目管理
- **Jira**: 敏捷项目管理
- **Trello**: 看板式项目管理
- **Asana**: 团队任务管理

### 文档协作
- **Confluence**: 团队协作空间
- **Notion**: 一体化工作空间
- **Google Docs**: 在线文档

### 通信工具
- **Slack**: 团队沟通
- **Microsoft Teams**: 企业协作
- **Discord**: 社区沟通

## 安全工具

### 身份认证
- **Keycloak**: 开源身份管理
- **Auth0**: 身份验证即服务
- **Okta**: 企业身份管理

### 密钥管理
- **HashiCorp Vault**: 密钥管理
- **AWS Secrets Manager**: AWS 密钥管理
- **Azure Key Vault**: Azure 密钥管理

### 网络安全
- **Cloudflare**: CDN 和安全
- **AWS WAF**: Web 应用防火墙
- **ModSecurity**: 开源 WAF

## 性能优化工具

### 前端性能
- **Webpack Bundle Analyzer**: 包分析
- **Lighthouse CI**: 持续性能监控
- **Core Web Vitals**: 核心网页指标

### 后端性能
- **New Relic**: 应用性能监控
- **Datadog**: 监控和分析平台
- **AppDynamics**: 应用性能管理

### 数据库性能
- **pg_stat_statements**: PostgreSQL 性能监控
- **Redis Insight**: Redis 监控和管理
- **MongoDB Compass**: MongoDB GUI

## 本地开发环境

### 开发环境
- **Docker Desktop**: 容器化开发
- **Minikube**: 本地 Kubernetes
- **Vagrant**: 开发环境自动化

### 数据库工具
- **TablePlus**: 现代数据库工具
- **DBeaver**: 通用数据库工具
- **pgAdmin**: PostgreSQL 管理

### API 测试工具
- **Postman**: API 开发和测试
- **Insomnia**: API 设计平台
- **Bruno**: 开源 API 客户端

## 部署策略

### 部署环境
- **开发环境**: 功能开发测试
- **测试环境**: 集成测试
- **预生产环境**: 生产前验证
- **生产环境**: 线上服务

### 部署方式
- **蓝绿部署**: 零停机部署
- **金丝雀发布**: 渐进式发布
- **滚动更新**: 逐步替换实例

### 监控告警
- **PagerDuty**: 事件响应平台
- **Opsgenie**: 告警和待命管理
- **VictorOps**: 事件管理平台