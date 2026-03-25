package com.paperlearning.assistant.util

import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.StepType

/**
 * 学习路径模板配置
 * 为不同学习模式提供预定义的步骤结构和提示词模板
 */
object LearningPathTemplates {

    /**
     * 学习模式配置
     * 定义每种模式的步骤类型序列和预估时间
     */
    data class ModeConfig(
        val mode: LearningMode,
        val stepTypes: List<StepType>,
        val baseTimePerStep: Int, // 每个步骤的基础时间（分钟）
        val systemPrompt: String,
        val userPromptTemplate: String
    )

    /**
     * 获取指定学习模式的配置
     */
    fun getModeConfig(mode: LearningMode): ModeConfig {
        return when (mode) {
            LearningMode.FAST -> FAST_MODE_CONFIG
            LearningMode.STANDARD -> STANDARD_MODE_CONFIG
            LearningMode.DEEP -> DEEP_MODE_CONFIG
        }
    }

    /**
     * 快速了解模式配置
     * 适合快速浏览论文核心内容，约 5 分钟完成
     */
    private val FAST_MODE_CONFIG = ModeConfig(
        mode = LearningMode.FAST,
        stepTypes = listOf(
            StepType.BACKGROUND,
            StepType.PROBLEM,
            StepType.CORE_CONCEPT,
            StepType.CONCLUSION
        ),
        baseTimePerStep = 2,
        systemPrompt = """你是一个专业的学术论文助手。用户需要快速了解一篇论文的核心内容。
请用简洁清晰的语言解释论文，避免过多技术细节。
每个步骤的回答控制在 100-200 字以内。""",
        userPromptTemplate = """请为以下论文生成快速学习路径：

论文标题：{title}
作者：{authors}
摘要：{abstract}

学习模式：快速了解（约 5 分钟）

请按照以下步骤生成学习内容：
1. 研究背景：这篇论文研究什么问题？为什么重要？
2. 问题定义：论文要解决的核心问题是什么？
3. 核心概念：最关键的概念或方法是什么？
4. 总结：论文的主要贡献和结论是什么？

请为每个步骤提供简洁的解释。"""
    )

    /**
     * 标准学习模式配置
     * 适合系统学习论文内容，约 20 分钟完成
     */
    private val STANDARD_MODE_CONFIG = ModeConfig(
        mode = LearningMode.STANDARD,
        stepTypes = listOf(
            StepType.BACKGROUND,
            StepType.PROBLEM,
            StepType.CORE_CONCEPT,
            StepType.METHOD,
            StepType.EXPERIMENT,
            StepType.CONCLUSION,
            StepType.QUIZ
        ),
        baseTimePerStep = 3,
        systemPrompt = """你是一个专业的学术论文助手。用户希望系统学习一篇论文。
请提供详细但易懂的解释，包含必要的技术细节。
每个步骤的回答控制在 200-400 字以内。
在测验步骤，请生成 3-5 道选择题来检验理解。""",
        userPromptTemplate = """请为以下论文生成标准学习路径：

论文标题：{title}
作者：{authors}
摘要：{abstract}

学习模式：标准学习（约 20 分钟）

请按照以下步骤生成详细的学习内容：
1. 研究背景：研究领域、发展脉络、研究动机
2. 问题定义：形式化的问题描述、现有方法的局限
3. 核心概念：关键定义、符号说明、基础理论
4. 方法详解：提出的方法、技术路线、创新点
5. 实验分析：实验设置、对比方法、主要结果
6. 总结：主要贡献、局限性、未来方向
7. 小测验：生成 3-5 道选择题检验理解

请为每个步骤提供详细的解释。"""
    )

