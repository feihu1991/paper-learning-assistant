# Paper Learning Assistant 📚

一个基于 AI 的论文学习与解析平台，帮助学生、科研工作者和终身学习者循序渐进地理解学术论文。

## 功能特性

- 🔍 **论文查询**: 支持 arXiv、Crossref 等公开数据库搜索
- 📄 **论文解析**: 自动提取摘要、方法、实验、结论等关键信息
- 🎯 **学习路径**: 循序渐进的引导式学习，配有动图和图示
- 📊 **深度分析**: 公式提取、数据集识别、创新性评分
- 💾 **本地存储**: Room 数据库 + 内部存储 (Android 应用)
- 🤖 **大模型集成**: 支持 OpenAI GPT、Claude 等多种 LLM

## 技术栈

### Android 应用

| 层级 | 技术 |
|------|------|
| 语言 | Kotlin 1.9+ |
| UI 框架 | Jetpack Compose + Material 3 |
| 架构 | MVVM + Clean Architecture |
| 依赖注入 | Hilt |
| 本地数据库 | Room |
| 网络请求 | Retrofit + OkHttp |
| 异步处理 | Kotlin Coroutines + Flow |
| PDF 解析 | MuPDF / Apache PDFBox |
| 图片加载 | Coil |
| 后台任务 | WorkManager |

### 后端服务 (可选)

| 层级 | 技术 |
|------|------|
| 后端 | Java 17 + Spring Boot 3.x |
| 数据库 | MySQL 8.0+ |
| 前端 | React 18 + TypeScript |
| 部署 | Docker + Docker Compose |

## 快速开始

### Android 应用

#### 环境要求

- Android Studio Hedgehog / Iguana
- JDK 17+
- Android SDK 34
- Kotlin 1.9.20

#### 构建与运行

```bash
# 克隆仓库
git clone https://github.com/feihu1991/paper-learning-assistant.git
cd paper-learning-assistant/android-app

# 使用 Android Studio 打开项目
# 或命令行构建
./gradlew assembleDebug

# 安装到设备
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

#### 真机测试

推荐测试设备：**红米 K70** (骁龙 8 Gen 2, 12GB RAM)

详细测试指南见 [docs/DEPLOY.md](docs/DEPLOY.md)

### 后端服务 (可选)

#### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Docker & Docker Compose (可选)

#### Docker 部署 (推荐)

```bash
cd backend
docker-compose up -d

# 查看日志
docker-compose logs -f
```

访问 http://localhost:3000

#### 本地开发

```bash
# 后端
cd backend
./mvnw spring-boot:run

# 前端
cd frontend
npm install
npm run dev
```

## 项目结构

### Android 应用

```
paper-tutor/android-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/paperlearning/assistant/
│   │   │   ├── PaperLearningApp.kt    # Application 入口
│   │   │   ├── MainActivity.kt         # 主活动
│   │   │   ├── data/                   # 数据层
│   │   │   │   ├── local/              # Room 数据库
│   │   │   │   │   ├── dao/            # 数据访问对象
│   │   │   │   │   └── PaperDatabase.kt
│   │   │   │   ├── remote/             # 远程 API
│   │   │   │   │   ├── arxiv/          # arXiv API
│   │   │   │   │   └── llm/            # 大模型 API
│   │   │   │   ├── repository/         # 数据仓库
│   │   │   │   └── model/              # 数据模型
│   │   │   ├── domain/                 # 领域层
│   │   │   │   ├── model/              # 领域模型
│   │   │   │   └── usecase/            # 业务用例
│   │   │   ├── ui/                     # UI 层
│   │   │   │   ├── screens/            # 屏幕界面
│   │   │   │   ├── components/         # 可复用组件
│   │   │   │   ├── navigation/         # 导航配置
│   │   │   │   └── theme/              # 主题样式
│   │   │   ├── viewmodel/              # ViewModel
│   │   │   ├── di/                     # 依赖注入
│   │   │   └── util/                   # 工具类
│   │   ├── res/                        # 资源文件
│   │   └── AndroidManifest.xml         # 应用清单
│   ├── src/test/                       # 单元测试
│   ├── src/androidTest/                # 仪器测试
│   └── build.gradle.kts                # 构建配置
├── proguard-rules.pro                  # ProGuard 规则
├── build.gradle.kts                    # 项目构建配置
└── settings.gradle.kts                 # 项目设置
```

### 后端服务

```
paper-tutor/backend/
├── src/main/java/
│   └── com/papertutor/
│       ├── config/      # 配置类
│       ├── controller/  # REST API
│       ├── service/     # 业务逻辑
│       ├── repository/  # 数据访问
│       ├── entity/      # JPA 实体
│       ├── dto/         # 数据传输对象
│       └── parser/      # PDF 解析
└── pom.xml
```

### 文档

```
paper-tutor/docs/
├── API.md                      # API 接口文档
├── DEPLOY.md                   # 部署与测试指南
├── GITHUB_SETUP.md             # GitHub 配置指南
└── superpowers/
    ├── specs/                  # 需求规格
    └── plans/                  # 实现计划
