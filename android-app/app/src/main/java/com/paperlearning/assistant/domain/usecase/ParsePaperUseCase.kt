package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.remote.llm.LlmRequestBuilder
import com.paperlearning.assistant.data.remote.llm.getAssistantResponse
import com.paperlearning.assistant.data.remote.llm.hasError
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.data.repository.SettingsRepository
import com.paperlearning.assistant.util.PdfParser
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * 论文解析用例
 * 负责从 PDF 中提取信息并调用大模型生成结构化摘要
 */
class ParsePaperUseCase @Inject constructor(
    private val paperRepository: PaperRepository,
    private val settingsRepository: SettingsRepository,
    private val pdfParser: PdfParser
) {
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
                ?: return Result(success = false, errorMessage = "论文不存在")

            // 2. 更新状态为解析中
            paperRepository.updateParseStatus(paperId, ParseStatus.PARSING)

            // 3. 检查 PDF 文件是否存在
            if (paper.pdfPath.isBlank()) {
                return Result(success = false, errorMessage = "PDF 文件路径为空")
            }

            // 4. 从 PDF 提取基础信息
            val metadata = pdfParser.extractTextFromFile(paper.pdfPath)
            
            // 5. 获取活跃的 LLM 配置
            val llmConfig = settingsRepository.getActiveLlmConfig()
                ?: return Result(success = false, errorMessage = "未配置 LLM")

            // 6. 构建 LLM 请求
            val llmRequest = buildLlmRequest(paper, llmConfig.model)

            // 7. 调用 LLM API 生成结构化摘要
            val structuredSummary = callLlmApi(llmConfig.apiEndpoint, llmConfig.apiKey, llmRequest)
                ?: return Result(success = false, errorMessage = "LLM 调用失败")

            // 8. 更新论文信息
            val updatedPaper = paper.copy(
                title = metadata.title ?: paper.title,
                authors = metadata.authors?.joinToString(", ") ?: paper.authors,
                abstract = metadata.abstract ?: paper.abstract,
                parsedStatus = ParseStatus.COMPLETED
            )
            
            paperRepository.updatePaper(updatedPaper)

            Result(success = true, paperId = paperId, structuredSummary = structuredSummary)
        } catch (e: Exception) {
            // 更新状态为失败
            paperRepository.updateParseStatus(paperId, ParseStatus.FAILED)
            Result(success = false, errorMessage = e.message)
        }
    }

    /**
     * 构建 LLM 请求
     * 创建用于生成结构化摘要的提示词
     */
    private fun buildLlmRequest(paper: PaperEntity, model: String): com.paperlearning.assistant.data.remote.llm.LlmRequest {
        val systemPrompt = """
            你是一位专业的学术论文分析助手。你的任务是根据论文信息生成结构化的摘要。
            
            请按照以下格式输出：
            
            ## 研究背景
            [简述研究领域的背景和意义]
            
            ## 核心问题
            [论文要解决的核心问题是什么]
            
            ## 主要方法
            [论文提出的主要方法或技术]
            
            ## 关键创新
            [论文的创新点和贡献]
            
            ## 实验结果
            [主要的实验结果和发现]
            
            ## 局限性
            [方法的局限性或未来工作]
            
            保持简洁、准确，使用中文回答。
        """.trimIndent()

        val userPrompt = """
            请分析以下论文并生成结构化摘要：
            
            标题：${paper.title}
            
            作者：${paper.authors}
            
            摘要：${paper.abstract}
            
            请基于以上信息，按照系统提示中的格式生成结构化摘要。
        """.trimIndent()

        return LlmRequestBuilder(model)
            .addSystemMessage(systemPrompt)
            .addUserMessage(userPrompt)
            .setTemperature(0.7f)
            .setMaxTokens(1000)
            .build()
    }

    /**
     * 调用 LLM API
     */
    private suspend fun callLlmApi(
        baseUrl: String,
        apiKey: String,
        request: com.paperlearning.assistant.data.remote.llm.LlmRequest
    ): String? {
        // 创建 Retrofit 实例
        val retrofit = Retrofit.Builder()
            .baseUrl(normalizeBaseUrl(baseUrl))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 创建 API 服务
        val apiService = retrofit.create(
            com.paperlearning.assistant.data.remote.llm.LlmApiService::class.java
        )

        // 调用 API
        val response = apiService.createChatCompletion(
            apiKey = "Bearer $apiKey",
            request = request
        )

        if (!response.isSuccessful) {
            throw Exception("LLM API 调用失败：${response.code()} ${response.message()}")
        }

        val llmResponse = response.body()
        
        if (llmResponse.hasError()) {
            throw Exception("LLM 返回错误：${llmResponse.getErrorMessage()}")
        }

        return llmResponse.getAssistantResponse()
    }

    /**
     * 标准化 Base URL
     * 确保 URL 以 / 结尾
     */
    private fun normalizeBaseUrl(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    /**
     * 批量解析论文（可选功能）
     * 用于后台任务处理多篇论文
     */
    suspend fun parsePapersBatch(paperIds: List<Long>): List<Result> {
        return paperIds.map { paperId ->
            invoke(paperId)
        }
    }
}
