package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.UserProgressEntity
import com.paperlearning.assistant.data.repository.LearningRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * 获取学习进度的 UseCase
 * 用于查询用户学习状态，支持继续学习
 */
class GetProgressUseCase @Inject constructor(
    private val learningRepository: LearningRepository
) {
    data class ProgressResult(
        val paperId: Long,
        val currentStep: Int,
        val completedSteps: List<Int>,
        val learningMode: com.paperlearning.assistant.data.model.LearningMode,
        val startedAt: Long,
        val completedAt: Long?,
        val isCompleted: Boolean,
        val progressPercentage: Float
    )

    suspend operator fun invoke(paperId: Long): Result<ProgressResult?> {
        return try {
            val progress = learningRepository.getUserProgressByPaperId(paperId)
            
            if (progress == null) {
                Result.success(null)
            } else {
                val completedStepsList = parseCompletedSteps(progress.completedSteps)
                val totalSteps = getTotalSteps(paperId)
                val progressPercentage = if (totalSteps > 0) {
                    (completedStepsList.size.toFloat() / totalSteps) * 100f
                } else {
                    0f
                }
                
                Result.success(
                    ProgressResult(
                        paperId = progress.paperId,
                        currentStep = progress.currentStep,
                        completedSteps = completedStepsList,
                        learningMode = progress.learningMode,
                        startedAt = progress.startedAt,
                        completedAt = progress.completedAt,
                        isCompleted = progress.completedAt != null,
                        progressPercentage = progressPercentage
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun parseCompletedSteps(completedStepsString: String): List<Int> {
        return if (completedStepsString.isBlank() || completedStepsString == "[]") {
            emptyList()
        } else {
            completedStepsString
                .trim('[', ']')
                .split(",")
                .filter { it.isNotBlank() }
                .mapNotNull { it.toIntOrNull() }
        }
    }
    
    private suspend fun getTotalSteps(paperId: Long): Int {
        return learningRepository.getLearningStepsByPaperId(paperId).first().size
    }
}
