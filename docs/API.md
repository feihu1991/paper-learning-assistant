# Paper Tutor API 文档

## 基础信息

- **Base URL**: `http://localhost:8080/api/v1`
- **认证方式**: JWT Token (待实现)
- **数据格式**: JSON

## 错误码

| 代码 | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 论文模块

### 1. 搜索论文

```http
GET /api/v1/papers/search
```

**请求参数:**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| keyword | String | 是 | - | 搜索关键词 |
| source | String | 否 | local | 数据源 (arxiv/crossref/local) |
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 20 | 每页数量 |

**响应示例:**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 156,
    "page": 1,
    "size": 20,
    "papers": [
      {
        "id": 1,
        "arxivId": "1706.03762",
        "title": "Attention Is All You Need",
        "authors": ["Vaswani, Ashish", "Shazeer, Noam"],
        "publishDate": "2017-06-12",
        "abstract": "The dominant sequence transduction models...",
        "category": "cs.CL",
        "parsedStatus": "COMPLETED"
      }
    ]
  }
}
```

### 2. 获取论文详情

```http
GET /api/v1/papers/{id}
```

**路径参数:**

| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 论文 ID |

**响应示例:**

```json
{
  "code": 200,
  "data": {
    "id": 1,
    "arxivId": "1706.03762",
    "title": "Attention Is All You Need",
    "authors": [...],
    "abstract": "...",
    "structuredSummary": {
      "background": "...",
      "problem": "...",
      "method": "...",
      "experiments": "...",
      "conclusion": "..."
    },
    "learningStepsCount": 8,
    "pdfUrl": "/api/v1/papers/1/pdf"
  }
}
```

### 3. 执行论文分析

```http
POST /api/v1/papers/{id}/analyze
```

**请求体:**

```json
{
  "analysisTypes": ["STRUCTURE", "KEY_INFO", "DEEP_INSIGHT"],
  "includeLLM": true,
  "templateId": 1
}
```

**响应:**

```json
{
  "code": 200,
  "data": {
    "analysisId": 1,
    "paperId": 1,
    "status": "PROCESSING",
    "estimatedTime": "3-8 minutes",
    "tasks": [
      {"type": "STRUCTURE", "status": "QUEUED"},
      {"type": "KEY_INFO", "status": "QUEUED"},
      {"type": "DEEP_INSIGHT", "status": "QUEUED"}
    ]
  }
}
```

### 4. 获取分析结果

```http
GET /api/v1/papers/{id}/analysis
```

**响应:**

```json
{
  "code": 200,
  "data": {
    "paperId": 1,
    "analysisVersion": "2.1.0",
    "confidenceScore": 0.92,
    "structure": {
      "sections": [...],
      "totalPages": 15,
      "figureCount": 4,
      "tableCount": 7
    },
    "keyInformation": {
      "datasets": [...],
      "metrics": [...],
      "formulas": [...],
      "contributions": [...]
    },
    "deepInsights": {
      "innovationScore": 9,
      "difficultyLevel": "ADVANCED",
      "prerequisiteKnowledge": [...],
      "relatedPapers": [...]
    }
  }
}
```

### 5. 获取学习路径

```http
GET /api/v1/papers/{id}/learning-path
```

**响应:**

```json
{
  "code": 200,
  "data": {
    "paperId": 1,
    "paperTitle": "Attention Is All You Need",
    "totalSteps": 8,
    "estimatedTime": 45,
    "steps": [
      {
        "stepId": 1,
        "order": 1,
        "type": "CONCEPT",
        "title": "什么是 Transformer？",
        "content": "Transformer 是一种基于自注意力机制的深度学习模型...",
        "mediaType": "GIF",
        "mediaPath": "/media/papers/1/transformer-overview.gif",
        "estimatedMinutes": 5
      }
    ]
  }
}
```

### 6. 上传 PDF

```http
POST /api/v1/papers/upload
Content-Type: multipart/form-data
```

**请求参数:**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | PDF 文件 |
| title | String | 否 | 论文标题 |

**响应:**

```json
{
  "code": 200,
  "data": {
    "paperId": 1,
    "message": "上传成功",
    "filename": "attention-is-all-you-need.pdf"
  }
}
```

---

## 用户模块

### 7. 提交学习进度

```http
POST /api/v1/users/{userId}/progress
```

**请求体:**

```json
{
  "paperId": 1,
  "completedStepIds": [1, 2, 3],
  "currentStep": 4
}
```

### 8. 获取用户进度

```http
GET /api/v1/users/{userId}/progress/{paperId}
```

---

## 媒体模块

### 9. 获取媒体文件

```http
GET /api/v1/media/papers/{paperId}/{filename}
```

---

## Swagger UI

访问 `http://localhost:8080/swagger-ui.html` 查看交互式 API 文档。
