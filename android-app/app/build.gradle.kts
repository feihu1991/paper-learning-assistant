// android-app/app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.paperlearning.assistant"
    compileSdk = 34

    // Disable lint abort on error for release builds
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

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
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Load signing config from gradle.properties (injected by CI workflow)
            val storeFilePath = project.properties["PAPER_LEARNING_STORE_FILE"] as String?
            val storePassword = project.properties["PAPER_LEARNING_STORE_PASSWORD"] as String?
            val keyAlias = project.properties["PAPER_LEARNING_KEY_ALIAS"] as String?
            val keyPassword = project.properties["PAPER_LEARNING_KEY_PASSWORD"] as String?
            if (storeFilePath != null && storePassword != null && keyAlias != null && keyPassword != null) {
                signingConfigs {
                    create("release") {
                        this.storeFile = file(storeFilePath)
                        this.storePassword = storePassword
                        this.keyAlias = keyAlias
                        this.keyPassword = keyPassword
                    }
                }
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            isMinifyEnabled = false
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
    // AppCompat (必须，提供基础主题)
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Activity Compose (必须)
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Room - use KSP instead of KAPT to avoid annotation processing issues
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    
    // Hilt - still uses KAPT (KSP not supported yet)
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // PDF Processing
    implementation("com.github.TomRoush:PdfBox-Android:1.8.10.2")
    
    // Security - EncryptedSharedPreferences for API key storage
    implementation("androidx.security:security-crypto:1.1.0")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
