package com.paperlearning.assistant.domain.model

/**
 * 测验数据模型
 * 支持选择题，包含答案和解析
 */
data class Quiz(
    val id: Long = 0,
    val paperId: Long,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM
)

/**
 * 测验难度等级
 */
enum class QuizDifficulty {
    EASY,      // 简单
    MEDIUM,    // 中等
    HARD       // 困难
}

/**
 * 测验结果
 */
data class QuizResult(
    val quizId: Long,
    val selectedAnswerIndex: Int,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
