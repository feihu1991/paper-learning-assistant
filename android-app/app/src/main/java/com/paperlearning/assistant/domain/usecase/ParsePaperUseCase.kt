package com.paperlearning.assistant.domain.usecase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.remote.llm.LlmClient
import com.paperlearning.assistant.data.remote.llm.LlmRequestBuilder
import com.paperlearning.assistant.data.remote.llm.hasError
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.data.repository.SettingsRepository
import com.paperlearning.assistant.util.PdfParser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 论文解析用例
 * 负责从 PDF 中提取元数据并调用大模型生成结构化摘要
 */
class ParsePaperUseCase @Inject constructor(
    private val paperRepository: PaperRepository,
    private val settingsRepository: SettingsRepository,
    private val pdfParser: PdfParser,
    private val llmClient: LlmClient,
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "ParsePaperUseCase"
    }

    /**
     * 解析结果
     */
    data class Result(
        val success: Boolean,
        val paperId: Long? = null,
        val errorMessage: String? = null,
        val structuredSummary: String? = null
    )

    /**
     * 执行论文解析
     *
     * @param paperId 论文 ID
     * @return 解析结果
     */
    suspend operator fun invoke(paperId: Long): Result {
        return try {
            // 1. 获取论文信息
            val paper = paperRepository.getPaperById(paperId)
                ?: return Result(success = false, errorMessage = "论文不存在").also {
                    Log.e(TAG, "Paper not found: id=$paperId")
                }

            // 2. 更新状态为解析中
            paperRepository.updateParseStatus(paperId, ParseStatus.PARSING)

            // 3. 检查 PDF 文件是否存在
            if (paper.pdfPath.isBlank()) {
                return Result(success = false, errorMessage = "PDF 文件路径为空").also {
                    Log.w(TAG, "PDF path is blank for paper: id=$paperId")
                }
            }

            // 4. 从 PDF 提取元数据（使用 URI）
            val pdfUri = Uri.parse(paper.pdfPath)
            val metadata = pdfParser.extractMetadata(context, pdfUri)

            // 5. 获取活跃的 LLM 配置
            val llmConfig = settingsRepository.getActiveLlmConfig()
                ?: return Result(success = false, errorMessage = "未配置 LLM").also {
                    Log.w(TAG, "No active LLM config for paper: id=$paperId")
                }

            // 6. 构建 LLM 请求
            val llmRequest = buildLlmRequest(paper, metadata, llmConfig.model)

            // 7. 调用 LLM API 生成结构化摘要（复用 LlmClient）
            val llmResponse = llmClient.chatCompletion(
                baseUrl = llmConfig.apiEndpoint,
                apiKey = llmConfig.apiKey,
                request = llmRequest
            )

            if (llmResponse == null) {
                Log.e(TAG, "LLM API call failed for paper: id=$paperId, endpoint=${llmConfig.apiEndpoint}")
                return Result(success = false, errorMessage = "LLM 调用失败，请检查网络和 API 配置").also {
                    paperRepository.updateParseStatus(paperId, ParseStatus.FAILED)
                }
            }

            if (llmResponse.hasError()) {
                val errorMsg = "LLM 返回错误：${llmResponse.getErrorMessage()}"
                Log.e(TAG, "$errorMsg for paper: id=$paperId")
                return Result(success = false, errorMessage = errorMsg).also {
                    paperRepository.updateParseStatus(paperId, ParseStatus.FAILED)
                }
            }

            val structuredSummary = llmResponse.getAssistantResponse()
                ?: return Result(success = false, errorMessage = "LLM 返回为空").also {
                    Log.e(TAG, "Empty response from LLM for paper: id=$paperId")
                    paperRepository.updateParseStatus(paperId, ParseStatus.FAILED)
                }

            // 8. 更新论文信息
            val updatedPaper = paper.copy(
                title = metadata.title ?: paper.title,
                authors = metadata.authors?.joinToString(", ") ?: paper.authors,
                paperAbstract = metadata.abstract ?: paper.paperAbstract,
                parsedStatus = ParseStatus.COMPLETED
            )

            paperRepository.updatePaper(updatedPaper)

            Log.i(TAG, "Paper parsed successfully: id=$paperId, title=${updatedPaper.title}")
            Result(success = true, paperId = paperId, structuredSummary = structuredSummary)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error parsing paper: id=$paperId", e)
            paperRepository.updateParseStatus(paperId, ParseStatus.FAILED)
            Result(success = false, errorMessage = "解析失败：${e.message}")
        }
    }

    /**
     * 构建 LLM 请求
     * 根据提取的元数据生成结构化摘要提示词
     */
    private fun buildLlmRequest(
        paper: PaperEntity,
        metadata: PdfParser.PdfMetadata,
        model: String
    ) = buildString {
        appendLine("你是一位专业的学术论文分析助手。你的任务是根据论文信息生成结构化的摘要。")
        appendLine()
        appendLine("请按照以下格式输出：")
        appendLine()
        appendLine("## 研究背景")
        appendLine("[简述研究领域的背景和意义]")
        appendLine()
        appendLine("## 核心问题")
        appendLine("[论文要解决的核心问题是什么]")
        appendLine()
        appendLine("## 主要方法")
        appendLine("[论文提出的主要方法或技术]")
        appendLine()
        appendLine("## 关键创新")
        appendLine("[论文的创新点和贡献]")
        appendLine()
        appendLine("## 实验结果")
        appendLine("[主要的实验结果和发现]")
        appendLine()
        appendLine("## 局限性")
        appendLine("[方法的局限性或未来工作]")
        appendLine()
        appendLine("保持简洁、准确，使用中文回答。")
    }.let { systemPrompt ->
        val userPrompt = buildString {
            appendLine("请分析以下论文并生成结构化摘要：")
            appendLine()
            appendLine("标题：${metadata.title ?: paper.title}")
            appendLine()
            appendLine("作者：${metadata.authors?.joinToString(", ") ?: paper.authors}")
            appendLine()
            appendLine("摘要：${metadata.abstract ?: paper.paperAbstract}")
            appendLine()
            appendLine("请基于以上信息，按照系统提示中的格式生成结构化摘要。")
        }

        LlmRequestBuilder(model)
            .addSystemMessage(systemPrompt)
            .addUserMessage(userPrompt)
            .setTemperature(0.7f)
            .setMaxTokens(1000)
            .build()
    }

    /**
     * 批量解析论文
     */
    suspend fun parsePapersBatch(paperIds: List<Long>): List<Result> {
        return paperIds.map { invoke(it) }
    }
}
