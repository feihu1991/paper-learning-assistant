package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.UserProgressEntity
import com.paperlearning.assistant.data.repository.LearningRepository
import javax.inject.Inject

/**
 * 更新学习进度的 UseCase
 * 记录用户当前学习步骤和完成状态
 */
class UpdateProgressUseCase @Inject constructor(
    private val learningRepository: LearningRepository
) {
    data class UpdateRequest(
        val paperId: Long,
        val currentStep: Int,
        val completedSteps: List<Int>,
        val learningMode: LearningMode,
        val isCompleted: Boolean = false
    )

    suspend operator fun invoke(request: UpdateRequest): Result<Unit> {
        return try {
            val existingProgress = learningRepository.getUserProgressByPaperId(request.paperId)

            val progress = if (existingProgress != null) {
                existingProgress.copy(
                    currentStep = request.currentStep,
                    completedSteps = request.completedSteps.joinToString(","),
                    learningMode = request.learningMode,
                    completedAt = if (request.isCompleted) System.currentTimeMillis() else null
                )
            } else {
                UserProgressEntity(
                    paperId = request.paperId,
                    currentStep = request.currentStep,
                    completedSteps = request.completedSteps.joinToString(","),
                    learningMode = request.learningMode,
                    completedAt = if (request.isCompleted) System.currentTimeMillis() else null
                )
            }

            if (existingProgress != null) {
                learningRepository.updateUserProgress(progress)
            } else {
                learningRepository.saveUserProgress(progress)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e.message ?: "更新进度失败")
        }
    }
}
