# GitHub Actions CI/CD 配置指南

## 📋 工作流说明

### 1. Android Build (android-build.yml)

**触发条件**：
- Push 到 `main` 或 `develop` 分支
- Pull Request 到 `main` 分支

**执行内容**：
1. ✅ 检出代码
2. ✅ 设置 JDK 17
3. ✅ 设置 Gradle 8.2
4. ✅ 构建 Debug APK
5. ✅ 运行单元测试
6. ✅ 上传 Debug APK（保留 30 天）
7. ✅ 上传测试报告（保留 7 天）

**查看构建**：https://github.com/feihu1991/paper-learning-assistant/actions

---

### 2. Android Release (android-release.yml)

**触发条件**：
- 创建版本标签（如 `v1.0.0`、`v1.0.1`）

**执行内容**：
1. ✅ 检出代码
2. ✅ 设置 JDK 17
3. ✅ 解码签名密钥库（从 Secrets）
4. ✅ 构建 Release APK（带签名）
5. ✅ 运行测试
6. ✅ 创建 GitHub Release
7. ✅ 上传 Release APK
8. ✅ 上传 ProGuard 映射文件

---

## 🔐 配置 GitHub Secrets

### 步骤 1: 生成签名密钥库

```bash
# 在本地生成 release.keystore
keytool -genkey -v -keystore release.keystore \
  -alias paper-learning \
  -keyalg RSA -keysize 2048 -validity 10000
```

### 步骤 2: 将密钥库转换为 Base64

```bash
# Linux/Mac
base64 release.keystore > release.keystore.base64

# Windows (PowerShell)
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) > release.keystore.base64
```

### 步骤 3: 添加到 GitHub Secrets

访问：https://github.com/feihu1991/paper-learning-assistant/settings/secrets/actions

添加以下 Secrets：

| Secret 名称 | 值 | 说明 |
|------------|-----|------|
| `RELEASE_KEYSTORE` | 步骤 2 生成的 Base64 字符串 | 签名密钥库 |
| `RELEASE_STORE_PASSWORD` | 你的密钥库密码 | 密钥库密码 |
| `RELEASE_KEY_ALIAS` | `paper-learning` | 密钥别名 |
| `RELEASE_KEY_PASSWORD` | 你的密钥密码 | 密钥密码 |

---

## 🚀 发布新版本

### 步骤 1: 更新版本号

在 `android-app/app/build.gradle.kts` 中更新：

```kotlin
android {
    defaultConfig {
        versionCode = 2  // 递增
        versionName = "1.0.1"  // 语义化版本
    }
}
```

### 步骤 2: 提交并推送

```bash
git add .
git commit -m "chore: bump version to 1.0.1"
git push origin main
```

### 步骤 3: 创建并推送标签

```bash
# 创建标签
git tag v1.0.1

# 推送标签（触发 Release 构建）
git push origin v1.0.1
```

### 步骤 4: 等待自动构建

GitHub Actions 会自动：
1. 构建 Release APK
2. 创建 GitHub Release
3. 上传 APK 到 Release

访问：https://github.com/feihu1991/paper-learning-assistant/releases

---

## 📱 本地测试构建

### Debug APK

```bash
cd android-app
./gradlew assembleDebug

# APK 位置
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK（本地）

```bash
# 先在本地创建 keystore.properties
cat > keystore.properties << EOF
RELEASE_STORE_FILE=release.keystore
RELEASE_STORE_PASSWORD=your_password
RELEASE_KEY_ALIAS=paper-learning
RELEASE_KEY_PASSWORD=your_key_password
EOF

# 构建
./gradlew assembleRelease

# APK 位置
app/build/outputs/apk/release/app-release.apk
```

---

## 🔍 故障排查

### 构建失败

1. 检查 GitHub Actions 日志
2. 确认 JDK 版本正确
3. 确认 Gradle 版本兼容

### 签名失败

1. 检查 Secrets 是否正确配置
2. 确认 Base64 编码正确
3. 验证密钥库密码

### 测试失败

1. 查看测试报告 artifact
2. 本地运行测试：`./gradlew test`
3. 修复失败的测试用例

---

## 📊 构建产物

| 类型 | 保留期 | 下载位置 |
|------|--------|----------|
| Debug APK | 30 天 | Actions → Artifacts |
| Test Report | 7 天 | Actions → Artifacts |
| Release APK | 永久 | Releases 页面 |
| Mapping 文件 | 永久 | Releases 页面 |

---

## ✅ 检查清单

- [ ] 已配置 JDK 17
- [ ] 已配置 Gradle 8.2
- [ ] 已生成签名密钥库
- [ ] 已添加 GitHub Secrets
- [ ] 已测试 Debug 构建
- [ ] 已测试 Release 构建
- [ ] 已创建第一个版本标签

---

**配置完成！** 🎉

现在每次 push 都会自动构建，创建标签会自动发布 Release！
