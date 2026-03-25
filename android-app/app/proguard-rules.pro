# Paper Learning Assistant ProGuard 混淆规则
# 优化 APK 大小并保护代码

# ============================================
# 基础配置
# ============================================

# 保留行号信息，便于崩溃分析
-keepattributes SourceFile,LineNumberTable

# 保留泛型信息
-keepattributes Signature

# 保留注解
-keepattributes *Annotation*

# ============================================
# Kotlin 配置
# ============================================

# 保留 Kotlin 元数据
-keep class kotlin.Metadata { *; }

# 保留 Kotlin 协程相关
-keepclassmembers class **$WhenMappings {
    <fields>;
}

-keepclassmembers class kotlin.coroutines.jvm.internal.CoroutinesKt {
    public static void ***;
}

# 保留 Kotlin 数据类
-keepclassmembers class ** {
    *** get*();
    void set*(***);
}

# ============================================
# AndroidX 和 Jetpack 组件
# ============================================

# 保留 Room 实体和 DAO
-keep class com.paperlearning.assistant.data.model.** { *; }
-keep class com.paperlearning.assistant.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class * extends androidx.room.RoomDatabase {
    abstract *;
}

# 保留 Room 内部类
-keepclassmembers class * extends androidx.room.RoomDatabase {
    *** *Dao();
}

# 保留 Lifecycle 组件
-keep class * extends androidx.lifecycle.LifecycleObserver { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# 保留 ViewModel 工厂
-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ============================================
# Hilt 依赖注入
# ============================================

# 保留 Hilt 生成的类
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }

# 保留 Hilt 注解
-keep class javax.inject.** { *; }
-keep class * extends javax.inject.Inject { *; }

# 保留 @HiltAndroidApp 标记的 Application
-keep class com.paperlearning.assistant.PaperLearningApp { *; }

# 保留 Hilt Worker
-keep class * extends androidx.hilt.work.HiltWorker { *; }

# ============================================
# Jetpack Compose
# ============================================

# 保留 Compose 相关
-keep class androidx.compose.** { *; }
-keep class * implements androidx.compose.runtime.Composable { *; }

# 保留 Compose 编译器生成的类
-keepclassmembers class **$Companion {
    public static *** getInstance();
}

# ============================================
# Retrofit 和 OkHttp
# ============================================

# 保留 Retrofit 接口
-keep interface com.paperlearning.assistant.data.remote.** { *; }

# 保留 Retrofit 数据类
-keep class com.paperlearning.assistant.data.remote.** { *; }

# 保留 OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# 保留 Gson 转换
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ============================================
# 数据模型 (不混淆)
# ============================================

# 保留所有数据模型类
-keep class com.paperlearning.assistant.data.model.** { *; }
-keep class com.paperlearning.assistant.domain.model.** { *; }

# 保留枚举类
-keepclassmembers enum com.paperlearning.assistant.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# UseCase 和 Repository
# ============================================

# 保留 UseCase 类
-keep class com.paperlearning.assistant.domain.usecase.** { *; }

# 保留 Repository 类
-keep class com.paperlearning.assistant.data.repository.** { *; }

# ============================================
# ViewModel
# ============================================

# 保留 ViewModel 类
-keep class com.paperlearning.assistant.viewmodel.** { *; }

# ============================================
# UI 组件
# ============================================

# 保留 UI 屏幕类
-keep class com.paperlearning.assistant.ui.screens.** { *; }

# 保留 UI 组件类
-keep class com.paperlearning.assistant.ui.components.** { *; }

# 保留导航类
-keep class com.paperlearning.assistant.ui.navigation.** { *; }

# ============================================
# 工具类
# ============================================

# 保留工具类
-keep class com.paperlearning.assistant.util.** { *; }

# ============================================
# 测试相关
# ============================================

# 不混淆测试代码
-keep class com.paperlearning.assistant.**Test { *; }
-keep class * extends junit.framework.TestCase { *; }
-keep class * extends org.junit.** { *; }

# ============================================
# 第三方库
# ============================================

# Coil 图片加载
-keep class io.coil-kt.** { *; }

# WorkManager
-keep class androidx.work.** { *; }

# ============================================
# 优化规则
# ============================================

# 移除日志代码 (可选，发布时启用)
# -assumenosideeffects class android.util.Log {
#     public static *** d(...);
#     public static *** v(...);
#     public static *** i(...);
# }

# 优化枚举
-optimizations !code/simplification/enum

# 保留 Native 方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留序列化
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
