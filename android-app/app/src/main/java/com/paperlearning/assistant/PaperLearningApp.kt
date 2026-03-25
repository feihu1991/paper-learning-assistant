package com.paperlearning.assistant

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application 入口类
 * 初始化 Hilt 依赖注入
 */
@HiltAndroidApp
class PaperLearningApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 在这里初始化全局组件
    }
}
