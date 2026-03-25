package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.UserProgressEntity
import com.paperlearning.assistant.data.repository.LearningRepository
import javax.inject.Inject

/**
 * 保存学习进度的 UseCase
 * 用于保存用户当前学习状态，支持继续学习
 */
class SaveProgressUseCase @Inject constructor(
    private val learningRepository: LearningRepository
) {
    data class SaveRequest(
        val paperId: Long,
        val currentStep: Int,
        val completedSteps: List<Int>,
        val learningMode: LearningMode,
        val isCompleted: Boolean = false
    )

    suspend operator fun invoke(request: SaveRequest): Result<Unit, String> {
        return try {
            val existingProgress = learningRepository.getUserProgressByPaperId(request.paperId)
            
            if (existingProgress != null) {
                // 更新现有进度
                val updatedProgress = existingProgress.copy(
                    currentStep = request.currentStep,
                    completedSteps = formatCompletedSteps(request.completedSteps),
                    learningMode = request.learningMode,
                    completedAt = if (request.isCompleted) System.currentTimeMillis() else existingProgress.completedAt
                )
                learningRepository.updateUserProgress(updatedProgress)
            } else {
                // 创建新进度记录
                val newProgress = UserProgressEntity(
                    paperId = request.paperId,
                    currentStep = request.currentStep,
                    completedSteps = formatCompletedSteps(request.completedSteps),
                    learningMode = request.learningMode,
                    startedAt = System.currentTimeMillis(),
                    completedAt = if (request.isCompleted) System.currentTimeMillis() else null
                )
                learningRepository.saveUserProgress(newProgress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.message ?: "保存进度失败")
        }
    }
    
    private fun formatCompletedSteps(steps: List<Int>): String {
        return if (steps.isEmpty()) {
            "[]"
        } else {
            steps.joinToString(",", prefix = "[", postfix = "]")
        }
    }
}
