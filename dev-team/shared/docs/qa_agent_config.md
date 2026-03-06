# QA Agent 配置文档

## 概述
QA Agent 负责自动化质量保证，包括测试、代码检查、性能测试和报告生成。

## 核心职责

### 1. 自动化测试
- **单元测试**: 针对函数/方法的独立测试
- **集成测试**: 模块间接口测试
- **端到端测试**: 完整业务流程测试

### 2. 代码质量检查
- **ESLint**: JavaScript/TypeScript 代码规范检查
- **代码规范**: 团队约定的编码规范
- **安全扫描**: 安全漏洞检查

### 3. 性能测试
- **响应时间**: API 接口响应时间测试
- **内存使用**: 内存泄漏检测
- **并发测试**: 高并发场景测试
- **负载测试**: 系统负载能力测试

### 4. 测试报告生成
- **测试覆盖率报告**
- **代码质量报告**
- **性能测试报告**
- **安全扫描报告**

### 5. 验收确认
- **自动化验收**: 基于测试结果的自动验收
- **人工验收**: 需要人工确认的场景
- **Bug 跟踪**: 问题追踪和修复验证

## 技术栈

### 测试框架
- **Jest**: JavaScript 测试框架
- **Mocha**: Node.js 测试框架
- **Cypress**: 端到端测试
- **Playwright**: 浏览器自动化测试

### 代码检查
- **ESLint**: JavaScript 代码检查
- **Prettier**: 代码格式化
- **SonarQube**: 代码质量平台

### 性能测试
- **k6**: 负载测试工具
- **Artillery**: 现代负载测试工具
- **Lighthouse**: Web 性能测试

### 报告工具
- **Allure**: 测试报告框架
- **JUnit**: XML 测试报告
- **HTML Reporter**: 自定义 HTML 报告

## 工作流程

### 1. 任务接收
```json
{
  "task_id": "qa_001",
  "type": "qa",
  "objective": "测试用户管理模块",
  "specs": {
    "test_type": ["unit", "integration"],
    "code_path": "/dev-team/shared/output/be/user-management/",
    "coverage_threshold": 80,
    "performance_targets": {
      "response_time": "200ms",
      "concurrent_users": 1000
    }
  },
  "shared_context": [
    "/dev-team/shared/docs/api_spec.md",
    "/dev-team/shared/docs/test_guidelines.md"
  ]
}
```

### 2. 测试执行流程
1. **环境准备**: 安装依赖、配置测试环境
2. **单元测试**: 运行 Jest/Mocha 单元测试
3. **集成测试**: 测试模块间接口
4. **代码检查**: ESLint、代码规范检查
5. **性能测试**: 响应时间、负载测试
6. **报告生成**: 生成测试报告
7. **验收确认**: 标记任务状态

### 3. 报告格式
```json
{
  "task_id": "qa_001",
  "status": "passed|failed|warning",
  "test_results": {
    "unit_tests": {
      "total": 50,
      "passed": 48,
      "failed": 2,
      "coverage": 85
    },
    "integration_tests": {
      "total": 20,
      "passed": 20,
      "failed": 0
    },
    "code_quality": {
      "eslint_errors": 3,
      "eslint_warnings": 5,
      "complexity": "medium"
    },
    "performance": {
      "avg_response_time": "150ms",
      "p95_response_time": "220ms",
      "memory_usage": "45MB",
      "throughput": "1200 req/s"
    }
  },
  "issues": [
    {
      "type": "bug|improvement|security",
      "severity": "critical|high|medium|low",
      "description": "具体问题描述",
      "location": "文件路径:行号",
      "suggestion": "修复建议"
    }
  ],
  "recommendations": [
    "建议增加错误处理",
    "建议优化数据库查询"
  ],
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 配置示例

### QA Agent 配置文件
```json
{
  "qa_agent": {
    "name": "质量保证代理",
    "version": "1.0.0",
    "monitoring_paths": [
      "/dev-team/shared/tasks/",
      "/dev-team/shared/output/be/",
      "/dev-team/shared/output/fe/"
    ],
    "test_frameworks": {
      "javascript": "jest",
      "typescript": "jest",
      "nodejs": "mocha",
      "e2e": "cypress"
    },
    "code_quality": {
      "eslint_config": ".eslintrc.js",
      "prettier_config": ".prettierrc",
      "complexity_threshold": 10
    },
    "performance": {
      "response_time_threshold": "500ms",
      "memory_threshold": "100MB",
      "concurrent_users": 1000
    },
    "reporting": {
      "format": ["html", "json", "junit"],
      "output_path": "/dev-team/shared/output/qa/",
      "notify_on_failure": true
    }
  }
}
```

### 测试脚本模板
```javascript
// test-template.js
module.exports = {
  setup: async () => {
    // 测试环境准备
  },
  unitTests: async (codePath) => {
    // 单元测试逻辑
  },
  integrationTests: async (codePath) => {
    // 集成测试逻辑
  },
  codeQualityCheck: async (codePath) => {
    // 代码质量检查
  },
  performanceTest: async (codePath) => {
    // 性能测试
  },
  generateReport: async (results) => {
    // 生成报告
  }
};
```

## 集成点

### 1. 与 BE/FE 集成
- 监控 BE/FE 产出目录
- 自动触发测试
- 反馈测试结果

### 2. 与 DevOps 集成
- 提供测试报告给部署流程
- 质量门禁控制
- 自动化部署验证

### 3. 与 PM 集成
- 提供验收依据
- 生成质量报告
- 问题跟踪

## 最佳实践

### 测试策略
1. **测试金字塔**: 70% 单元测试，20% 集成测试，10% E2E 测试
2. **测试驱动开发**: 先写测试，再写实现
3. **持续测试**: 每次代码变更都运行测试

### 质量门禁
1. **代码覆盖率**: 不低于 80%
2. **无严重 Bug**: 无 Critical/High 级别 Bug
3. **性能达标**: 响应时间符合要求
4. **安全合规**: 无安全漏洞

### 报告优化
1. **可视化报告**: 图表展示测试结果
2. **趋势分析**: 历史数据对比
3. **根因分析**: 问题根本原因分析
4. **改进建议**: 具体可行的改进建议