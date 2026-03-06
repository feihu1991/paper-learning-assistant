# 论文学习网站后端 (Paper Learning Backend)

基于 Spring Boot + MySQL 的论文学习网站后端服务。

## 技术栈

- **Spring Boot 3.2.0**
- **MyBatis Plus 3.5.5**
- **MySQL 8.0**
- **Java 17**
- **Lombok**

## 项目结构

```
src/main/java/com/paperlearning/
├── PaperLearningApplication.java    # 启动类
├── entity/                           # 实体类
│   ├── Paper.java                    # 论文实体
│   ├── PaperAnalysis.java            # 论文分析实体
│   └── AnalysisTask.java             # 分析任务实体
├── repository/                       # 数据访问层
│   ├── PaperRepository.java
│   ├── PaperAnalysisRepository.java
│   └── AnalysisTaskRepository.java
├── service/                          # 业务逻辑层
│   ├── PaperService.java
│   └── AnalysisService.java
├── controller/                       # 控制器层
│   ├── PaperController.java
│   └── AnalysisController.java
└── dto/                              # 数据传输对象
    ├── PaperDTO.java
    ├── PaperRequestDTO.java
    ├── AnalysisResultDTO.java
    └── AnalysisRequestDTO.java
```

## 数据库表

### papers - 论文表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR(500) | 论文标题 |
| abstract | TEXT | 论文摘要 |
| authors | VARCHAR(1000) | 作者 |
| pdf_url | VARCHAR(2000) | PDF链接 |
| cover_image | LONGBLOB | 封面图片(BLOB) |
| created_at | TIMESTAMP | 创建时间 |

### paper_analysis - 论文分析表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| paper_id | BIGINT | 论文ID |
| background | TEXT | 背景介绍 |
| coreConcepts | JSON | 核心概念 |
| methodology | JSON | 方法论 |
| keyInnovations | JSON | 关键创新 |
| learningPath | JSON | 学习路径 |
| difficulty | INT | 难度等级(1-5) |
| estimated_time | INT | 预估学习时间(分钟) |
| status | VARCHAR(20) | 状态 |
| created_at | TIMESTAMP | 创建时间 |

### analysis_tasks - 分析任务表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| paper_id | BIGINT | 论文ID |
| status | VARCHAR(20) | pending/processing/completed |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

## API 接口

### 论文管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/papers | 创建论文 |
| GET | /api/papers | 获取所有论文 |
| GET | /api/papers/{id} | 获取论文详情 |
| GET | /api/papers/page | 分页获取论文 |
| PUT | /api/papers/{id} | 更新论文 |
| DELETE | /api/papers/{id} | 删除论文 |

### 分析任务

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/analysis/tasks/{paperId} | 创建分析任务 |
| POST | /api/analysis/tasks/{paperId}/start | 开始分析 |
| POST | /api/analysis/{paperId} | 保存分析结果 |
| GET | /api/analysis/{paperId} | 获取分析结果 |
| GET | /api/analysis/tasks/pending | 获取待分析任务 |
| GET | /api/analysis/tasks/processing | 获取正在分析任务 |
| GET | /api/analysis/tasks/completed | 获取已完成任务 |
| GET | /api/analysis/tasks/grouped | 按状态分组获取任务 |

## 配置

修改 `src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/paper_learning
    username: root
    password: root
```

## 运行

1. 创建数据库：
```bash
mysql -u root -p < src/main/resources/schema.sql
```

2. 编译运行：
```bash
mvn spring-boot:run
```

服务将在 http://localhost:8080 启动。
