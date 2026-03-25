package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.repository.PaperRepository
import javax.inject.Inject

/**
 * 根据 ID 获取单篇论文的 UseCase
 */
class GetPaperByIdUseCase @Inject constructor(
    private val paperRepository: PaperRepository
) {
    suspend operator fun invoke(paperId: Long): PaperEntity? {
        return paperRepository.getPaperById(paperId)
    }
}
