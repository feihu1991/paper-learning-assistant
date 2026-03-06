# API 风格指南

## RESTful API 设计原则

### 1. 资源命名
- 使用名词复数形式
- 使用小写字母和连字符
- 避免动词在 URL 中

**正确示例**:
```
GET    /users
GET    /users/{id}
POST   /users
PUT    /users/{id}
DELETE /users/{id}
```

**错误示例**:
```
GET    /getUsers
POST   /createUser
GET    /user/list
```

### 2. HTTP 方法使用
| 方法 | 用途 | 幂等性 | 安全性 |
|------|------|--------|--------|
| GET | 获取资源 | 是 | 是 |
| POST | 创建资源 | 否 | 否 |
| PUT | 更新整个资源 | 是 | 否 |
| PATCH | 部分更新资源 | 否 | 否 |
| DELETE | 删除资源 | 是 | 否 |

### 3. 状态码
| 状态码 | 含义 | 使用场景 |
|--------|------|----------|
| 200 | OK | 成功请求 |
| 201 | Created | 资源创建成功 |
| 204 | No Content | 成功但无返回内容 |
| 400 | Bad Request | 客户端错误 |
| 401 | Unauthorized | 未认证 |
| 403 | Forbidden | 无权限 |
| 404 | Not Found | 资源不存在 |
| 409 | Conflict | 资源冲突 |
| 422 | Unprocessable Entity | 验证失败 |
| 429 | Too Many Requests | 请求过多 |
| 500 | Internal Server Error | 服务器错误 |

## 请求和响应格式

### 请求头
```http
Content-Type: application/json
Authorization: Bearer {token}
Accept: application/json
X-Request-ID: {uuid}
```

### 请求体示例
```json
{
  "user": {
    "name": "张三",
    "email": "zhangsan@example.com",
    "password": "securePassword123"
  }
}
```

### 响应体格式
```json
{
  "success": true,
  "data": {
    "id": "123",
    "name": "张三",
    "email": "zhangsan@example.com",
    "created_at": "2024-01-01T12:00:00Z"
  },
  "meta": {
    "total": 1,
    "page": 1,
    "limit": 20
  },
  "error": null
}
```

### 错误响应格式
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "邮箱格式不正确",
    "details": [
      {
        "field": "email",
        "message": "必须是有效的邮箱地址"
      }
    ]
  }
}
```

## 分页和过滤

### 分页参数
```
GET /users?page=1&limit=20&sort=created_at&order=desc
```

### 过滤参数
```
GET /users?status=active&role=admin&created_after=2024-01-01
```

### 搜索参数
```
GET /users?q=张三&fields=name,email
```

### 分页响应
```json
{
  "data": [...],
  "meta": {
    "total": 150,
    "page": 1,
    "limit": 20,
    "pages": 8,
    "has_next": true,
    "has_prev": false
  }
}
```

## 版本管理

### URL 版本控制
```
/api/v1/users
/api/v2/users
```

### 请求头版本控制
```
Accept: application/vnd.example.v1+json
```

## 认证和授权

### JWT 认证
```javascript
// 请求头
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

// Token 结构
{
  "sub": "user_id",
  "role": "admin",
  "iat": 1516239022,
  "exp": 1516242622
}
```

### API Key 认证
```
X-API-Key: {api_key}
```

### 权限控制
```javascript
// 中间件示例
const requireRole = (role) => {
  return (req, res, next) => {
    if (req.user.role !== role) {
      return res.status(403).json({ error: 'Forbidden' });
    }
    next();
  };
};
```

## 数据验证

### 请求验证
```javascript
// 使用 Joi 或类似库
const schema = Joi.object({
  name: Joi.string().min(2).max(50).required(),
  email: Joi.string().email().required(),
  age: Joi.number().integer().min(0).max(120)
});
```

### 响应验证
- 确保响应数据符合约定格式
- 移除敏感信息
- 格式化日期时间

## 性能优化

### 1. 缓存策略
```http
Cache-Control: public, max-age=3600
ETag: "33a64df551425fcc55e4d42a148795d9f25f89d4"
```

### 2. 压缩响应
```http
Content-Encoding: gzip
```

### 3. 分页限制
- 默认分页大小: 20
- 最大分页大小: 100
- 避免全量数据查询

### 4. 字段选择
```
GET /users?fields=id,name,email
```

## 文档规范

### OpenAPI/Swagger
```yaml
openapi: 3.0.0
info:
  title: 用户管理 API
  version: 1.0.0
paths:
  /users:
    get:
      summary: 获取用户列表
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 1
      responses:
        '200':
          description: 成功
```

### API 文档包含内容
1. **端点描述**: 功能说明
2. **请求示例**: 完整请求示例
3. **响应示例**: 成功和失败响应
4. **参数说明**: 所有参数说明
5. **错误码**: 可能的错误码
6. **权限要求**: 需要的权限

## 监控和日志

### 请求日志
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "method": "GET",
  "path": "/api/v1/users",
  "status": 200,
  "duration": 45,
  "ip": "192.168.1.1",
  "user_agent": "Mozilla/5.0...",
  "request_id": "req_123456"
}
```

### 性能指标
- 响应时间 (平均、P95、P99)
- 请求率 (QPS)
- 错误率
- 资源使用率

### 健康检查端点
```
GET /health
```

响应:
```json
{
  "status": "healthy",
  "timestamp": "2024-01-01T12:00:00Z",
  "services": {
    "database": "connected",
    "cache": "connected",
    "external_api": "connected"
  }
}
```

## 安全最佳实践

### 1. 输入验证
- 验证所有输入参数
- 防止 SQL 注入
- 防止 XSS 攻击

### 2. 输出编码
- 对输出进行编码
- 设置 Content-Type
- 避免信息泄露

### 3. 速率限制
```javascript
// 使用 express-rate-limit
const rateLimit = require('express-rate-limit');

const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15分钟
  max: 100 // 每个IP最多100次请求
});
```

### 4. CORS 配置
```javascript
app.use(cors({
  origin: ['https://example.com'],
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));
```

### 5. HTTPS 强制
```javascript
// 重定向 HTTP 到 HTTPS
app.use((req, res, next) => {
  if (!req.secure) {
    return res.redirect(`https://${req.headers.host}${req.url}`);
  }
  next();
});
```

## 测试策略

### 单元测试
- 测试控制器逻辑
- 测试服务层
- 测试数据验证

### 集成测试
- 测试 API 端点
- 测试数据库交互
- 测试外部服务

### 性能测试
- 负载测试
- 压力测试
- 耐久测试

### 安全测试
- 渗透测试
- 漏洞扫描
- 依赖检查