package com.paperlearning.assistant.data.remote.llm

import com.paperlearning.assistant.data.model.LlmConfigEntity

/**
 * 预置大模型配置列表
 * 包含主流 LLM 服务商的 API 端点和默认模型配置
 */
object PresetLlmConfigs {
    
    /**
     * 获取所有预置配置列表
     */
    fun getAllPresets(): List<LlmConfigEntity> {
        return listOf(
            createOpenAIConfig(),
            createClaudeConfig(),
            createQwenConfig(),
            createErnieConfig(),
            createKimiConfig(),
            createZhipuConfig()
        )
    }
    
    /**
     * OpenAI GPT-4 配置
     */
    private fun createOpenAIConfig(): LlmConfigEntity {
        return LlmConfigEntity(
            id = 1,
            name = "OpenAI GPT-4",
            apiEndpoint = "https://api.openai.com/v1",
            apiKey = "",
            model = "gpt-4",
            isActive = false,
            isPreset = true
        )
    }
    
    /**
     * Claude 3.5 配置
     */
    private fun createClaudeConfig(): LlmConfigEntity {
        return LlmConfigEntity(
            id = 2,
            name = "Claude 3.5",
            apiEndpoint = "https://api.anthropic.com/v1",
            apiKey = "",
            model = "claude-3-5-sonnet-20241022",
            isActive = false,
            isPreset = true
        )
    }
    
    /**
     * 通义千问配置
     */
    private fun createQwenConfig(): LlmConfigEntity {
        return LlmConfigEntity(
            id = 3,
            name = "通义千问",
            apiEndpoint = "https://dashscope.aliyuncs.com/api/v1",
            apiKey = "",
            model = "qwen-max",
            isActive = false,
            isPreset = true
        )
    }
    
    /**
     * 文心一言配置
     */
    private fun createErnieConfig(): LlmConfigEntity {
        return LlmConfigEntity(
            id = 4,
            name = "文心一言",
            apiEndpoint = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1",
            apiKey = "",
            model = "ernie-4.0-8k",
            isActive = false,
            isPreset = true
        )
    }
    
    /**
     * Kimi 配置
     */
    private fun createKimiConfig(): LlmConfigEntity {
        return LlmConfigEntity(
            id = 5,
            name = "Kimi",
            apiEndpoint = "https://api.moonshot.cn/v1",
            apiKey = "",
            model = "moonshot-v1-8k",
            isActive = false,
            isPreset = true
        )
    }
    
    /**
     * 智谱 AI 配置
     */
    private fun createZhipuConfig(): LlmConfigEntity {
        return LlmConfigEntity(
            id = 6,
            name = "智谱 AI",
            apiEndpoint = "https://open.bigmodel.cn/api/paas/v4",
            apiKey = "",
            model = "glm-4",
            isActive = false,
            isPreset = true
        )
    }
}
