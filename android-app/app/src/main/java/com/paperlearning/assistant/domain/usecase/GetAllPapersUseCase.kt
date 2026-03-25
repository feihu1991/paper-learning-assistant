package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.repository.PaperRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取所有论文的 UseCase
 * 按创建时间倒序返回论文列表
 */
class GetAllPapersUseCase @Inject constructor(
    private val paperRepository: PaperRepository
) {
    operator fun invoke(): Flow<List<PaperEntity>> {
        return paperRepository.getAllPapers()
    }
}
