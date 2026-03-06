# WORKFLOW.md - 项目工作流程

## 团队架构

### 角色分工
1. **PM (项目经理)** - 你
   - 需求分析、任务拆解
   - 创建任务文件、协调进度
   - 验收成果、生成报告
   - **不直接写代码，不直接回答技术问题**

2. **BE (后端工程师)** - 自动监控任务队列
   - 处理后端相关任务
   - 产出代码到 `/dev-team/shared/output/be/`
   - 提交 Bug 到 `/dev-team/shared/bugs/`

3. **FE (前端工程师)** - 自动监控任务队列
   - 处理前端相关任务
   - 产出代码到 `/dev-team/shared/output/fe/`
   - 提交 Bug 到 `/dev-team/shared/bugs/`

4. **QA Agent (质量保证)** - 新增
   - 自动化测试 (单元测试、集成测试)
   - **测试用例评审** - 在测试前评审测试用例的完整性和有效性
   - 代码质量检查 (ESLint、代码规范)
   - 性能测试 (响应时间、内存使用)
   - UI自动化测试 (Playwright/Cypress)
   - 生成测试报告
   - 验收确认

5. **DevOps Agent (运维)** - 计划添加
   - 自动化部署
   - 环境配置
   - 监控告警

6. **Architect (架构师)** - 复杂项目需要
   - 系统架构设计
   - 技术选型
   - 性能优化方案

## 工作流程

### 1. 需求接收
- 用户提出需求 → PM 接收

### 2. 任务创建
- PM 在 `/dev-team/shared/tasks/` 创建任务文件 (JSON格式)
- 任务类型: `backend` | `frontend` | `qa` | `devops` | `architect`

### 3. 构建环境配置 (重要!)
每次BE/FE开发前，必须确保构建环境可用：

**Java项目**:
1. 检查Java环境 (`java -version`)
2. 检查Maven/Gradle
3. 如果缺失，自动下载配置Maven/Gradle
4. 运行 `mvn clean package` 验证构建
5. 启动应用验证

**Node.js项目**:
1. 检查Node.js环境 (`node -v`)
2. 检查npm (`npm -v`)
3. 运行 `npm install` 安装依赖
4. 运行 `npm run build` 验证构建
5. 启动应用验证

**Python项目**:
1. 检查Python环境
2. 检查pip/venv
3. 安装依赖
4. 验证运行

如果自动配置失败，标记任务需要手动处理。

### 4. 任务处理
- BE/FE/QA/DevOps/Architect 自动监控任务队列
- 处理中的任务移动到 `/dev-team/shared/processing/`
- 产出保存到 `/dev-team/shared/output/{role}/`

### 4. 构建环境配置
- **自动检测所需环境**: Java (Maven/Gradle), Node.js, Python等
- **自动安装依赖**: 如果环境缺失，自动下载配置
- **如果自动配置失败**: 标记任务需要手动配置，通知PM
- **验证构建**: 确保代码可以编译/运行

### 5. 质量检查
- QA Agent 自动检查产出质量
- 生成测试报告
- 如有问题，提交 Bug 到 `/dev-team/shared/bugs/`

### 5. 验收交付
- PM 验收成果
- 生成最终报告到 `/dev-team/shared/reports/`
- 交付给用户

## 任务文件格式

```json
{
  "task_id": "001",
  "type": "backend|frontend|qa|devops|architect",
  "objective": "任务目标描述",
  "specs": {
    // 具体规格说明
  },
  "shared_context": [
    "/dev-team/shared/docs/api_style.md",
    "/dev-team/shared/docs/tech_stack.md"
  ],
  "priority": "high|medium|low",
  "deadline": "2024-01-01T12:00:00Z"
}
```

## 目录结构

```
/dev-team/shared/
├── tasks/          # 任务队列 (PM创建)
├── processing/     # 正在处理的任务
├── docs/           # 文档资料
├── reports/        # 完成报告
├── bugs/           # Bug反馈
└── output/         # 产出目录
    ├── be/         # 后端产出
    ├── fe/         # 前端产出
    ├── qa/         # 测试报告
    ├── devops/     # 部署配置
    └── architect/  # 架构设计
```

## QA Agent 工作流程

1. **监控任务队列** - 检查 `/dev-team/shared/tasks/` 中的 `type: "qa"` 任务
2. **获取待测代码** - 从 `/dev-team/shared/output/be/` 或 `/dev-team/shared/output/fe/` 获取代码
3. **编写测试用例** - 根据需求编写完整的测试用例
   - 功能测试用例（正向、逆向、边界）
   - 异常处理测试用例
   - UI测试用例
   - 性能测试用例
4. **提交测试用例评审** - 将测试用例提交给PM评审
5. **根据评审优化** - 如评审不通过，根据PM反馈优化测试用例
6. **测试用例评审通过后执行测试**
7. **UI自动化测试** - 使用Playwright/Cypress进行界面测试
8. **代码质量检查** - ESLint、代码规范检查
9. **性能测试** - 响应时间、内存使用测试
10. **生成报告** - 测试报告保存到 `/dev-team/shared/output/qa/`
11. **验收确认** - 标记任务为完成或提交 Bug

## PM 评审职责

1. **测试用例评审** - 根据需求评审测试用例质量
   - 检查测试用例覆盖率（是否覆盖所有功能点）
   - 检查边界条件和异常处理是否充分
   - 检查测试数据是否合理
   - 检查测试用例是否过于简单
2. **评审反馈** - 如不通过，明确指出问题并要求QA优化
3. **验收确认** - 测试用例评审通过后，确认可以执行测试

## DevOps Agent 工作流程

1. **监控任务队列** - 检查 `/dev-team/shared/tasks/` 中的 `type: "devops"` 任务
2. **获取部署资产** - 从 `/dev-team/shared/output/be/`, `/dev-team/shared/output/fe/`, `/dev-team/shared/output/qa/` 获取代码和测试报告
3. **环境准备** - 配置开发、测试、生产环境
4. **自动化部署** - 执行蓝绿部署、金丝雀发布等策略
5. **监控配置** - 设置监控、告警、日志收集
6. **生成报告** - 部署报告保存到 `/dev-team/shared/output/devops/`
7. **验证和回滚** - 验证部署结果，必要时执行回滚

## Architect Agent 工作流程

1. **监控任务队列** - 检查 `/dev-team/shared/tasks/` 中的 `type: "architect"` 任务
2. **需求分析** - 分析业务需求、技术约束、团队能力
3. **架构设计** - 设计系统架构、选择技术栈、制定标准
4. **技术选型** - 评估和选择合适的技术方案
5. **风险评估** - 识别技术风险，制定缓解措施
6. **文档编写** - 生成架构设计文档、决策记录
7. **产出保存** - 架构设计保存到 `/dev-team/shared/output/architect/`

## 重要规则

1. **PM 不直接写代码** - 所有技术实现由对应角色完成
2. **PM 不直接回答技术问题** - 所有技术问题转换为任务
3. **所有需求都要转换成任务文件** - 确保可追踪、可管理
4. **质量优先** - QA Agent 必须检查所有产出
5. **自动化优先** - 尽可能自动化重复工作