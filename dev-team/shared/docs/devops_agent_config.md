# DevOps Agent 配置文档

## 概述
DevOps Agent 负责自动化部署、环境管理、监控告警和持续集成/持续部署 (CI/CD) 流程。

## 核心职责

### 1. 自动化部署
- **应用部署**: 自动化部署应用程序
- **环境管理**: 开发、测试、预生产、生产环境管理
- **配置管理**: 环境配置和密钥管理
- **回滚机制**: 快速回滚到之前版本

### 2. 持续集成/持续部署 (CI/CD)
- **代码构建**: 自动化构建过程
- **测试流水线**: 集成测试自动化
- **质量门禁**: 部署前的质量检查
- **自动化发布**: 一键发布到不同环境

### 3. 基础设施即代码 (IaC)
- **环境编排**: 使用代码定义基础设施
- **资源管理**: 云资源自动化管理
- **配置漂移检测**: 检测配置变更

### 4. 监控和告警
- **应用监控**: 性能指标监控
- **日志管理**: 集中式日志收集
- **告警系统**: 异常告警通知
- **健康检查**: 服务健康状态监控

### 5. 安全和合规
- **安全扫描**: 容器镜像安全扫描
- **合规检查**: 安全策略合规性
- **漏洞管理**: 漏洞扫描和修复
- **访问控制**: 权限管理和审计

## 技术栈

### 容器化
- **Docker**: 容器化平台
- **Docker Compose**: 多容器应用
- **BuildKit**: 高效的镜像构建

### 编排平台
- **Kubernetes**: 容器编排
- **Helm**: Kubernetes 包管理
- **Kustomize**: Kubernetes 原生配置管理

### CI/CD 工具
- **GitHub Actions**: CI/CD 流水线
- **Jenkins**: 自动化服务器
- **GitLab CI/CD**: 集成 CI/CD
- **ArgoCD**: GitOps 持续交付

### 基础设施即代码
- **Terraform**: 多云基础设施管理
- **Pulumi**: 使用编程语言定义基础设施
- **Ansible**: 配置管理和应用部署
- **CloudFormation**: AWS 基础设施即代码

### 监控和日志
- **Prometheus**: 监控系统
- **Grafana**: 监控仪表板
- **ELK Stack**: 日志管理
- **Loki**: 轻量级日志聚合
- **Jaeger**: 分布式追踪

### 安全工具
- **Trivy**: 容器镜像漏洞扫描
- **Aqua Security**: 云原生安全
- **Sysdig**: 容器安全和监控
- **Falco**: 运行时安全

## 工作流程

### 1. 任务接收
```json
{
  "task_id": "devops_001",
  "type": "devops",
  "objective": "部署用户管理服务到 Kubernetes",
  "specs": {
    "application": "user-management-service",
    "environment": "production",
    "deployment_strategy": "blue-green",
    "infrastructure": {
      "kubernetes": true,
      "cloud_provider": "aws",
      "region": "us-east-1"
    },
    "monitoring": {
      "metrics": ["cpu", "memory", "response_time"],
      "alerts": ["error_rate", "latency"]
    }
  },
  "shared_context": [
    "/dev-team/shared/docs/tech_stack.md",
    "/dev-team/shared/docs/deployment_guidelines.md"
  ]
}
```

### 2. 部署执行流程
1. **代码检查**: 验证代码质量和安全
2. **构建镜像**: Docker 镜像构建和推送
3. **环境准备**: 基础设施准备和配置
4. **部署执行**: 应用部署到目标环境
5. **健康检查**: 服务健康状态验证
6. **监控配置**: 监控和告警设置
7. **文档更新**: 部署文档更新

