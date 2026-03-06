# Dev Team 工作流程自动化

## 概述
这是一个自动化的工作流程系统，用于协调多个 AI agent 协作完成项目任务。

## 目录结构
```
/dev-team/shared/
├── tasks/          # 任务队列 (PM 创建任务文件)
├── processing/     # 正在处理的任务
├── output/         # 产出目录
│   ├── be/         # 后端产出
│   ├── fe/         # 前端产出
│   ├── qa/         # 测试报告
│   ├── devops/     # 部署配置
│   └── architect/  # 架构设计
├── docs/           # 文档和模板
├── reports/        # 完成报告
├── bugs/           # Bug反馈
├── scripts/        # 自动化脚本
└── logs/           # 系统日志
```

## 工作流程

### 1. 任务创建
- PM 在 `tasks/` 目录创建 JSON 格式的任务文件
- 任务文件包含: task_id, type, objective, specs, priority, deadline

### 2. 任务监控
- 监控脚本每分钟检查 `tasks/` 目录
- 根据任务类型分配给相应的 agent
- 任务被移动到 `processing/` 目录

### 3. 任务处理
- 相应的 agent 处理任务
- 产出保存到 `output/{type}/` 目录
- 生成处理报告

### 4. 质量检查
- QA Agent 检查所有产出
- 发现问题时提交到 `bugs/` 目录
- 生成质量报告

### 5. 项目验收
- PM 验收最终产出
- 生成项目报告
- 归档项目文件

## 快速开始

### 安装依赖
```bash
cd /dev-team/shared
npm install
```

### 运行监控
```bash
npm run monitor
```

### 设置工作流程
```bash
npm run setup
```

## Agent 配置

系统支持以下类型的 agent:
- **BE Agent**: 后端开发
- **FE Agent**: 前端开发  
- **QA Agent**: 质量保证
- **DevOps Agent**: 运维部署
- **Architect Agent**: 架构设计

每个 agent 的详细配置见 `docs/` 目录。

## 任务文件格式

任务文件必须是 JSON 格式，包含以下字段:
```json
{
  "task_id": "unique_id",
  "type": "be|fe|qa|devops|architect",
  "objective": "任务目标描述",
  "specs": {
    // 具体规格和要求
  },
  "shared_context": [
    "相关文档路径"
  ],
  "priority": "high|medium|low",
  "deadline": "ISO 8601 时间格式",
  "output_location": "产出保存路径"
}
```

## 监控和日志

- 监控脚本每分钟运行一次
- 日志保存在 `logs/` 目录
- 可以通过 cron 任务配置监控频率

## 故障排除

### 常见问题
1. **任务没有被处理**: 检查监控脚本是否在运行
2. **产出目录不存在**: 运行 `npm run setup` 重新创建目录
3. **权限问题**: 确保脚本有读写权限

### 查看日志
```bash
tail -f logs/task_monitor.log
```

## 扩展和定制

可以通过修改以下文件定制工作流程:
- `scripts/monitor_tasks.js`: 任务监控逻辑
- `scripts/setup_workflow.js`: 系统设置
- `docs/*_template.json`: 任务模板

---

*最后更新: 2026-03-06T00:45:19.438Z*
*由工作流程自动化脚本生成*