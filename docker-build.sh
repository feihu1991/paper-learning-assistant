#!/bin/bash
# Android 项目 Docker 构建脚本

set -e

echo "🔨 使用 Docker 构建 Android 项目"
echo "=================================="

# 创建临时构建目录
BUILD_DIR=$(mktemp -d)
echo "📁 构建目录：$BUILD_DIR"

# 复制项目文件
cp -r android-app/* $BUILD_DIR/

# 使用官方 Android Docker 镜像
docker run --rm \
  -v $BUILD_DIR:/home/gradle/project \
  -w /home/gradle/project \
  gradle:8.2-jdk17 \
  gradle assembleDebug --no-daemon --stacktrace

# 复制 APK 到输出目录
if [ -f "$BUILD_DIR/app/build/outputs/apk/debug/app-debug.apk" ]; then
  echo "✅ 构建成功！"
  mkdir -p output
  cp $BUILD_DIR/app/build/outputs/apk/debug/app-debug.apk output/
  echo "📦 APK 位置：$(pwd)/output/app-debug.apk"
else
  echo "❌ 构建失败"
  exit 1
fi

# 清理
rm -rf $BUILD_DIR
