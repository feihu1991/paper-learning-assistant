# Agent 协作规则

> **单一事实源**：本文件是 Agent 协作规则的唯一入口，CLAUDE.md 是指向它的软链接。

## 📜 协作纪律

### 提交规范
- ✅ **baby-step commit**: 每个小功能单独提交，不要一次性提交大量代码
- ✅ **co-author 规范**: AI 生成的代码必须标注 `Co-authored-by: AI Agent <agent@papertutor.ai>`
- ✅ **提交前检查**: 
  - 代码能否编译
  - 测试是否通过
  - 文件是否超出预算

### 知识入口（渐进式披露）

**不要一次性读取所有文档**，按需要下钻：

| 问题类型 | 读取文档 |
|----------|----------|
| 产品功能 | `docs/product-specs/FEATURE_TREE.md` |
| 系统架构 | `docs/ARCHITECTURE.md` |
| 设计意图 | `docs/design-docs/` |
| 执行计划 | `docs/exec-plans/` |
| 质量验证 | `docs/fitness/` |

---

## 🎯 当前项目状态

**项目名称**: Paper Learning Assistant  
**技术栈**: Kotlin + Jetpack Compose + Room + Hilt  
**目标**: Android 论文学习助手

**当前阶段**: 初始开发（构建配置中）

---

## 📁 项目结构

```
paper-tutor/
├── AGENTS.md              # 本文件：Agent 协作规则
├── android-app/           # Android 应用
│   ├── app/
│   │   ├── src/main/java/
│   │   │   └── com/paperlearning/assistant/
│   │   │       ├── data/          # 数据层（Repository, DAO, Entity）
│   │   │       ├── domain/        # 领域层（UseCase）
│   │   │       ├── di/            # 依赖注入（Hilt Module）
│   │   │       ├── ui/            # UI 层（Compose, ViewModel）
│   │   │       ├── util/          # 工具类
│   │   │       ├── MainActivity.kt
│   │   │       └── PaperLearningApp.kt
│   │   └── src/main/res/  # 资源文件
│   └── build.gradle.kts
├── docs/                  # 文档
│   ├── product-specs/     # 产品规格
│   ├── design-docs/       # 设计文档
│   ├── exec-plans/        # 执行计划
│   ├── fitness/           # 质量验证
│   └── API.md             # API 文档
└── tools/                 # 治理工具
    └── entrix/            # 反熵治理引擎
```

---

## ✅ 提交前检查清单

在提交代码前，必须确认：

- [ ] 代码能编译通过
- [ ] 文件行数未超出预算（Kotlin < 500 行，XML < 300 行）
- [ ] 添加了必要的测试
- [ ] 更新了相关文档
- [ ] 提交信息清晰（使用 Conventional Commits）

---

## 🚫 禁止行为

- ❌ 不要一次性生成大量代码（超过 1000 行）
- ❌ 不要修改未授权的文件（见文件预算）
- ❌ 不要跳过测试
- ❌ 不要忽略 Fitness 检查结果

---

## 🔄 反馈环

如果遇到问题：

1. **编译错误** → 检查依赖配置
2. **文件超预算** → 重构或拆分文件
3. **测试失败** → 修复后重试
4. **不确定** → 询问用户

---

**最后更新**: 2026-03-25  
**版本**: 1.0
