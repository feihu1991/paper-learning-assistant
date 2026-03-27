package com.paperlearning.assistant.domain.usecase

import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.LearningStepEntity
import com.paperlearning.assistant.data.model.ParseStatus
import com.paperlearning.assistant.data.model.StepType
import com.paperlearning.assistant.data.remote.llm.LlmClient
import com.paperlearning.assistant.data.repository.LearningRepository
import com.paperlearning.assistant.data.repository.PaperRepository
import com.paperlearning.assistant.data.repository.SettingsRepository
import com.paperlearning.assistant.util.LearningPathTemplates
import javax.inject.Inject

/**
 * 生成学习路径的 UseCase
 * 调用大模型 API 自动生成论文的学习步骤
 */
class GenerateLearningPathUseCase @Inject constructor(
    private val paperRepository: PaperRepository,
    private val learningRepository: LearningRepository,
    private val settingsRepository: SettingsRepository,
    private val llmClient: LlmClient
) {
    /**
     * 生成学习路径请求参数
     */
    data class GenerateRequest(
        val paperId: Long,
        val learningMode: LearningMode
    )

    /**
     * 生成结果
     */
    data class GenerateResult(
        val success: Boolean,
        val stepsGenerated: Int = 0,
        val errorMessage: String? = null
    )

    /**
     * 执行学习路径生成
     * 
     * @param request 生成请求，包含论文 ID 和学习模式
     * @return 生成结果
     */
    suspend operator fun invoke(request: GenerateRequest): GenerateResult {
        return try {
            // 1. 获取论文信息
            val paper = paperRepository.getPaperById(request.paperId)
                ?: return GenerateResult(
                    success = false,
                    errorMessage = "论文不存在"
                )

            // 2. 检查论文是否已解析
            if (paper.parsedStatus != ParseStatus.COMPLETED) {
                return GenerateResult(
                    success = false,
                    errorMessage = "论文尚未完成解析"
                )
            }

            // 3. 获取活跃的 LLM 配置
            val llmConfig = settingsRepository.getActiveLlmConfig()
                ?: return GenerateResult(
                    success = false,
                    errorMessage = "请先配置 LLM API"
                )

            // 4. 删除旧的学习路径（如果存在）
            learningRepository.deleteLearningStepsByPaperId(request.paperId)

            // 5. 获取学习模式配置
            val modeConfig = LearningPathTemplates.getModeConfig(request.learningMode)

            // 6. 构建 LLM 请求提示词
            val (systemPrompt, userPrompt) = LearningPathTemplates.buildPrompts(
                mode = request.learningMode,
                title = paper.title,
                authors = paper.authors,
                abstract = paper.paperAbstract
            )

            // 7. 调用 LLM 生成学习内容
            val llmResponse = llmClient.simpleChat(
                baseUrl = llmConfig.baseUrl,
                apiKey = llmConfig.apiKey,
                model = llmConfig.model,
                userMessage = userPrompt,
                systemPrompt = systemPrompt
            )

            if (llmResponse.isNullOrBlank()) {
                return GenerateResult(
                    success = false,
                    errorMessage = "LLM 响应为空，请检查 API 配置"
                )
            }

            // 8. 解析 LLM 响应，提取步骤内容
            val parsedSteps = LearningPathTemplates.parseStepsFromResponse(
                response = llmResponse,
                expectedStepTypes = modeConfig.stepTypes
            )

            if (parsedSteps.isEmpty()) {
                return GenerateResult(
                    success = false,
                    errorMessage = "无法解析学习步骤，请重试"
                )
            }

            // 9. 转换为 LearningStepEntity 并保存
            val learningSteps = parsedSteps.mapIndexed { index, parsedStep ->
                LearningStepEntity(
                    paperId = request.paperId,
                    stepOrder = index,
                    stepType = parsedStep.stepType,
                    title = parsedStep.title,
                    content = parsedStep.content,
                    mediaPath = null, // 暂不支持多媒体
                    estimatedMinutes = modeConfig.baseTimePerStep
                )
            }

            learningRepository.insertLearningSteps(learningSteps)

            GenerateResult(
                success = true,
                stepsGenerated = learningSteps.size
            )
        } catch (e: Exception) {
            GenerateResult(
                success = false,
                errorMessage = "生成失败：${e.message ?: "未知错误"}"
            )
        }
    }

    /**
     * 快速生成学习路径（使用默认配置）
     *  Convenience method for quick generation with STANDARD mode
     */
    suspend fun generateQuick(paperId: Long): GenerateResult {
        return invoke(
            GenerateRequest(
                paperId = paperId,
                learningMode = LearningMode.STANDARD
            )
        )
    }

    /**
     * 重新生成学习路径
     * 删除现有路径并重新生成
     */
    suspend fun regenerate(
        paperId: Long,
        learningMode: LearningMode
    ): GenerateResult {
        // 先删除现有路径
        learningRepository.deleteLearningStepsByPaperId(paperId)
        
        return invoke(
            GenerateRequest(
                paperId = paperId,
                learningMode = learningMode
            )
        )
    }

    /**
     * 为多个论文批量生成学习路径
     * 
     * @param paperIds 论文 ID 列表
     * @param learningMode 学习模式
     * @return 每个论文的生成结果
     */
    suspend fun generateBatch(
        paperIds: List<Long>,
        learningMode: LearningMode
    ): Map<Long, GenerateResult> {
        val results = mutableMapOf<Long, GenerateResult>()
        
        for (paperId in paperIds) {
            val result = invoke(
                GenerateRequest(
                    paperId = paperId,
                    learningMode = learningMode
                )
            )
            results[paperId] = result
        }
        
        return results
    }

    /**
     * 验证学习路径是否已生成
     */
    suspend fun isPathGenerated(paperId: Long): Boolean {
        val steps = learningRepository.getLearningStepsByPaperId(paperId)
            .firstOrNull()
        return !steps.isNullOrEmpty()
    }

    /**
     * 获取学习路径生成建议
     * 根据论文长度和复杂度推荐学习模式
     */
    fun suggestLearningMode(
        abstractLength: Int,
        hasFormulas: Boolean = false,
        hasExperiments: Boolean = false
    ): LearningMode {
        return when {
            // 短摘要且无复杂内容 -> 快速模式
            abstractLength < 200 && !hasFormulas && !hasExperiments -> 
                LearningMode.FAST
            
            // 有公式或实验 -> 深入模式
            hasFormulas || hasExperiments -> 
                LearningMode.DEEP
            
            // 默认标准模式
            else -> 
                LearningMode.STANDARD
        }
    }
}
