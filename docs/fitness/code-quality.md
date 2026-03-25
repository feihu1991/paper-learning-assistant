---
dimension: Code Quality
weight: 0.3
tier: fast
threshold: 80
description: 代码质量检查 - 确保代码可读、可维护
metrics:
  - name: lint_pass
    description: Kotlin 代码通过 lint 检查
    type: boolean
    required: true
    
  - name: test_pass
    description: 单元测试通过
    type: boolean
    required: true
    
  - name: budget_compliance
    description: 文件行数符合预算
    type: boolean
    required: true
    
  - name: no_todo
    description: 没有未解决的 TODO 注释
    type: count
    threshold: 10
    
  - name: comment_ratio
    description: 注释比例（10%-30%）
    type: percentage
    min: 10
    max: 30

evidence:
  - tools/entrix/check-file-budgets.py 输出
  - ./gradlew lint 输出
  - ./gradlew test 输出
---

# 代码质量 Fitness Function

## 检查项

### 1. Lint 检查
```bash
./gradlew lint
```

### 2. 测试检查
```bash
./gradlew test
```

### 3. 文件预算检查
```bash
python3 tools/entrix/check-file-budgets.py
```

## 通过标准

- ✅ 所有必需指标通过
- ✅ 总分 >= 80 分
- ✅ 没有 Critical 违规

## 失败处理

如果检查失败：
1. 根据错误信息修复
2. 重新运行检查
3. 如果无法修复，记录技术债务
