---
dimension: Architecture
weight: 0.1
tier: normal
threshold: 85
description: 架构一致性检查 - 确保遵循 MVVM + Clean Architecture
metrics:
  - name: mvvm_layers_complete
    description: MVVM 层完整（Model, View, ViewModel）
    type: boolean
    required: true
    
  - name: clean_architecture_layers
    description: Clean Architecture 层完整（data, domain, ui）
    type: boolean
    required: true
    
  - name: di_configured
    description: 依赖注入配置正确（Hilt）
    type: boolean
    required: true
    
  - name: repository_pattern
    description: Repository 模式正确使用
    type: boolean
    required: true

evidence:
  - 项目结构检查
  - 依赖注入模块检查
---

# 架构一致性 Fitness Function

## 检查项

### 1. MVVM 层
- [x] Model - Entity/Repository
- [x] View - Compose UI
- [x] ViewModel - State 管理

### 2. Clean Architecture 层
- [x] data - Repository, DAO, API
- [x] domain - UseCase, Entity
- [x] ui - ViewModel, Screen

### 3. 依赖注入
- [x] Hilt 配置正确
- [x] Module 定义清晰
- [x] 作用域正确（@Singleton, @ViewModelScoped）

## 通过标准

- ✅ 分层清晰
- ✅ 依赖方向正确（UI → Domain → Data）
- ✅ 没有循环依赖

## 架构原则

1. **单一职责**: 每个类只做一件事
2. **依赖倒置**: 依赖抽象，不依赖具体实现
3. **接口隔离**: 接口要小而专一
