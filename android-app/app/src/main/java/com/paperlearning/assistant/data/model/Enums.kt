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
