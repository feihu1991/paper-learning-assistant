package com.paperlearning.assistant.data.remote.llm

import com.google.gson.annotations.SerializedName

/**
 * Request model for LLM chat completion API.
 * Compatible with OpenAI API format.
 */
data class LlmRequest(
    @SerializedName("model")
    val model: String,
    
    @SerializedName("messages")
    val messages: List<Message>,
    
    @SerializedName("temperature")
    val temperature: Float = 0.7f,
    
    @SerializedName("max_tokens")
    val maxTokens: Int? = null,
    
    @SerializedName("top_p")
    val topP: Float? = null,
    
    @SerializedName("frequency_penalty")
    val frequencyPenalty: Float? = null,
    
    @SerializedName("presence_penalty")
    val presencePenalty: Float? = null,
    
    @SerializedName("stream")
    val stream: Boolean = false
)

/**
 * Message model for chat conversations.
 */
data class Message(
    @SerializedName("role")
    val role: String,
    
    @SerializedName("content")
    val content: String
) {
    companion object {
        fun user(content: String) = Message("user", content)
        fun assistant(content: String) = Message("assistant", content)
        fun system(content: String) = Message("system", content)
    }
}

/**
 * Builder for creating LLM requests with common defaults.
 */
class LlmRequestBuilder(
    private val model: String
) {
    private var messages: MutableList<Message> = mutableListOf()
    private var temperature: Float = 0.7f
    private var maxTokens: Int? = null
    private var topP: Float? = null
    private var frequencyPenalty: Float? = null
    private var presencePenalty: Float? = null
    private var stream: Boolean = false

    fun addMessage(message: Message) = apply {
        messages.add(message)
    }

    fun addUserMessage(content: String) = apply {
        messages.add(Message.user(content))
    }

    fun addSystemMessage(content: String) = apply {
        messages.add(Message.system(content))
    }

    fun addAssistantMessage(content: String) = apply {
        messages.add(Message.assistant(content))
    }

    fun setTemperature(temp: Float) = apply {
        temperature = temp
    }

    fun setMaxTokens(tokens: Int) = apply {
        maxTokens = tokens
    }

    fun setTopP(topP: Float) = apply {
        this.topP = topP
    }

    fun setFrequencyPenalty(penalty: Float) = apply {
        frequencyPenalty = penalty
    }

    fun setPresencePenalty(penalty: Float) = apply {
        presencePenalty = penalty
    }

    fun setStream(stream: Boolean) = apply {
        this.stream = stream
    }

    fun build(): LlmRequest {
        return LlmRequest(
            model = model,
            messages = messages.toList(),
            temperature = temperature,
            maxTokens = maxTokens,
            topP = topP,
            frequencyPenalty = frequencyPenalty,
            presencePenalty = presencePenalty,
            stream = stream
        )
    }
}