### 3. 报告格式
```json
{
  "task_id": "devops_001",
  "status": "success|failed|partial",
  "deployment_details": {
    "application": "user-management-service",
    "version": "v1.2.3",
    "environment": "production",
    "deployment_time": "2024-01-01T12:00:00Z",
    "duration": "5m 30s",
    "strategy": "blue-green"
  },
  "infrastructure": {
    "kubernetes": {
      "namespace": "production",
      "pods": 3,
      "replicas": 3,
      "resources": {
        "cpu": "2",
        "memory": "4Gi"
      }
    },
    "cloud_resources": [
      {
        "type": "EC2",
        "count": 3,
        "instance_type": "t3.medium"
      },
      {
        "type": "RDS",
        "engine": "PostgreSQL",
        "version": "14"
      }
    ]
  },
  "monitoring": {
    "metrics_endpoint": "http://monitoring.example.com",
    "dashboard_url": "http://grafana.example.com/d/xyz",
    "alerts_configured": 5
  },
  "health_checks": {
    "readiness": "passed",
    "liveness": "passed",
    "endpoints": [
      {
        "url": "/api/health",
        "status": 200,
        "response_time": "45ms"
      }
    ]
  },
  "rollback_info": {
    "previous_version": "v1.2.2",
    "rollback_available": true,
    "rollback_time_estimate": "2m"
  },
  "issues": [
    {
      "severity": "warning",
      "description": "内存使用率接近阈值",
      "suggestion": "考虑增加内存限制"
    }
  ]
}
```

## 配置示例

### DevOps Agent 配置文件
```json
{
  "devops_agent": {
    "name": "运维自动化代理",
    "version": "1.0.0",
    "monitoring_paths": [
      "/dev-team/shared/tasks/",
      "/dev-team/shared/output/be/",
      "/dev-team/shared/output/fe/",
      "/dev-team/shared/output/qa/"
    ],
    "deployment_strategies": {
      "blue_green": {
        "enabled": true,
      "traffic_shift": "gradual"
      },
      "canary": {
        "enabled": true,
        "initial_traffic": "10%",
        "evaluation_period": "5m"
      },
      "rolling": {
        "enabled": true,
        "max_unavailable": "25%",
        "max_surge": "25%"
      }
    },
    "environments": {
      "development": {
        "kubernetes": true,
        "namespace": "dev",
        "replicas": 1,
        "auto_scaling": false
      },
      "staging": {
        "kubernetes": true,
        "namespace": "staging",
        "replicas": 2,
        "auto_scaling": true,
        "min_replicas": 2,
        "max_replicas": 4
      },
      "production": {
        "kubernetes": true,
        "namespace": "prod",
        "replicas": 3,
        "auto_scaling": true,
        "min_replicas": 3,
        "max_replicas": 10
      }
    },
    "monitoring": {
      "prometheus": {
        "enabled": true,
        "url": "http://prometheus:9090"
      },
      "grafana": {
        "enabled": true,
        "url": "http://grafana:3000"
      },
      "alerting": {
        "enabled": true,
        "providers": ["slack", "email", "pagerduty"]
      }
    },
    "security": {
      "image_scanning": {
        "enabled": true,
        "tool": "trivy",
        "fail_on_critical": true
      },
      "secret_management": {
        "enabled": true,
        "tool": "vault",
        "auto_rotation": true
      }
    }
  }
}
```

### 部署脚本模板
```yaml
# deployment-template.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Values.appName }}
  namespace: {{ .Values.namespace }}
spec:
  replicas: {{ .Values.replicas }}
  selector:
    matchLabels:
      app: {{ .Values.appName }}
  template:
    metadata:
      labels:
        app: {{ .Values.appName }}
        version: {{ .Values.version }}
    spec:
      containers:
      - name: {{ .Values.appName }}
        image: {{ .Values.image.repository }}:{{ .Values.image.tag }}
        ports:
        - containerPort: {{ .Values.service.port }}
        resources:
          requests:
            memory: "{{ .Values.resources.requests.memory }}"
            cpu: "{{ .Values.resources.requests.cpu }}"
          limits:
            memory: "{{ .Values.resources.limits.memory }}"
            cpu: "{{ .Values.resources.limits.cpu }}"
        livenessProbe:
          httpGet:
            path: /health
            port: {{ .Values.service.port }}
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: {{ .Values.service.port }}
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: {{ .Values.appName }}-service
  namespace: {{ .Values.namespace }}
spec:
  selector:
    app: {{ .Values.appName }}
  ports:
  - port: {{ .Values.service.port }}
    targetPort: {{ .Values.service.port }}
  type: {{ .Values.service.type }}
```

## 集成点

### 1. 与 QA Agent 集成
- 接收 QA 测试报告作为部署门禁
- 自动化部署验证测试
- 质量指标监控

