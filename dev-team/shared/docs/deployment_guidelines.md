# 部署指南

## 部署原则

### 1. 自动化优先
- 所有部署流程自动化
- 减少人工干预
- 提高部署一致性和可靠性

### 2. 渐进式发布
- 使用蓝绿部署或金丝雀发布
- 降低发布风险
- 快速回滚能力

### 3. 监控驱动
- 部署前后监控验证
- 关键指标告警
- 实时部署状态跟踪

### 4. 安全合规
- 安全扫描集成
- 合规性检查
- 权限最小化原则

## 部署环境

### 环境分类
| 环境 | 用途 | 特点 | 访问控制 |
|------|------|------|----------|
| 开发环境 | 功能开发测试 | 快速迭代、调试友好 | 开发团队 |
| 测试环境 | 集成测试 | 稳定、接近生产 | QA团队 |
| 预生产环境 | 生产前验证 | 与生产完全一致 | 运维团队 |
| 生产环境 | 线上服务 | 高可用、高性能 | 严格权限控制 |

### 环境配置管理
```yaml
# environments.yaml
environments:
  development:
    replicas: 1
    resources:
      requests:
        cpu: "100m"
        memory: "256Mi"
      limits:
        cpu: "500m"
        memory: "512Mi"
    auto_scaling: false
    
  staging:
    replicas: 2
    resources:
      requests:
        cpu: "200m"
        memory: "512Mi"
      limits:
        cpu: "1000m"
        memory: "1Gi"
    auto_scaling:
      min_replicas: 2
      max_replicas: 4
      
  production:
    replicas: 3
    resources:
      requests:
        cpu: "500m"
        memory: "1Gi"
      limits:
        cpu: "2000m"
        memory: "2Gi"
    auto_scaling:
      min_replicas: 3
      max_replicas: 10
```

## 部署流程

### 1. 预部署检查
```bash
# 检查清单
- [ ] 代码通过所有测试
- [ ] 安全扫描通过
- [ ] 性能测试通过
- [ ] 文档更新完成
- [ ] 回滚计划准备
- [ ] 团队通知发送
```

### 2. 构建阶段
```yaml
# GitHub Actions 示例
name: Build and Deploy

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2
      
    - name: Login to DockerHub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}
        
    - name: Build and push
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: |
          ${{ secrets.DOCKER_USERNAME }}/app:${{ github.sha }}
          ${{ secrets.DOCKER_USERNAME }}/app:latest
```

### 3. 部署阶段
```yaml
# Kubernetes 部署示例
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-deployment
  namespace: production
  labels:
    app: myapp
    version: v1.2.3
spec:
  replicas: 3
  selector:
    matchLabels:
      app: myapp
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  template:
    metadata:
      labels:
        app: myapp
        version: v1.2.3
    spec:
      containers:
      - name: app
        image: myregistry/app:v1.2.3
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        env:
        - name: NODE_ENV
          value: "production"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: database-url
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 5
```

### 4. 验证阶段
```bash
# 部署验证脚本
#!/bin/bash

# 检查部署状态
kubectl rollout status deployment/app-deployment -n production

# 检查Pod状态
kubectl get pods -n production -l app=myapp

# 检查服务端点
SERVICE_IP=$(kubectl get service app-service -n production -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
curl -f http://$SERVICE_IP:8080/health

# 检查日志
kubectl logs -n production -l app=myapp --tail=50

# 性能测试
k6 run --vus 10 --duration 30s test.js
```

### 5. 监控阶段
```yaml
# Prometheus 监控配置
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: app-monitor
  namespace: monitoring
spec:
  selector:
    matchLabels:
      app: myapp
  endpoints:
  - port: web
    interval: 30s
    path: /metrics
```

## 部署策略

### 蓝绿部署
```yaml
# 蓝绿部署配置
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: app-ingress
  annotations:
    nginx.ingress.kubernetes.io/canary: "true"
    nginx.ingress.kubernetes.io/canary-weight: "0"
spec:
  rules:
  - host: app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: app-blue
            port:
              number: 80
---
# 切换流量到绿色
kubectl patch ingress app-ingress \
  -p '{"metadata":{"annotations":{"nginx.ingress.kubernetes.io/canary-weight":"100"}}}'
```

### 金丝雀发布
```yaml
# 金丝雀发布配置
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-canary
spec:
  replicas: 1  # 10% 流量
  selector:
    matchLabels:
      app: myapp
      track: canary
  template:
    metadata:
      labels:
        app: myapp
        track: canary
    spec:
      containers:
      - name: app
        image: myregistry/app:v1.2.3-canary
---
# 流量分割
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: app-virtualservice
spec:
  hosts:
  - app.example.com
  http:
  - route:
    - destination:
        host: app
        subset: stable
      weight: 90
    - destination:
        host: app
        subset: canary
      weight: 10
```

