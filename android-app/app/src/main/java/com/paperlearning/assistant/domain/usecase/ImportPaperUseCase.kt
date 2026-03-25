package com.paperlearning.assistant.domain.usecase

import android.content.Context
import android.net.Uri
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.util.PdfParser
import javax.inject.Inject

/**
 * 导入论文的 UseCase
 * 从 URI 复制 PDF 到内部存储，并创建论文记录
 */
class ImportPaperUseCase @Inject constructor(
    private val paperRepository: PaperRepository,
    private val pdfParser: PdfParser
) {
    data class Result(
        val success: Boolean,
        val paperId: Long? = null,
        val errorMessage: String? = null
    )

    suspend operator fun invoke(
        context: Context,
        uri: Uri,
        arxivId: String? = null
    ): Result {
        return try {
            // 提取 PDF 元数据
            val metadata = pdfParser.extractMetadata(uri)
            
            // 创建论文实体
            val paper = PaperEntity(
                arxivId = arxivId,
                title = metadata.title ?: "未知标题",
                authors = metadata.authors?.joinToString(", ") ?: "",
                abstract = metadata.abstract ?: "",
                pdfPath = "" // 将在 copyToInternalStorage 后更新
            )
            
            // 插入数据库，获取 ID
            val paperId = paperRepository.insertPaper(paper)
            
            // 复制 PDF 到内部存储
            val pdfPath = pdfParser.copyToInternalStorage(context, uri, paperId)
            
            // 更新论文的 PDF 路径
            paperRepository.updatePaper(paper.copy(id = paperId, pdfPath = pdfPath))
            
            Result(success = true, paperId = paperId)
        } catch (e: Exception) {
            Result(success = false, errorMessage = e.message)
        }
    }
}