### 2. 与 BE/FE 集成
- 自动化构建和部署代码变更
- 环境配置管理
- 版本发布管理

### 3. 与监控系统集成
- 自动配置监控和告警
- 性能指标收集
- 日志聚合配置

### 4. 与安全系统集成
- 安全扫描集成
- 合规性检查
- 漏洞管理

## 部署策略

### 蓝绿部署
**优点**: 零停机、快速回滚
**流程**:
1. 部署新版本到绿色环境
2. 测试绿色环境
3. 切换流量到绿色环境
4. 保留蓝色环境用于回滚

### 金丝雀发布
**优点**: 风险可控、渐进式发布
**流程**:
1. 向小部分用户发布新版本
2. 监控关键指标
3. 逐步扩大发布范围
4. 全量发布或回滚

### 滚动更新
**优点**: 资源高效、简单易用
**流程**:
1. 逐步替换旧版本实例
2. 保持服务可用性
3. 自动健康检查
4. 失败时自动回滚

## 监控指标

### 应用指标
- **响应时间**: P50, P95, P99
- **错误率**: HTTP 错误率、业务错误率
- **吞吐量**: 请求/秒、事务/秒
- **资源使用**: CPU、内存、磁盘、网络

### 业务指标
- **用户活跃度**: DAU、MAU
- **转化率**: 注册转化、购买转化
- **收入指标**: GMV、ARPU
- **用户体验**: 页面加载时间、交互延迟

### 基础设施指标
- **节点状态**: 节点数量、健康状态
- **资源利用率**: CPU、内存、存储使用率
- **网络指标**: 带宽、延迟、丢包率
- **存储指标**: IOPS、吞吐量、延迟

## 告警策略

### 告警级别
- **P0 (紧急)**: 服务完全不可用
- **P1 (高)**: 核心功能受影响
- **P2 (中)**: 非核心功能问题
- **P3 (低)**: 信息性告警

### 告警规则
```yaml
groups:
- name: application_alerts
  rules:
  - alert: HighErrorRate
    expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.05
    for: 2m
    labels:
      severity: critical
    annotations:
      summary: "高错误率检测"
      description: "错误率超过5%，当前值 {{ $value }}"
  
  - alert: HighLatency
    expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 1
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "高延迟检测"
      description: "P95延迟超过1秒，当前值 {{ $value }}s"
```

## 灾难恢复

### 备份策略
- **数据库备份**: 每日全量 + 每小时增量
- **配置文件备份**: 版本控制 + 定期备份
- **镜像备份**: 镜像仓库复制

### 恢复流程
1. **评估影响**: 确定影响范围和优先级
2. **切换流量**: 切换到备用环境
3. **数据恢复**: 从备份恢复数据
4. **服务恢复**: 重启受影响服务
5. **验证功能**: 验证核心功能
6. **根本原因分析**: 分析问题原因

### 恢复时间目标 (RTO)
- **关键服务**: ≤ 15分钟
- **重要服务**: ≤ 1小时
- **一般服务**: ≤ 4小时

### 恢复点目标 (RPO)
- **关键数据**: ≤ 5分钟
- **重要数据**: ≤ 1小时
- **一般数据**: ≤ 24小时

## 最佳实践

### 基础设施管理
1. **不可变基础设施**: 使用容器和镜像
2. **配置即代码**: 所有配置版本控制
3. **自动化测试**: 基础设施变更测试
4. **环境一致性**: 保持环境一致性

### 部署管理
1. **版本控制**: 所有部署版本化
2. **回滚计划**: 每个部署都有回滚计划
3. **渐进式发布**: 使用金丝雀或蓝绿部署
4. **监控验证**: 部署后立即监控验证

### 安全管理
1. **最小权限原则**: 仅授予必要权限
2. **秘密管理**: 使用专门的秘密管理工具
3. **安全扫描**: 集成安全扫描到流水线
4. **合规审计**: 定期安全审计

### 成本优化
1. **资源优化**: 合理配置资源规格
2. **自动伸缩**: 基于负载自动伸缩
3. **闲置资源清理**: 定期清理闲置资源
4. **成本监控**: 实时成本监控和告警