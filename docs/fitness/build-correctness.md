---
dimension: Build Correctness
weight: 0.4
tier: fast
threshold: 100
description: 构建正确性检查 - 确保代码能编译通过
metrics:
  - name: compile_pass
    description: Kotlin 代码编译通过
    type: boolean
    required: true
    
  - name: no_compile_errors
    description: 没有编译错误
    type: boolean
    required: true
    
  - name: resource_valid
    description: Android 资源文件有效
    type: boolean
    required: true

evidence:
  - ./gradlew assembleDebug 输出
  - ./gradlew processDebugResources 输出
---

# 构建正确性 Fitness Function

## 检查项

### 1. 编译检查
```bash
./gradlew assembleDebug --no-daemon
```

### 2. 资源处理检查
```bash
./gradlew processDebugResources
```

## 通过标准

- ✅ 编译无错误
- ✅ 资源处理成功
- ✅ APK 生成成功

## 失败处理

如果构建失败：
1. 查看错误日志
2. 修复编译错误
3. 重新构建
4. 如果是依赖问题，检查网络或降级版本
