package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.UserProgressEntity
import com.paperlearning.assistant.data.repository.UserProgressRepository
import javax.inject.Inject

/**
 * 更新学习进度的 UseCase
 * 记录用户当前学习步骤和完成状态
 */
class UpdateProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) {
    data class UpdateRequest(
        val paperId: Long,
        val currentStep: Int,
        val completedSteps: List<Int>,
        val learningMode: LearningMode,
        val isCompleted: Boolean = false
    )

    suspend operator fun invoke(request: UpdateRequest): Result<Unit, String> {
        return try {
            val existingProgress = userProgressRepository.getProgressByPaperId(request.paperId)
            
            val progress = if (existingProgress != null) {
                existingProgress.copy(
                    currentStep = request.currentStep,
                    completedSteps = request.completedSteps.joinToString(",", transform = { it.toString() }),
                    learningMode = request.learningMode,
                    completedAt = if (request.isCompleted) System.currentTimeMillis() else null
                )
            } else {
                UserProgressEntity(
                    paperId = request.paperId,
                    currentStep = request.currentStep,
                    completedSteps = request.completedSteps.joinToString(",", transform = { it.toString() }),
                    learningMode = request.learningMode,
                    completedAt = if (request.isCompleted) System.currentTimeMillis() else null
                )
            }
            
            if (existingProgress != null) {
                userProgressRepository.updateProgress(progress)
            } else {
                userProgressRepository.insertProgress(progress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.message ?: "更新进度失败")
        }
    }
}
