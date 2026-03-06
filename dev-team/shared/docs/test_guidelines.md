# 测试指南

## 测试原则

### 1. 测试金字塔
```
        E2E 测试 (10%)
           /\
          /  \
         /    \
  集成测试 (20%)
       /\
      /  \
     /    \
单元测试 (70%)
```

### 2. 测试优先
- **测试驱动开发 (TDD)**: 先写测试，再写实现
- **行为驱动开发 (BDD)**: 基于用户行为的测试
- **验收测试驱动开发 (ATDD)**: 基于验收标准的测试

### 3. 持续测试
- 每次代码提交都运行测试
- 自动化测试流水线
- 快速反馈机制

## 测试类型

### 单元测试
**目的**: 测试单个函数/方法的正确性
**范围**: 最小可测试单元
**工具**: Jest, Mocha, Jasmine

**最佳实践**:
- 每个测试用例只测试一个功能
- 使用描述性的测试名称
- 避免测试实现细节
- 使用 Mock/Stub 隔离依赖

```javascript
// 示例
describe('UserService', () => {
  it('should create user with valid data', () => {
    // 测试逻辑
  });
  
  it('should reject duplicate email', () => {
    // 测试逻辑
  });
});
```

### 集成测试
**目的**: 测试模块间的交互
**范围**: 多个模块的组合
**工具**: Supertest, Jest

**最佳实践**:
- 测试真实的数据库连接
- 测试 API 端点
- 测试外部服务集成

```javascript
// 示例
describe('User API Integration', () => {
  it('should return user list', async () => {
    const response = await request(app).get('/api/users');
    expect(response.status).toBe(200);
  });
});
```

### 端到端测试
**目的**: 测试完整业务流程
**范围**: 整个应用
**工具**: Cypress, Playwright, Selenium

**最佳实践**:
- 模拟真实用户操作
- 测试关键业务流程
- 避免脆弱的测试

```javascript
// 示例
describe('User Registration Flow', () => {
  it('should complete registration', () => {
    cy.visit('/register');
    cy.get('#email').type('test@example.com');
    cy.get('#submit').click();
    cy.url().should('include', '/welcome');
  });
});
```

### 性能测试
**目的**: 测试系统性能指标
**范围**: 关键性能路径
**工具**: k6, Artillery, Lighthouse

**测试指标**:
- 响应时间 (平均、P95、P99)
- 吞吐量 (请求/秒)
- 错误率
- 资源使用 (CPU、内存)

```javascript
// k6 示例
import http from 'k6/http';

export default function() {
  const response = http.get('https://api.example.com/users');
  check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 200ms': (r) => r.timings.duration < 200
  });
}
```

### 安全测试
**目的**: 发现安全漏洞
**范围**: 所有公开接口
**工具**: OWASP ZAP, Snyk, npm audit

**检查项**:
- SQL 注入
- XSS 攻击
- CSRF 漏洞
- 认证绕过
- 敏感数据泄露

## 代码质量检查

### ESLint 规则
```javascript
// .eslintrc.js
module.exports = {
  rules: {
    'no-console': 'warn',
    'no-unused-vars': 'error',
    'eqeqeq': 'error',
    'curly': 'error',
    'quotes': ['error', 'single']
  }
};
```

### 复杂度控制
- **圈复杂度**: ≤ 10
- **认知复杂度**: ≤ 15
- **函数行数**: ≤ 30
- **文件行数**: ≤ 500

### 代码重复
- 重复代码比例: ≤ 5%
- 使用工具: jscpd, SonarQube

## 测试覆盖率

### 覆盖率目标
- **语句覆盖率**: ≥ 80%
- **分支覆盖率**: ≥ 70%
- **函数覆盖率**: ≥ 85%
- **行覆盖率**: ≥ 80%

### 覆盖率排除
```json
{
  "coveragePathIgnorePatterns": [
    "/node_modules/",
    "/test/",
    "/coverage/",
    "*.test.js",
    "*.spec.js"
  ]
}
```

## 测试报告

### 报告格式
1. **HTML 报告**: 可视化展示
2. **JSON 报告**: 机器可读
3. **JUnit XML**: CI/CD 集成
4. **控制台输出**: 实时反馈

### 报告内容
- 测试结果摘要
- 失败详情
- 覆盖率报告
- 性能指标
- 建议改进

### 报告示例
```json
{
  "summary": {
    "total": 150,
    "passed": 145,
    "failed": 5,
    "skipped": 0,
    "duration": "45.2s"
  },
  "coverage": {
    "statements": 85,
    "branches": 78,
    "functions": 90,
    "lines": 84
  },
  "performance": {
    "avg_response": "120ms",
    "p95": "210ms",
    "throughput": "950 req/s"
  }
}
```

## CI/CD 集成

### 流水线阶段
1. **代码检查**: ESLint, Prettier
2. **单元测试**: 快速反馈
3. **集成测试**: 模块交互
4. **E2E 测试**: 完整流程
5. **性能测试**: 性能验证
6. **安全扫描**: 安全检查
7. **部署测试**: 生产环境验证

### 质量门禁
- 测试通过率: 100%
- 覆盖率达标: 是/否
- 无安全漏洞: 是/否
- 性能达标: 是/否

## 最佳实践

### 1. 测试数据管理
- 使用测试数据工厂
- 清理测试数据
- 避免生产数据污染

### 2. 测试环境
- 与生产环境一致
- 可重复的测试环境
- 快速环境搭建

### 3. 测试维护
- 定期清理过时测试
- 更新测试数据
- 优化测试性能

### 4. 团队协作
- 统一的测试标准
- 代码审查包含测试
- 测试知识共享

## 工具推荐

### JavaScript/TypeScript
- **测试框架**: Jest, Mocha
- **断言库**: Chai, Jest Assertions
- **Mock**: Sinon, Jest Mocks
- **E2E**: Cypress, Playwright
- **覆盖率**: Istanbul, Jest Coverage

### API 测试
- **HTTP 客户端**: Supertest, Axios
- **GraphQL**: Apollo Testing
- **WebSocket**: Socket.IO Testing

### 性能测试
- **负载测试**: k6, Artillery
- **Web 性能**: Lighthouse, WebPageTest
- **监控**: New Relic, Datadog

### 安全测试
- **静态分析**: Snyk, npm audit
- **动态分析**: OWASP ZAP
- **依赖检查**: Dependabot, Renovate