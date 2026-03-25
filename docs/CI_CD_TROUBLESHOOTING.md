# CI/CD 故障排查指南

## 获取构建错误日志

### 方法 1: GitHub Actions 自动上传（推荐）

构建失败时，错误日志会自动上传为 Artifact：

1. 访问：https://github.com/feihu1991/paper-learning-assistant/actions
2. 点击失败的构建运行
3. 在页面底部找到 "Artifacts" 部分
4. 下载 `error-logs.zip`

### 方法 2: 使用辅助脚本

```bash
# 获取最新构建的错误日志
cd /home/admin/openclaw/workspace/paper-tutor
./scripts/get-build-logs.sh feihu1991/paper-learning-assistant <RUN_ID>

# 示例
./scripts/get-build-logs.sh feihu1991/paper-learning-assistant 23529321416
```

### 方法 3: 手动 curl 命令

```bash
# 使用 Token 认证（避免限流）
curl -H "Authorization: token ghp_YOUR_TOKEN" \
  https://api.github.com/repos/feihu1991/paper-learning-assistant/actions/runs/<RUN_ID>/logs \
  -o error.log
```

---

## 常见错误及解决方案

### 1. gradlew: Syntax error

**错误**: `./gradlew: 176: Syntax error: redirection unexpected`

**原因**: gradlew 脚本格式错误

**解决**:
```bash
# 使用极简脚本
cat > android-app/gradlew << 'EOF'
#!/bin/sh
exec java -Dorg.gradle.appname=gradlew -classpath "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
EOF
chmod +x android-app/gradlew
```

### 2. ClassNotFoundException: GradleWrapperMain

**错误**: `Could not find or load main class org.gradle.wrapper.GradleWrapperMain`

**原因**: 缺少 gradle-wrapper.jar

**解决**:
```bash
# 下载官方 wrapper
curl -L "https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar" \
  -o android-app/gradle/wrapper/gradle-wrapper.jar

# 强制添加到 Git（因为 .gitignore 忽略了 *.jar）
git add -f android-app/gradle/wrapper/gradle-wrapper.jar
git commit -m "feat: add gradle-wrapper.jar"
git push
```

### 3. Cache service responded with 400

**错误**: `Failed to save cache entry... Cache service responded with 400`

**原因**: GitHub 缓存服务暂时不可用

**解决**: 禁用 Gradle 缓存
```yaml
- name: Setup Gradle
  uses: gradle/actions/setup-gradle@v3
  with:
    gradle-version: '8.2'
    cache-disabled: true
```

或在环境变量中禁用：
```yaml
env:
  GRADLE_OPTS: -Dorg.gradle.daemon=false -Dorg.gradle.caching=false
```

### 4. API 限流 (403 Rate limit exceeded)

**错误**: `API rate limit exceeded`

**原因**: 匿名请求或频繁调用 API

**解决**:
1. 使用 GITHUB_TOKEN 认证
2. 添加重试机制
3. 减少 API 调用频率

```bash
# 使用 Token
curl -H "Authorization: token $GITHUB_TOKEN" ...

# 重试机制
for i in 1 2 3; do
  curl ... && break
  sleep $((i*10))
done
```

---

## 构建配置最佳实践

### 1. 简化工作流

```yaml
- name: Build with Gradle
  run: |
    cd android-app
    ./gradlew assembleDebug --no-daemon --stacktrace
```

### 2. 禁用缓存（避免服务问题）

```yaml
env:
  GRADLE_OPTS: -Dorg.gradle.daemon=false -Dorg.gradle.caching=false
```

### 3. 自动上传错误日志

```yaml
- name: Download error logs on failure
  if: failure()
  run: |
    curl -H "Authorization: token ${{ secrets.GITHUB_TOKEN }}" \
      https://api.github.com/repos/${{ github.repository }}/actions/runs/${{ github.run_id }}/logs \
      -o error.log
      
- name: Upload error logs
  if: failure()
  uses: actions/upload-artifact@v4
  with:
    name: error-logs
    path: error.log
```

---

## 快速检查清单

- [ ] gradlew 脚本是 2 行极简版本
- [ ] gradle-wrapper.jar 存在（63KB）
- [ ] Gradle 缓存已禁用
- [ ] 工作流配置正确
- [ ] 错误日志自动上传

---

## 联系支持

如果问题持续，请提供：
1. 构建运行 ID
2. 完整错误日志
3. 最近的提交记录
