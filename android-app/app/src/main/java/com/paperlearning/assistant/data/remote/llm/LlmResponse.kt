package com.paperlearning.assistant.data.remote.llm

import com.google.gson.annotations.SerializedName

/**
 * Response model for LLM chat completion API.
 * Compatible with OpenAI API format.
 */
data class LlmResponse(
    @SerializedName("id")
    val id: String?,
    
    @SerializedName("object")
    val `object`: String?,
    
    @SerializedName("created")
    val created: Long?,
    
    @SerializedName("model")
    val model: String?,
    
    @SerializedName("choices")
    val choices: List<Choice>?,
    
    @SerializedName("usage")
    val usage: Usage?,
    
    @SerializedName("error")
    val error: Error?
)

/**
 * Choice model containing the generated message.
 */
data class Choice(
    @SerializedName("index")
    val index: Int?,
    
    @SerializedName("message")
    val message: Message?,
    
    @SerializedName("finish_reason")
    val finishReason: String?
)

/**
 * Usage statistics for the API call.
 */
data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int?,
    
    @SerializedName("completion_tokens")
    val completionTokens: Int?,
    
    @SerializedName("total_tokens")
    val totalTokens: Int?
)

/**
 * Error model for API errors.
 */
data class Error(
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("type")
    val type: String?,
    
    @SerializedName("param")
    val param: String?,
    
    @SerializedName("code")
    val code: String?
)

/**
 * Helper extension to extract the assistant's response text.
 */
fun LlmResponse?.getAssistantResponse(): String? {
    return this?.choices?.firstOrNull()?.message?.content
}

/**
 * Helper extension to check if the response contains an error.
 */
fun LlmResponse?.hasError(): Boolean {
    return this?.error != null
}

/**
 * Helper extension to get the error message if present.
 */
fun LlmResponse?.getErrorMessage(): String? {
    return this?.error?.message ?: "Unknown error"
}
