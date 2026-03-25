# Paper Learning Assistant Android 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个完全本地存储的 Android 论文学习助手应用，支持论文搜索/导入、大模型智能解析、结构化学习路径。

**Architecture:** MVVM + Clean Architecture，完全本地存储（Room + 内部存储），Jetpack Compose UI。

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Room, Retrofit, Hilt, WorkManager, MuPDF

**测试设备:** 红米 K70（骁龙 8 Gen 2, 12GB RAM）

**设计文档:** `/home/admin/openclaw/workspace/paper-tutor/docs/superpowers/specs/2026-03-25-paper-learning-assistant-design.md`

---

## 文件结构总览

### 项目根结构
```
paper-tutor/android-app/
├── app/
│   ├── src/main/java/com/paperlearning/assistant/
│   │   ├── PaperLearningApp.kt
│   │   ├── MainActivity.kt
│   │   ├── ui/                          # UI 层（Compose）
│   │   ├── viewmodel/                   # ViewModel 层
│   │   ├── domain/                      # 领域层（UseCases）
│   │   ├── data/                        # 数据层
│   │   │   ├── local/                   # 本地存储（Room）
│   │   │   ├── remote/                  # 远程 API（arXiv, LLM）
│   │   │   ├── repository/              # 数据仓库
│   │   │   └── model/                   # 数据模型
│   │   └── util/                        # 工具类
│   ├── src/main/res/
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

### 核心文件清单

| 文件路径 | 职责 | 创建顺序 |
|---------|------|---------|
| `app/build.gradle.kts` | 依赖配置 | Phase 1 |
| `PaperLearningApp.kt` | Application 入口 + Hilt | Phase 1 |
| `data/local/PaperDatabase.kt` | Room 数据库定义 | Phase 1 |
| `data/local/dao/*.kt` | DAO 接口 | Phase 1 |
| `data/model/*.kt` | Entity/DTO 数据类 | Phase 1 |
| `data/repository/*.kt` | 数据仓库实现 | Phase 2 |
| `domain/usecase/*.kt` | 业务逻辑 | Phase 2 |
| `viewmodel/*.kt` | ViewModel | Phase 2 |
| `ui/screens/*.kt` | Compose 界面 | Phase 2-4 |
| `data/remote/*.kt` | API 客户端 | Phase 3 |

---

## Phase 1: 项目搭建 + 数据库 (Week 1)

### Task 1.1: 创建 Android 项目骨架

**Files:**
- Create: `android-app/build.gradle.kts`
- Create: `android-app/settings.gradle.kts`
- Create: `android-app/app/build.gradle.kts`
- Create: `android-app/gradle.properties`
- Create: `android-app/local.properties`

- [ ] **Step 1: 创建根 build.gradle.kts**

```kotlin
// android-app/build.gradle.kts
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
}
```

- [ ] **Step 2: 创建 settings.gradle.kts**

```kotlin
// android-app/settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "PaperLearningAssistant"
include(":app")
```

- [ ] **Step 3: 创建 app/build.gradle.kts**

```kotlin
// android-app/app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.paperlearning.assistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.paperlearning.assistant"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.0")
    
    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

kapt {
    correctErrorTypes = true
}
```

- [ ] **Step 4: 创建 gradle.properties**

```properties
# android-app/gradle.properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **Step 5: 验证项目结构**

Run: `cd /home/admin/openclaw/workspace/paper-tutor/android-app && ls -la`
Expected: 显示 build.gradle.kts, settings.gradle.kts, app/ 目录

- [ ] **Step 6: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/build.gradle.kts android-app/settings.gradle.kts android-app/app/build.gradle.kts
git commit -m "feat: setup Android project skeleton with Gradle configuration"
```

---

### Task 1.2: 创建数据模型（Entity）

**Files:**
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/model/PaperEntity.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/model/LearningStepEntity.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/model/UserProgressEntity.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/model/LlmConfigEntity.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/model/Enums.kt`
- Test: `android-app/app/src/test/java/com/paperlearning/assistant/data/model/ModelTests.kt`

- [ ] **Step 1: 编写枚举类型测试**

```kotlin
// android-app/app/src/test/java/com/paperlearning/assistant/data/model/ModelTests.kt
package com.paperlearning.assistant.data.model

import org.junit.Test
import org.junit.Assert.*

class ModelTests {
    @Test
    fun `ParseStatus values are correct`() {
        assertEquals(ParseStatus.NOT_PARSED.order, 0)
        assertEquals(ParseStatus.PARSING.order, 1)
        assertEquals(ParseStatus.COMPLETED.order, 2)
        assertEquals(ParseStatus.FAILED.order, 3)
    }
    
    @Test
    fun `LearningMode estimatedMinutes are correct`() {
        assertEquals(LearningMode.FAST.estimatedMinutes, 5)
        assertEquals(LearningMode.STANDARD.estimatedMinutes, 20)
        assertEquals(LearningMode.DEEP.estimatedMinutes, 60)
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `cd android-app && ./gradlew testDebugUnitTest --tests "com.paperlearning.assistant.data.model.ModelTests"`
Expected: FAIL with "Unresolved reference: ParseStatus"

- [ ] **Step 3: 创建枚举类型**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/model/Enums.kt
package com.paperlearning.assistant.data.model

enum class ParseStatus(val order: Int) {
    NOT_PARSED(0),
    PARSING(1),
    COMPLETED(2),
    FAILED(3)
}

enum class LearningMode(val displayName: String, val estimatedMinutes: Int) {
    FAST("快速了解", 5),
    STANDARD("标准学习", 20),
    DEEP("深入掌握", 60)
}

enum class StepType(val displayName: String) {
    BACKGROUND("研究背景"),
    PROBLEM("问题定义"),
    CORE_CONCEPT("核心概念"),
    METHOD("方法详解"),
    FORMULA("关键公式"),
    EXPERIMENT("实验分析"),
    CONCLUSION("总结"),
    QUIZ("小测验")
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `cd android-app && ./gradlew testDebugUnitTest --tests "com.paperlearning.assistant.data.model.ModelTests"`
Expected: PASS

- [ ] **Step 5: 编写 PaperEntity 测试**

```kotlin
// Add to ModelTests.kt
@Test
fun `PaperEntity creates with default values`() {
    val paper = PaperEntity(
        title = "Test Paper",
        authors = """["Author A", "Author B"]""",
        abstract = "Test abstract",
        pdfPath = "/path/to/file.pdf"
    )
    assertEquals(0L, paper.id)
    assertEquals(ParseStatus.NOT_PARSED, paper.parsedStatus)
    assertNotNull(paper.createdAt)
}
```

- [ ] **Step 6: 创建 PaperEntity**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/model/PaperEntity.kt
package com.paperlearning.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "papers")
data class PaperEntity(
    @PrimaryKey val id: Long = 0,
    val arxivId: String?,
    val title: String,
    val authors: String,  // JSON array string
    val abstract: String,
    val pdfPath: String,
    val parsedStatus: ParseStatus = ParseStatus.NOT_PARSED,
    val createdAt: Long = System.currentTimeMillis()
)
```

- [ ] **Step 7: 运行测试验证通过**

Run: `cd android-app && ./gradlew testDebugUnitTest --tests "com.paperlearning.assistant.data.model.ModelTests"`
Expected: PASS

- [ ] **Step 8: 创建其他 Entity（LearningStepEntity, UserProgressEntity, LlmConfigEntity）**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/model/LearningStepEntity.kt
package com.paperlearning.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_steps")
data class LearningStepEntity(
    @PrimaryKey val id: Long = 0,
    val paperId: Long,
    val stepOrder: Int,
    val stepType: StepType,
    val title: String,
    val content: String,
    val mediaPath: String?,
    val estimatedMinutes: Int
)

// android-app/app/src/main/java/com/paperlearning/assistant/data/model/UserProgressEntity.kt
package com.paperlearning.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Long = 0,
    val paperId: Long,
    val currentStep: Int,
    val completedSteps: String,  // JSON array string
    val learningMode: LearningMode,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

// android-app/app/src/main/java/com/paperlearning/assistant/data/model/LlmConfigEntity.kt
package com.paperlearning.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "llm_configs")
data class LlmConfigEntity(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val apiEndpoint: String,
    val apiKey: String,
    val model: String,
    val isActive: Boolean = false,
    val isPreset: Boolean = false
)
```

- [ ] **Step 9: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/app/src/main/java/com/paperlearning/assistant/data/model/
git add android-app/app/src/test/java/com/paperlearning/assistant/data/model/
git commit -m "feat: create data models and entities with tests"
```

---

### Task 1.3: 创建 Room 数据库和 DAO

**Files:**
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/local/dao/PaperDao.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/local/dao/LearningStepDao.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/local/dao/UserProgressDao.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/local/dao/LlmConfigDao.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/local/PaperDatabase.kt`
- Create: `android-app/app/src/test/java/com/paperlearning/assistant/data/local/dao/DaoTests.kt`

- [ ] **Step 1: 编写 DAO 测试**

```kotlin
// android-app/app/src/test/java/com/paperlearning/assistant/data/local/dao/DaoTests.kt
package com.paperlearning.assistant.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.paperlearning.assistant.data.local.PaperDatabase
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PaperDaoTests {
    private lateinit var database: PaperDatabase
    private lateinit var paperDao: PaperDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PaperDatabase::class.java
        ).build()
        paperDao = database.paperDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `insertPaper inserts and retrieves paper`() = runTest {
        val paper = PaperEntity(
            title = "Test Paper",
            authors = """["Author A"]""",
            abstract = "Test",
            pdfPath = "/test.pdf"
        )
        val id = paperDao.insert(paper)
        val retrieved = paperDao.getById(id)
        assertNotNull(retrieved)
        assertEquals("Test Paper", retrieved?.title)
    }

    @Test
    fun `getAllPapers returns all papers`() = runTest {
        paperDao.insert(PaperEntity(title = "Paper 1", authors = "[]", abstract = "", pdfPath = ""))
        paperDao.insert(PaperEntity(title = "Paper 2", authors = "[]", abstract = "", pdfPath = ""))
        val papers = paperDao.getAll()
        assertEquals(2, papers.size)
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `cd android-app && ./gradlew testDebugUnitTest --tests "com.paperlearning.assistant.data.local.dao.PaperDaoTests"`
Expected: FAIL with "Unresolved reference: PaperDatabase"

- [ ] **Step 3: 创建 PaperDao**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/local/dao/PaperDao.kt
package com.paperlearning.assistant.data.local.dao

import androidx.room.*
import com.paperlearning.assistant.data.model.PaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paper: PaperEntity): Long

    @Update
    suspend fun update(paper: PaperEntity)

    @Delete
    suspend fun delete(paper: PaperEntity)

    @Query("SELECT * FROM papers WHERE id = :id")
    suspend fun getById(id: Long): PaperEntity?

    @Query("SELECT * FROM papers ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers WHERE parsedStatus = :status")
    fun getByStatus(status: ParseStatus): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers WHERE title LIKE '%' || :query || '%' OR abstract LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<PaperEntity>>
}
```

- [ ] **Step 4: 创建其他 DAO**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/local/dao/LearningStepDao.kt
package com.paperlearning.assistant.data.local.dao

import androidx.room.*
import com.paperlearning.assistant.data.model.LearningStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningStepDao {
    @Insert
    suspend fun insert(steps: List<LearningStepEntity>)

    @Query("SELECT * FROM learning_steps WHERE paperId = :paperId ORDER BY stepOrder")
    fun getByPaperId(paperId: Long): Flow<List<LearningStepEntity>>

    @Query("DELETE FROM learning_steps WHERE paperId = :paperId")
    suspend fun deleteByPaperId(paperId: Long)
}

// android-app/app/src/main/java/com/paperlearning/assistant/data/local/dao/UserProgressDao.kt
package com.paperlearning.assistant.data.local.dao

import androidx.room.*
import com.paperlearning.assistant.data.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: UserProgressEntity)

    @Update
    suspend fun update(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE paperId = :paperId")
    suspend fun getByPaperId(paperId: Long): UserProgressEntity?

    @Query("SELECT * FROM user_progress ORDER BY startedAt DESC")
    fun getAll(): Flow<List<UserProgressEntity>>
}

// android-app/app/src/main/java/com/paperlearning/assistant/data/local/dao/LlmConfigDao.kt
package com.paperlearning.assistant.data.local.dao

import androidx.room.*
import com.paperlearning.assistant.data.model.LlmConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LlmConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: LlmConfigEntity)

    @Update
    suspend fun update(config: LlmConfigEntity)

    @Query("SELECT * FROM llm_configs WHERE isActive = 1")
    suspend fun getActive(): LlmConfigEntity?

    @Query("SELECT * FROM llm_configs WHERE isPreset = 1")
    fun getPresets(): Flow<List<LlmConfigEntity>>

    @Query("SELECT * FROM llm_configs")
    fun getAll(): Flow<List<LlmConfigEntity>>
}
```

- [ ] **Step 5: 创建 PaperDatabase**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/local/PaperDatabase.kt
package com.paperlearning.assistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paperlearning.assistant.data.local.dao.*
import com.paperlearning.assistant.data.model.*

@Database(
    entities = [
        PaperEntity::class,
        LearningStepEntity::class,
        UserProgressEntity::class,
        LlmConfigEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class PaperDatabase : RoomDatabase() {
    abstract fun paperDao(): PaperDao
    abstract fun learningStepDao(): LearningStepDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun llmConfigDao(): LlmConfigDao

    companion object {
        const val DATABASE_NAME = "paper_learning_db"
    }
}
```

- [ ] **Step 6: 运行测试验证通过**

Run: `cd android-app && ./gradlew testDebugUnitTest --tests "com.paperlearning.assistant.data.local.dao.*"`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/app/src/main/java/com/paperlearning/assistant/data/local/
git add android-app/app/src/test/java/com/paperlearning/assistant/data/local/
git commit -m "feat: create Room database and DAOs with tests"
```

---

### Task 1.4: 创建 Application 类和 Hilt 配置

**Files:**
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/PaperLearningApp.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/di/DatabaseModule.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/di/RepositoryModule.kt`
- Create: `android-app/app/src/main/AndroidManifest.xml`
- Test: `android-app/app/src/test/java/com/paperlearning/assistant/HiltTests.kt`

- [ ] **Step 1: 编写 Hilt 测试**

```kotlin
// android-app/app/src/test/java/com/paperlearning/assistant/HiltTests.kt
package com.paperlearning.assistant

import org.junit.Test
import org.junit.Assert.*

class HiltTests {
    @Test
    fun `PaperLearningApp creates successfully`() {
        // Basic smoke test
        val app = PaperLearningApp()
        assertNotNull(app)
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `cd android-app && ./gradlew testDebugUnitTest --tests "com.paperlearning.assistant.HiltTests"`
Expected: FAIL with "Unresolved reference: PaperLearningApp"

- [ ] **Step 3: 创建 Application 类**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/PaperLearningApp.kt
package com.paperlearning.assistant

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PaperLearningApp : Application()
```

- [ ] **Step 4: 创建 DatabaseModule**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/di/DatabaseModule.kt
package com.paperlearning.assistant.di

import android.content.Context
import androidx.room.Room
import com.paperlearning.assistant.data.local.PaperDatabase
import com.paperlearning.assistant.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PaperDatabase {
        return Room.databaseBuilder(
            context,
            PaperDatabase::class.java,
            PaperDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePaperDao(database: PaperDatabase): PaperDao {
        return database.paperDao()
    }

    @Provides
    @Singleton
    fun provideLearningStepDao(database: PaperDatabase): LearningStepDao {
        return database.learningStepDao()
    }

    @Provides
    @Singleton
    fun provideUserProgressDao(database: PaperDatabase): UserProgressDao {
        return database.userProgressDao()
    }

    @Provides
    @Singleton
    fun provideLlmConfigDao(database: PaperDatabase): LlmConfigDao {
        return database.llmConfigDao()
    }
}
```

- [ ] **Step 5: 创建 AndroidManifest.xml**

```xml
<!-- android-app/app/src/main/AndroidManifest.xml -->
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />

    <application
        android:name=".PaperLearningApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.PaperLearningAssistant">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.PaperLearningAssistant">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

- [ ] **Step 6: 运行测试验证通过**

Run: `cd android-app && ./gradlew testDebugUnitTest --tests "com.paperlearning.assistant.HiltTests"`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/app/src/main/java/com/paperlearning/assistant/PaperLearningApp.kt
git add android-app/app/src/main/java/com/paperlearning/assistant/di/
git add android-app/app/src/main/AndroidManifest.xml
git commit -m "feat: setup Hilt dependency injection and Application class"
```

---

## Phase 2: 论文搜索 + 导入 (Week 2)

### Task 2.1: 创建 arXiv API 客户端

**Files:**
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/remote/arxiv/ArxivApiService.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/remote/arxiv/ArxivResponse.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/remote/arxiv/ArxivRepository.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/di/NetworkModule.kt`
- Test: `android-app/app/src/test/java/com/paperlearning/assistant/data/remote/arxiv/ArxivRepositoryTests.kt`

- [ ] **Step 1: 编写 arXiv API 测试**

```kotlin
// android-app/app/src/test/java/com/paperlearning/assistant/data/remote/arxiv/ArxivRepositoryTests.kt
package com.paperlearning.assistant.data.remote.arxiv

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ArxivRepositoryTests {
    @Test
    fun `search returns papers matching query`() = runTest {
        // Mock test - will be implemented with MockWebServer
        val repository = ArxivRepository()
        // Test implementation pending
    }
}
```

- [ ] **Step 2: 创建 ArxivApiService**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/remote/arxiv/ArxivApiService.kt
package com.paperlearning.assistant.data.remote.arxiv

import retrofit2.http.GET
import retrofit2.http.Query

interface ArxivApiService {
    @GET("feed")
    suspend fun search(
        @Query("search_query") query: String,
        @Query("start") start: Int = 0,
        @Query("max_results") maxResults: Int = 20
    ): String  // XML response
}
```

- [ ] **Step 3: 创建 NetworkModule**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/di/NetworkModule.kt
package com.paperlearning.assistant.di

import com.paperlearning.assistant.data.remote.arxiv.ArxivApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideArxivApiService(client: OkHttpClient): ArxivApiService {
        return Retrofit.Builder()
            .baseUrl("http://export.arxiv.org/api/query?")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ArxivApiService::class.java)
    }
}
```

- [ ] **Step 4: 添加 Retrofit 依赖**

在 `app/build.gradle.kts` 中添加：
```kotlin
implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
```

- [ ] **Step 5: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/app/src/main/java/com/paperlearning/assistant/data/remote/
git add android-app/app/src/main/java/com/paperlearning/assistant/di/NetworkModule.kt
git commit -m "feat: create arXiv API client with Retrofit"
```

---

### Task 2.2: 创建 PDF 导入功能

**Files:**
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/repository/PdfImportRepository.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/util/PdfParser.kt`
- Test: `android-app/app/src/test/java/com/paperlearning/assistant/data/repository/PdfImportRepositoryTests.kt`

- [ ] **Step 1: 编写 PDF 导入测试**

```kotlin
// Test file with mock PDF parsing logic
```

- [ ] **Step 2: 创建 PdfParser 工具类**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/util/PdfParser.kt
package com.paperlearning.assistant.util

import android.content.Context
import android.net.Uri
import java.io.File

class PdfParser(private val context: Context) {
    
    data class PdfMetadata(
        val title: String?,
        val authors: List<String>?,
        val abstract: String?
    )

    suspend fun extractMetadata(uri: Uri): PdfMetadata {
        // Use MuPDF or alternative library to extract metadata
        // For now, return basic info from filename
        val fileName = getFileName(uri)
        return PdfMetadata(
            title = fileName.removeSuffix(".pdf"),
            authors = null,
            abstract = null
        )
    }

    suspend fun copyToInternalStorage(uri: Uri, paperId: Long): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputFile = File(context.filesDir, "papers/$paperId.pdf")
        outputFile.parentFile?.mkdirs()
        
        inputStream?.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        return outputFile.absolutePath
    }

    private fun getFileName(uri: Uri): String {
        return context.contentResolver.query(uri, null, null, null, null)
            ?.use { cursor ->
                cursor.moveToFirst()
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.getString(nameIndex)
            } ?: "unknown.pdf"
    }
}
```

- [ ] **Step 3: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/app/src/main/java/com/paperlearning/assistant/data/repository/
git add android-app/app/src/main/java/com/paperlearning/assistant/util/
git commit -m "feat: create PDF import and parsing utilities"
```

---

## Phase 3: 大模型集成 (Week 3)

### Task 3.1: 创建 LLM 服务接口

**Files:**
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/LlmService.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/LlmRequest.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/LlmResponse.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/OpenAiClient.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/ClaudeClient.kt`
- Test: `android-app/app/src/test/java/com/paperlearning/assistant/data/remote/llm/LlmServiceTests.kt`

- [ ] **Step 1: 定义 LLM 服务接口**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/LlmService.kt
package com.paperlearning.assistant.data.remote.llm

import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.PaperEntity

interface LlmService {
    suspend fun parsePaper(pdfContent: ByteArray, mode: ParseMode): ParseResult
    suspend fun generateLearningPath(paper: PaperEntity, mode: LearningMode): LearningPath
}

enum class ParseMode {
    METADATA_ONLY,
    FULL_STRUCTURED
}

data class ParseResult(
    val title: String,
    val authors: List<String>,
    val abstract: String,
    val sections: List<Section>
)

data class Section(
    val title: String,
    val content: String
)

data class LearningPath(
    val steps: List<LearningStep>
)

data class LearningStep(
    val stepType: String,
    val title: String,
    val content: String,
    val estimatedMinutes: Int
)
```

- [ ] **Step 2: 创建 OpenAI 客户端实现**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/OpenAiClient.kt
package com.paperlearning.assistant.data.remote.llm

import okhttp3.*
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OpenAiClient(private val apiKey: String, private val baseUrl: String = "https://api.openai.com/v1") {
    
    private val client = OkHttpClient()
    private val mediaType = MediaType.parse("application/json; charset=utf-8")

    suspend fun chat(prompt: String, systemPrompt: String): String {
        return suspendCancellableCoroutine { continuation ->
            val json = JSONObject()
            json.put("model", "gpt-4-turbo")
            json.put("messages", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            json.put("max_tokens", 4000)

            val body = RequestBody.create(mediaType, json.toString())
            val request = Request.Builder()
                .url("$baseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful && responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val content = jsonResponse
                            .getJSONObject("choices")
                            .getJSONArray(0)
                            .getJSONObject(0)
                            .getString("content")
                        continuation.resume(content)
                    } else {
                        continuation.resumeWithException(Exception("API Error: ${response.code}"))
                    }
                }
            })
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/
git commit -m "feat: create LLM service interface and OpenAI client"
```

---

## Phase 4: 学习路径 UI (Week 4)

### Task 4.1: 创建主界面（Home Screen）

**Files:**
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/ui/theme/Theme.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/ui/theme/Color.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/ui/screens/home/HomeScreen.kt`
- Create: `android-app/app/src/main/java/com/paperlearning/assistant/viewmodel/HomeViewModel.kt`
- Test: `android-app/app/src/test/java/com/paperlearning/assistant/viewmodel/HomeViewModelTests.kt`

- [ ] **Step 1: 创建主题配置**

```kotlin
// android-app/app/src/main/java/com/paperlearning/assistant/ui/theme/Color.kt
package com.paperlearning.assistant.ui.theme

import androidx.compose.ui.graphics.Color

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
```

- [ ] **Step 2: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/app/src/main/java/com/paperlearning/assistant/ui/
git add android-app/app/src/main/java/com/paperlearning/assistant/viewmodel/
git commit -m "feat: create Home screen UI with ViewModel"
```

---

## Phase 5: 测试 + 优化 (Week 5)

### Task 5.1: 集成测试

**Files:**
- Create: `android-app/app/src/androidTest/java/com/paperlearning/assistant/IntegrationTests.kt`

- [ ] **Step 1: 创建集成测试套件**

```kotlin
// Comprehensive integration tests for all features
```

- [ ] **Step 2: Commit**

```bash
cd /home/admin/openclaw/workspace/paper-tutor
git add android-app/app/src/androidTest/
git commit -m "feat: add integration tests for all features"
```

---

## 执行检查清单

### 每个 Phase 完成后验证

- [ ] 所有单元测试通过
- [ ] 代码已提交到 git
- [ ] 在红米 K70 上测试基本功能
- [ ] 更新进度到此计划文档

### 最终交付物

- [ ] APK 文件可安装运行
- [ ] 所有核心功能正常工作
- [ ] 代码通过审查
- [ ] README 文档更新

---

**计划创建时间**: 2026-03-25  
**预计总任务数**: 20+  
**预计完成时间**: 5 周