    /**
     * 深入掌握模式配置
     * 适合深度研究论文，约 60 分钟完成
     */
    private val DEEP_MODE_CONFIG = ModeConfig(
        mode = LearningMode.DEEP,
        stepTypes = listOf(
            StepType.BACKGROUND,
            StepType.PROBLEM,
            StepType.CORE_CONCEPT,
            StepType.METHOD,
            StepType.FORMULA,
            StepType.EXPERIMENT,
            StepType.CONCLUSION,
            StepType.QUIZ
        ),
        baseTimePerStep = 8,
        systemPrompt = """你是一个专业的学术论文助手。用户希望深入研究一篇论文。
请提供全面深入的解释，包含技术细节、公式推导、实验分析。
每个步骤的回答控制在 400-800 字以内。
在公式步骤，请用 LaTeX 格式展示关键公式并详细解释。
在测验步骤，请生成 5-10 道题目（包含选择和简答）来检验理解。""",
        userPromptTemplate = """请为以下论文生成深入学习路径：

论文标题：{title}
作者：{authors}
摘要：{abstract}

学习模式：深入掌握（约 60 分钟）

请按照以下步骤生成深入的学习内容：
1. 研究背景：领域历史、关键里程碑、研究动机、相关综述
2. 问题定义：形式化定义、符号系统、问题分类、挑战分析
3. 核心概念：基础理论、关键定义、前置知识、概念关联
4. 方法详解：方法框架、技术细节、算法流程、创新分析
5. 关键公式：核心公式推导、符号解释、直观理解、变体形式
6. 实验分析：实验设计、数据集、对比方法、结果分析、消融实验
7. 总结：主要贡献、理论意义、实际应用、局限性、未来方向
8. 小测验：生成 5-10 道题目（选择和简答）检验深度理解

请为每个步骤提供全面深入的解释，包含必要的公式和图表描述。"""
    )

    /**
     * 构建 LLM 请求的提示词
     * 
     * @param mode 学习模式
     * @param title 论文标题
     * @param authors 作者列表
     * @param abstract 论文摘要
     * @return 包含 system 和 user 消息的 Pair
     */
    fun buildPrompts(
        mode: LearningMode,
        title: String,
        authors: String,
        abstract: String
    ): Pair<String, String> {
        val config = getModeConfig(mode)
        
        val userPrompt = config.userPromptTemplate
            .replace("{title}", title)
            .replace("{authors}", authors)
            .replace("{abstract}", abstract)
        
        return Pair(config.systemPrompt, userPrompt)
    }

    /**
     * 获取指定模式的预估总时间
     */
    fun getEstimatedTotalMinutes(mode: LearningMode): Int {
        val config = getModeConfig(mode)
        return config.stepTypes.size * config.baseTimePerStep
    }

    /**
     * 解析 LLM 响应，提取各步骤内容
     * 期望响应格式：
     * ### 步骤 1: [步骤类型]
     * [内容]
     * 
     * ### 步骤 2: [步骤类型]
     * [内容]
     * ...
     */
    fun parseStepsFromResponse(response: String, expectedStepTypes: List<StepType>): List<ParsedStep> {
        val steps = mutableListOf<ParsedStep>()
        val lines = response.split("\n")
        
        var currentStepIndex = -1
        var currentContent = StringBuilder()
        
        for ((index, line) in lines.withIndex()) {
            if (line.startsWith("###") || line.startsWith("##") || line.matches(Regex("^\\d+\\.\\s*"))) {
                // 保存前一个步骤
                if (currentStepIndex >= 0 && currentStepIndex < expectedStepTypes.size) {
                    steps.add(
                        ParsedStep(
                            stepType = expectedStepTypes[currentStepIndex],
                            title = extractTitle(lines[currentStepIndex]),
                            content = currentContent.toString().trim()
                        )
                    )
                }
                
                // 开始新步骤
                currentStepIndex++
                currentContent = StringBuilder()
                
                // 尝试从当前行提取标题
                if (currentStepIndex < expectedStepTypes.size) {
                    currentContent.append(line).append("\n")
                }
            } else {
                currentContent.append(line).append("\n")
            }
        }
        
        // 保存最后一个步骤
        if (currentStepIndex >= 0 && currentStepIndex < expectedStepTypes.size) {
            steps.add(
                ParsedStep(
                    stepType = expectedStepTypes[currentStepIndex],
                    title = extractTitle(lines.getOrNull(currentStepIndex) ?: "步骤 ${currentStepIndex + 1}"),
                    content = currentContent.toString().trim()
                )
            )
        }
        
        return steps
    }

    private fun extractTitle(line: String): String {
        // 尝试提取标题部分
        val patterns = listOf(
            Regex("###\\s*步骤\\s*\\d+[:：]?\\s*\\[?([^\\]]+)\\]?"),
            Regex("##\\s*步骤\\s*\\d+[:：]?\\s*\\[?([^\\]]+)\\]?"),
            Regex("\\d+\\.\\s*(.+)")
        )
        
        for (pattern in patterns) {
            val match = pattern.find(line)
            if (match != null && match.groupValues.size > 1) {
                return match.groupValues[1].trim()
            }
        }
        
        return line.trim().take(50)
    }

    /**
     * 解析后的步骤数据
     */
    data class ParsedStep(
        val stepType: StepType,
        val title: String,
        val content: String
    )
}
