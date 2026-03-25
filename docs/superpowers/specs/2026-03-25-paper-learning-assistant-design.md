# Paper Learning Assistant 设计文档

**创建日期**: 2026-03-25  
**版本**: 1.0  
**状态**: 已批准  

---

## 1. 项目概述

Paper Learning Assistant 是一个 Android 原生应用，帮助用户循序渐进地理解学术论文。

### 1.1 核心目标

- 论文搜索与导入（arXiv API + 本地 PDF）
- 智能解析（本地 + 大模型）
- 结构化学习路径（模板式 + 分层学习）
- 完全本地存储（无后端）

### 1.2 目标用户

- 学生（本科/研究生）
- 科研工作者
- 毕业多年想学习论文的工作人员

### 1.3 测试设备

- 红米 K70（骁龙 8 Gen 2, 12GB RAM）

---

## 2. 技术架构

### 2.1 技术栈

| 层级 | 技术选型 |
|------|----------|
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM + Clean Architecture |
| 数据库 | Room (SQLite) |
| 网络 | Retrofit + OkHttp |
| PDF 解析 | MuPDF |
| 依赖注入 | Hilt |
| 后台任务 | WorkManager |
| 图片加载 | Coil |

### 2.2 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      UI Layer (Compose)                      │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐ │
│  │  Home     │  │  Search   │  │  Paper    │  │ Learning  │ │
│  │  Screen   │  │  Screen   │  │  Detail   │  │  Path     │ │
│  └───────────┘  └───────────┘  └───────────┘  └───────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    ViewModel Layer                           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Domain Layer (UseCases)                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Data Layer                                │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐ │
│  │Repository │  │  Room DB  │  │  LLM API  │  │  File     │ │
│  │           │  │  (SQLite) │  │  Client   │  │  Storage  │ │
│  └───────────┘  └───────────┘  └───────────┘  └───────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. 核心功能设计

### 3.1 数据存储设计

#### 3.1.1 数据库表结构

**papers 表**
```kotlin
@Entity(tableName = "papers")
data class PaperEntity(
    @PrimaryKey val id: Long = 0,
    val arxivId: String?,
    val title: String,
    val authors: String,  // JSON
    val abstract: String,
    val pdfPath: String,
    val parsedStatus: ParseStatus,
    val createdAt: Long
)
```

**learning_steps 表**
```kotlin
@Entity(tableName = "learning_steps")
data class LearningStepEntity(
    @PrimaryKey val id: Long = 0,
    val paperId: Long,
    val stepOrder: Int,
    val stepType: StepType,
    val title: String,
    val content: String,
    val mediaPath: String?,
    val estimatedMinutes: Int
)
```

**user_progress 表**
```kotlin
@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Long = 0,
    val paperId: Long,
    val currentStep: Int,
    val completedSteps: String,  // JSON
    val learningMode: LearningMode,
    val startedAt: Long,
    val completedAt: Long?
)
```

**llm_configs 表**
```kotlin
@Entity(tableName = "llm_configs")
data class LlmConfigEntity(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val apiEndpoint: String,
    val apiKey: String,
    val model: String,
    val isActive: Boolean,
    val isPreset: Boolean
)
```

### 3.2 大模型集成

#### 3.2.1 预置模型配置

```kotlin
object PresetLlmConfigs {
    val configs = listOf(
        LlmPreset("OpenAI GPT-4", "https://api.openai.com/v1", "gpt-4-turbo"),
        LlmPreset("Claude 3.5", "https://api.anthropic.com", "claude-3-5-sonnet-20241022"),
        LlmPreset("通义千问", "https://dashscope.aliyuncs.com", "qwen-max"),
        LlmPreset("文心一言", "https://aip.baidubce.com", "ernie-4.0"),
        LlmPreset("Kimi", "https://api.moonshot.cn", "moonshot-v1-8k"),
        LlmPreset("智谱 AI", "https://open.bigmodel.cn", "glm-4")
    )
}
```

#### 3.2.2 统一接口

```kotlin
interface LlmService {
    suspend fun parsePaper(pdfContent: ByteArray, mode: ParseMode): ParseResult
    suspend fun generateLearningPath(paper: PaperEntity, mode: LearningMode): LearningPath
}
```

### 3.3 学习路径设计

#### 3.3.1 学习模式

```kotlin
enum class LearningMode(val displayName: String, val estimatedMinutes: Int) {
    FAST("快速了解", 5),
    STANDARD("标准学习", 20),
    DEEP("深入掌握", 60)
}
```