```

## 核心 API

### Android 本地 API (Room)

| 表名 | 描述 | 主要操作 |
|------|------|----------|
| `papers` | 论文元数据 | 增删改查、搜索 |
| `learning_steps` | 学习步骤 | 按论文 ID 查询 |
| `user_progress` | 学习进度 | 更新进度、查询状态 |
| `llm_configs` | LLM 配置 | 管理 API 密钥 |

### 远程 API

| 服务 | 端点 | 描述 |
|------|------|------|
| arXiv | `http://export.arxiv.org/api/query` | 论文搜索 |
| OpenAI | `https://api.openai.com/v1/chat/completions` | GPT 模型调用 |
| Claude | `https://api.anthropic.com/v1/messages` | Claude 模型调用 |

详细 API 文档见 [docs/API.md](docs/API.md)

## 数据库设计

### Room 实体

- `PaperEntity` - 论文元数据 (标题、作者、摘要、PDF 路径)
- `LearningStepEntity` - 学习步骤 (步骤类型、内容、预估时间)
- `UserProgressEntity` - 用户进度 (当前步骤、完成状态、学习模式)
- `LlmConfigEntity` - LLM 配置 (API 端点、密钥、模型名称)

### 枚举类型

- `ParseStatus` - 解析状态 (未解析、解析中、已完成、失败)
- `LearningMode` - 学习模式 (快速了解、标准学习、深入掌握)
- `StepType` - 步骤类型 (背景、问题、概念、方法、公式、实验、总结、测验)

## 开发计划

### Phase 1: 项目搭建 + 数据库 ✅

- [x] Task 1.1: 创建 Android 项目骨架
- [x] Task 1.2: 创建数据模型 (Entity)
- [x] Task 1.3: 创建 Room 数据库和 DAO
- [x] Task 1.4: 创建 Hilt 模块
- [x] Task 1.5: 创建 Repository 层

### Phase 2: 核心功能 + UI ✅

- [x] Task 2.1: 创建 UseCase 层
- [x] Task 2.2: 创建 ViewModel
- [x] Task 2.3: 创建首页 UI
- [x] Task 2.4: 创建搜索页 UI
- [x] Task 2.5: 创建论文详情页 UI

### Phase 3: 大模型集成 ✅

- [x] Task 3.1: 创建 LLM API 客户端
- [x] Task 3.2: 预置模型配置
- [x] Task 3.3: 创建论文解析服务
- [x] Task 3.4: 创建学习路径生成服务

### Phase 4: 学习路径功能 ✅

- [x] Task 4.1: 学习路径 UI
- [x] Task 4.2: 进度追踪逻辑
- [x] Task 4.3: 测验功能

### Phase 5: 测试 + 优化 ✅

- [x] Task 5.1: 集成测试
- [x] Task 5.2: UI 测试
- [x] Task 5.3: 性能优化 (ProGuard 配置)
- [x] Task 5.4: 红米 K70 真机测试准备

## 测试论文

使用经典论文进行测试：

- **Attention Is All You Need** (arXiv:1706.03762)
- **BERT**: Pre-training of Deep Bidirectional Transformers (arXiv:1810.04805)
- **Transformer** 相关论文

## 性能优化

### ProGuard 混淆

已配置 `app/proguard-rules.pro`，优化 APK 大小并保护代码：

- 保留数据模型类 (不混淆)
- 保留 Room、Hilt、Retrofit 相关类
- 移除调试日志 (发布版本)

### 构建优化

```kotlin
// app/build.gradle.kts
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(...)
    }
}
```

详细优化配置见 [docs/DEPLOY.md](docs/DEPLOY.md#性能优化配置)

## 真机测试

**推荐测试设备**: 红米 K70 (Redmi K70)

- 处理器：骁龙 8 Gen 2
- RAM: 12GB LPDDR5X
- 存储：256GB UFS 4.0
- 系统：HyperOS 1.0 (Android 14)

详细测试步骤见 [docs/DEPLOY.md](docs/DEPLOY.md#红米-k70-真机测试)

## 贡献

欢迎提交 Issue 和 Pull Request!

### 开发环境设置

```bash
# 克隆仓库
git clone https://github.com/feihu1991/paper-learning-assistant.git
cd paper-learning-assistant

# Android 开发
cd android-app
./gradlew assembleDebug

# 运行测试
./gradlew test
./gradlew connectedAndroidTest
```

## 许可证

MIT License

## 联系方式

- 项目主页：https://github.com/feihu1991/paper-learning-assistant
- 问题反馈：https://github.com/feihu1991/paper-learning-assistant/issues
- 部署指南：[docs/DEPLOY.md](docs/DEPLOY.md)
