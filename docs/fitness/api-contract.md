---
dimension: API Contract
weight: 0.2
tier: normal
threshold: 90
description: API 契约检查 - 确保 API 接口一致性
metrics:
  - name: arxiv_api_defined
    description: ArxivApiService 接口定义完整
    type: boolean
    required: true
    
  - name: llm_api_defined
    description: LlmApiService 接口定义完整
    type: boolean
    required: true
    
  - name: response_models_complete
    description: 响应数据模型完整
    type: boolean
    required: true

evidence:
  - android-app/app/src/main/java/com/paperlearning/assistant/data/remote/arxiv/ArxivApiService.kt
  - android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/LlmApiService.kt
---

# API 契约 Fitness Function

## 检查项

### 1. ArXiv API 接口
- [x] searchPapers() - 搜索论文
- [x] getPaperById() - 根据 ID 获取
- [x] ArxivResponse - 响应模型
- [x] ArxivEntry - 条目模型

### 2. LLM API 接口
- [x] chatCompletion() - 聊天完成
- [x] LlmRequest - 请求模型
- [x] LlmResponse - 响应模型

## 通过标准

- ✅ 所有必需接口已定义
- ✅ 请求/响应模型完整
- ✅ Retrofit 配置正确

## 契约优先原则

新增 API 时的顺序：
1. 先定义接口（ArxivApiService）
2. 再定义数据模型（ArxivResponse）
3. 最后实现逻辑
