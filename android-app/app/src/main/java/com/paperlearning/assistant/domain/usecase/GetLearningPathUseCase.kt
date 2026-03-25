package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.LearningStepEntity
import com.paperlearning.assistant.data.repository.LearningStepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取学习路径的 UseCase
 * 返回指定论文的所有学习步骤，按顺序排列
 */
class GetLearningPathUseCase @Inject constructor(
    private val learningStepRepository: LearningStepRepository
) {
    operator fun invoke(paperId: Long): Flow<List<LearningStepEntity>> {
        return learningStepRepository.getStepsByPaperId(paperId)
    }
}
