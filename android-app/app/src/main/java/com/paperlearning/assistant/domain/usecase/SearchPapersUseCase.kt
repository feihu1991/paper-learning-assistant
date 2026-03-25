package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.repository.PaperRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 搜索论文的 UseCase
 * 根据标题或摘要内容进行模糊匹配
 */
class SearchPapersUseCase @Inject constructor(
    private val paperRepository: PaperRepository
) {
    operator fun invoke(query: String): Flow<List<PaperEntity>> {
        return paperRepository.searchPapers(query)
    }
}