## 回滚机制

### 自动回滚条件
```yaml
# 自动回滚配置
apiVersion: argoproj.io/v1alpha1
kind: Rollout
metadata:
  name: app-rollout
spec:
  replicas: 3
  strategy:
    canary:
      steps:
      - setWeight: 20
      - pause: {duration: 10m}
      - setWeight: 40
      - pause: {duration: 10m}
      - setWeight: 60
      - pause: {duration: 10m}
      - setWeight: 80
      - pause: {duration: 10m}
  revisionHistoryLimit: 10
  rollbackWindow:
    revisions: 5
```

### 手动回滚流程
```bash
# 1. 检查当前版本
kubectl get deployment app-deployment -o=jsonpath='{.spec.template.spec.containers[0].image}'

# 2. 回滚到上一个版本
kubectl rollout undo deployment/app-deployment

# 3. 验证回滚
kubectl rollout status deployment/app-deployment

# 4. 检查服务状态
kubectl get pods -l app=myapp
curl http://app.example.com/health
```

## 监控和告警

### 关键监控指标
```promql
# 响应时间监控
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))

# 错误率监控
rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m])

# 资源使用监控
container_memory_working_set_bytes{container="app"} / container_spec_memory_limit_bytes{container="app"}

# 业务指标监控
rate(order_created_total[5m])
```

### 告警规则
```yaml
# 紧急告警
- alert: AppDown
  expr: up{job="app"} == 0
  for: 1m
  labels:
    severity: critical
  annotations:
    summary: "应用不可用"
    
- alert: HighErrorRate
  expr: rate(http_requests_total{status=~"5.."}[5m]) / rate(http_requests_total[5m]) > 0.05
  for: 2m
  labels:
    severity: critical
    
# 警告告警  
- alert: HighLatency
  expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 1
  for: 5m
  labels:
    severity: warning
```

## 安全部署

### 安全扫描
```bash
# 容器镜像扫描
trivy image myregistry/app:v1.2.3

# 依赖漏洞扫描
npm audit
snyk test

# 基础设施安全扫描
checkov -d .
tfsec .
```

### 秘密管理
```yaml
# Kubernetes Secrets
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  database-url: <base64-encoded>
  api-key: <base64-encoded>
  
# 使用外部秘密管理
apiVersion: external-secrets.io/v1beta1
kind: ExternalSecret
metadata:
  name: app-external-secrets
spec:
  refreshInterval: 1h
  secretStoreRef:
    name: vault-backend
    kind: SecretStore
  target:
    name: app-secrets
  data:
  - secretKey: database-url
    remoteRef:
      key: secrets/app
      property: database-url
```

## 灾难恢复

### 备份策略
```bash
# 数据库备份
pg_dump -h localhost -U postgres mydb > backup_$(date +%Y%m%d).sql

# 配置文件备份
kubectl get all -n production -o yaml > backup_$(date +%Y%m%d).yaml

# 持久卷备份
velero backup create app-backup --include-namespaces production
```

### 恢复流程
```bash
# 1. 创建恢复环境
kubectl create namespace recovery

# 2. 恢复配置
kubectl apply -f backup.yaml -n recovery

# 3. 恢复数据
psql -h localhost -U postgres mydb < backup.sql

# 4. 验证恢复
kubectl get all -n recovery
curl http://recovery.app.example.com/health
```

## 最佳实践

### 部署清单
- [ ] **代码质量**: 通过所有测试和代码检查
- [ ] **安全合规**: 通过安全扫描和合规检查
- [ ] **性能验证**: 性能测试结果符合要求
- [ ] **文档更新**: 部署文档和运行手册更新
- [ ] **团队通知**: 相关团队已收到部署通知
- [ ] **回滚准备**: 回滚计划和脚本准备就绪
- [ ] **监控就绪**: 监控和告警配置完成
- [ ] **备份完成**: 重要数据备份完成

### 部署时间窗口
- **常规部署**: 工作日 10:00-16:00
- **紧急修复**: 随时，但需要审批
- **重大变更**: 周末或夜间低峰期

### 沟通计划
1. **部署前24小时**: 发送部署通知
2. **部署前1小时**: 最终确认会议
3. **部署进行中**: 实时状态更新
4. **部署完成后**: 结果通知和总结

### 持续改进
1. **部署回顾**: 每次部署后回顾会议
2. **指标分析**: 分析部署成功率和时间
3. **流程优化**: 基于反馈优化部署流程
4. **工具改进**: 持续改进部署工具和脚本