#### 3.3.2 步骤类型

```kotlin
enum class StepType(val displayName: String) {
    BACKGROUND("研究背景"),
    PROBLEM("问题定义"),
    CORE_CONCEPT("核心概念"),
    METHOD("方法详解"),
    FORMULA("关键公式"),
    EXPERIMENT("实验分析"),
    CONCLUSION("总结"),
    QUIZ("小测验")
}
```

#### 3.3.3 模板配置

```kotlin
// FAST 模式：3 步
[PROBLEM, CORE_CONCEPT, CONCLUSION]

// STANDARD 模式：6 步
[BACKGROUND, PROBLEM, CORE_CONCEPT, METHOD, EXPERIMENT, CONCLUSION]

// DEEP 模式：8 步
[BACKGROUND, PROBLEM, CORE_CONCEPT, METHOD, FORMULA, EXPERIMENT, CONCLUSION, QUIZ]
```

### 3.4 PDF 解析流程

```
用户导入 PDF
     │
     ▼
┌─────────────────┐
│ 提取基础信息     │ (本地：MuPDF + 规则)
│ 标题、作者、摘要 │
└─────────────────┘
     │
     ▼
┌─────────────────┐
│ 调用大模型 API   │ (异步，WorkManager)
│ - 结构化解析    │
│ - 生成学习路径  │
└─────────────────┘
     │
     ▼
┌─────────────────┐
│ 存储到 Room      │
│ 更新 UI         │
└─────────────────┘
```

---

## 4. 界面设计

### 4.1 主要界面

| 界面 | 功能 |
|------|------|
| **首页** | 最近学习、继续学习、快速导入 |
| **搜索页** | arXiv 搜索、筛选、收藏 |
| **论文详情** | 元数据、结构化摘要、下载学习 |
| **学习路径** | 步骤导航、进度条、测验 |
| **设置** | 大模型配置、存储管理、清理缓存 |

### 4.2 学习界面布局

```
┌─────────────────────────────────────┐
│  ←  Attention Is All You Need       │
├─────────────────────────────────────┤
│  步骤 3/8  ·  核心概念              │
│  ━━━━━━━━━━━━━━━━━━━━━━━ 37%        │
│  ┌─────────────────────────────┐   │
│  │      [动图/图示区域]         │   │
│  └─────────────────────────────┘   │
│  自注意力机制 (Self-Attention)      │
│  自注意力机制允许模型...            │
│  ┌─────────────────────────────┐   │
│  │  上一步    │    下一步       │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

---

## 5. 项目结构

```
app/
├── src/main/java/com/paperlearning/assistant/
│   ├── PaperLearningApp.kt
│   ├── MainActivity.kt
│   ├── ui/                          # UI 层
│   ├── viewmodel/                   # ViewModel 层
│   ├── domain/                      # 领域层
│   ├── data/                        # 数据层
│   └── util/                        # 工具类
├── src/main/res/
└── build.gradle.kts
```

---

## 6. 依赖配置

核心依赖：
- `androidx.compose.material3:material3:1.2.0`
- `androidx.room:room-ktx:2.6.1`
- `com.squareup.retrofit2:retrofit:2.9.0`
- `com.artifex:mupdf:1.23.0`
- `com.google.dagger:hilt-android:2.48.1`
- `androidx.work:work-runtime-ktx:2.9.0`

---

## 7. 测试策略

- 单元测试：ViewModel、UseCase
- 集成测试：Repository、Database
- UI 测试：Compose Testing
- 真机测试：红米 K70

---

## 8. 风险与应对

| 风险 | 影响 | 应对 |
|------|------|------|
| MuPDF 许可证 | 中 | 评估开源替代方案 |
| 大模型 API 成本 | 中 | 支持用户自备 Key |
| 手机存储限制 | 低 | 提供清理缓存功能 |
| 网络不稳定 | 中 | 离线模式 + 重试机制 |

---

## 9. 里程碑

| 阶段 | 内容 | 预计时间 |
|------|------|----------|
| Phase 1 | 项目搭建 + 数据库 | Week 1 |
| Phase 2 | 论文搜索 + 导入 | Week 2 |
| Phase 3 | 大模型集成 | Week 3 |
| Phase 4 | 学习路径 | Week 4 |
| Phase 5 | 测试 + 优化 | Week 5 |

---

**设计批准**: ✅ 已批准 (2026-03-25)  
**下一步**: 调用 `writing-plans` skill 创建实现计划
