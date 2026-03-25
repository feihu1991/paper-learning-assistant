# Paper Learning Assistant 部署与测试指南

> **最后更新**: 2026-03-25  
> **目标设备**: 红米 K70 (Redmi K70)  
> **Android 版本**: Android 14 / HyperOS 1.0  
> **硬件配置**: 骁龙 8 Gen 2, 12GB RAM, 256GB 存储

---

## 目录

1. [环境准备](#环境准备)
2. [构建 APK](#构建-apk)
3. [红米 K70 真机测试](#红米-k70-真机测试)
4. [性能优化配置](#性能优化配置)
5. [问题排查](#问题排查)
6. [发布准备](#发布准备)

---

## 环境准备

### 系统要求

| 组件 | 最低版本 | 推荐版本 |
|------|---------|---------|
| JDK | 17 | 17.0.9+ |
| Android SDK | 34 | 34 |
| Android Studio | Hedgehog | Hedgehog / Iguana |
| Gradle | 8.2 | 8.4 |
| Kotlin | 1.9.20 | 1.9.20 |

### 安装步骤

```bash
# 1. 验证 JDK
java -version
# 应显示：java version "17.0.x"

# 2. 设置环境变量 (如未配置)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# 3. 验证 Android SDK
sdkmanager --list
```

### 项目依赖

```bash
cd /home/admin/openclaw/workspace/paper-tutor/android-app

# 接受 SDK 许可证
sdkmanager --licenses

# 安装必要的 SDK 组件
sdkmanager "platforms;android-34"
sdkmanager "build-tools;34.0.0"
sdkmanager "platform-tools"
```

---

## 构建 APK

### Debug 版本 (开发测试)

```bash
cd /home/admin/openclaw/workspace/paper-tutor/android-app

# 清理并构建 Debug APK
./gradlew clean assembleDebug

# 输出位置
# app/build/outputs/apk/debug/app-debug.apk
```

### Release 版本 (生产发布)

```bash
# 1. 配置签名 (首次需要)
# 创建 keystore.properties
echo "storePassword=your_store_password" > keystore.properties
echo "keyPassword=your_key_password" >> keystore.properties
echo "keyAlias=your_key_alias" >> keystore.properties
echo "storeFile=../keystore.jks" >> keystore.properties

# 2. 构建 Release APK
./gradlew clean assembleRelease

# 输出位置
# app/build/outputs/apk/release/app-release.apk
```

### 构建优化版本 (启用 ProGuard)

```bash
# 1. 修改 build.gradle.kts
# 将 isMinifyEnabled 设置为 true

# 2. 构建
./gradlew clean assembleRelease

# 查看 APK 大小
ls -lh app/build/outputs/apk/release/
```

---

## 红米 K70 真机测试

### 设备准备

#### 1. 启用开发者选项

1. 进入 **设置** → **我的设备** → **全部参数**
2. 连续点击 **OS 版本号** 7 次
3. 返回 **设置** → **更多设置** → **开发者选项**

#### 2. 启用 USB 调试

1. 进入 **开发者选项**
2. 开启 **USB 调试**
3. 开启 **USB 调试 (安全设置)** (允许修改权限)
4. 开启 **关闭 USB 安装验证** (可选，避免安装拦截)

#### 3. 连接电脑

```bash
# 使用 USB 线连接红米 K70 到电脑

# 验证设备连接
adb devices

# 应显示:
# List of devices attached
# xxxxxxxx    device
```

### 安装应用

```bash
# 方法 1: 直接安装 APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 方法 2: 通过 Gradle 安装
./gradlew installDebug

# 方法 3: 安装 Release 版本
adb install -r app/build/outputs/apk/release/app-release.apk
```

### 测试检查清单

#### 基础功能测试

- [ ] **应用启动**: 冷启动时间 < 2 秒
- [ ] **首页加载**: 论文列表正常显示
- [ ] **搜索功能**: 可以搜索论文
- [ ] **PDF 导入**: 从本地选择 PDF 文件
- [ ] **论文解析**: 解析进度正常显示
- [ ] **学习路径**: 生成学习步骤
- [ ] **进度保存**: 关闭应用后进度保留

#### 性能测试

```bash
# 1. 测量启动时间
adb shell am start -W com.paperlearning.assistant/.MainActivity

# 2. 监控内存使用
adb shell dumpsys meminfo com.paperlearning.assistant

# 3. 检查 CPU 使用率
adb shell top -m 10 | grep paperlearning

# 4. 查看电池消耗
adb shell dumpsys batterystats | grep com.paperlearning.assistant
```

#### UI 测试

```bash
# 运行 UI 自动化测试
./gradlew connectedAndroidTest

# 查看测试报告
# app/build/reports/androidTests/connected/index.html
```

### 红米 K70 特定优化

#### 1. 高性能模式

红米 K70 搭载骁龙 8 Gen 2，建议：

- 在 **设置** → **省电与电池** → **性能模式** 中选择 **均衡模式**
- 测试时避免 **省电模式**，可能限制 CPU 频率

#### 2. 内存优化

```kotlin
// 在 Application 中配置大堆
android:largeHeap="true"
```

红米 K70 有 12GB RAM，可以充分利用：

- PDF 解析时分配更多内存
- 缓存更多论文数据

#### 3. 存储权限

HyperOS 1.0 (Android 14) 的存储权限更严格：

- 使用 **文件选择器** 而非直接请求存储权限
- PDF 文件保存到应用私有目录

---

## 性能优化配置

### 1. ProGuard 混淆

已在 `app/proguard-rules.pro` 中配置：

- 保留数据模型类 (不混淆)
- 保留 Room 实体和 DAO
- 保留 Hilt 依赖注入类
- 保留 Retrofit 接口
- 移除调试日志 (可选)

### 2. 构建优化

在 `app/build.gradle.kts` 中启用：

```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### 3. 图片优化

使用 Coil 加载图片时：

```kotlin
ImageRequest.Builder(context)
    .memoryCachePolicy(CachePolicy.ENABLED)
    .diskCachePolicy(CachePolicy.ENABLED)
    .size(200) // 限制图片大小
    .build()
```

### 4. 数据库优化

Room 查询优化：

```kotlin
// 使用 Flow 而非 suspend 函数，避免重复查询
@Query("SELECT * FROM papers ORDER BY createdAt DESC")
fun getAll(): Flow<List<PaperEntity>>

// 添加索引
@Entity(indices = [Index(value = ["arxivId"], unique = true)])
data class PaperEntity(...)
```

### 5. 网络优化

Retrofit + OkHttp 配置：

```kotlin
OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()
```

---

## 问题排查

### 常见问题

#### 1. 安装失败

```bash
# 错误：INSTALL_FAILED_UPDATE_INCOMPATIBLE
adb uninstall com.paperlearning.assistant
adb install -r app-debug.apk

# 错误：INSTALL_PARSE_FAILED_NO_CERTIFICATES
# 重新签名 APK
```

#### 2. 应用崩溃

```bash
# 查看日志
adb logcat | grep -i paperlearning

# 过滤崩溃日志
adb logcat | grep -E "FATAL|CRASH|AndroidRuntime"

# 导出完整日志
adb logcat -d > crash-log.txt
```

#### 3. 内存泄漏

```bash
# 使用 LeakCanary (开发版本)
# 自动检测内存泄漏并通知

# 手动检查
adb shell dumpsys meminfo com.paperlearning.assistant
```

#### 4. 网络请求失败

```bash
# 检查网络权限
adb shell dumpsys package com.paperlearning.assistant | grep permission

# 测试 API 连接
curl https://api.openai.com/v1/models -H "Authorization: Bearer YOUR_KEY"
```

### 性能分析工具

#### Android Studio Profiler

1. 打开 **Android Studio** → **View** → **Tool Windows** → **Profiler**
2. 选择设备和应用
3. 监控 **CPU**、**Memory**、**Network**、**Energy**

#### Perfetto

```bash
# 启动系统跟踪
adb shell perfetto -c /data/local/tmp/trace_config.pb -o /data/local/tmp/trace.perfetto-trace

# 停止跟踪 (Ctrl+C)
# 导出文件分析
adb pull /data/local/tmp/trace.perfetto-trace
```

---

## 发布准备

### 1. 版本管理

在 `app/build.gradle.kts` 中更新：

```kotlin
defaultConfig {
    versionCode = 1      // 每次发布递增
    versionName = "1.0.0" // 语义化版本
}
```

### 2. 签名配置

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 3. 生成发布包

```bash
# 构建 Release APK
./gradlew clean assembleRelease

# 验证 APK
apksigner verify --verbose app-release.apk

# 查看 APK 信息
aapt dump badging app-release.apk
```

### 4. 发布渠道

- **GitHub Releases**: 上传 APK 和 Release Notes
- **Google Play**: 通过 Play Console 上传
- **内部测试**: 通过 Firebase App Distribution

---

## 附录

### A. 红米 K70 规格

| 参数 | 规格 |
|------|------|
| 处理器 | 高通骁龙 8 Gen 2 |
| RAM | 12GB / 16GB LPDDR5X |
| 存储 | 256GB / 512GB / 1TB UFS 4.0 |
| 屏幕 | 6.67" 2K AMOLED, 120Hz |
| 电池 | 5000mAh, 120W 快充 |
| 系统 | HyperOS 1.0 (Android 14) |

### B. 常用 ADB 命令

```bash
# 设备管理
adb devices              # 列出设备
adb reboot               # 重启设备
adb reboot bootloader    # 进入 Fastboot

# 应用管理
adb install app.apk      # 安装应用
adb uninstall pkg.name   # 卸载应用
adb shell pm clear pkg   # 清除应用数据

# 文件传输
adb push local remote    # 上传文件
adb pull remote local    # 下载文件

# 日志
adb logcat               # 查看日志
adb logcat -c            # 清除日志
adb bugreport            # 生成完整报告
```

### C. 参考文档

- [Android 开发者文档](https://developer.android.com/)
- [ProGuard 用户指南](https://www.guardsquare.com/proguard)
- [红米开发者社区](https://dev.mi.com/)
- [Jetpack Compose 文档](https://developer.android.com/jetpack/compose)

---

**文档维护**: 龙虾王 · 严谨专业版  
**最后审查**: 2026-03-25
