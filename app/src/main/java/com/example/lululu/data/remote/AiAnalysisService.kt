package com.example.lululu.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers

/**
 * AI API 接口定义
 * 支持 OpenAI GPT 和其他兼容 API
 */
interface AiAnalysisService {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun analyzeHealth(@Body request: HealthAnalysisRequest): HealthAnalysisResponse

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getAdvice(@Body request: AdviceRequest): AdviceResponse
}

@JsonClass(generateAdapter = true)
data class HealthAnalysisRequest(
    @Json(name = "model") val model: String = "gpt-3.5-turbo",
    @Json(name = "messages") val messages: List<Message>,
    @Json(name = "max_tokens") val maxTokens: Int = 500,
    @Json(name = "temperature") val temperature: Double = 0.7
)

@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class HealthAnalysisResponse(
    @Json(name = "id") val id: String,
    @Json(name = "choices") val choices: List<Choice>
)

@JsonClass(generateAdapter = true)
data class Choice(
    @Json(name = "message") val message: Message
)

@JsonClass(generateAdapter = true)
data class AdviceRequest(
    @Json(name = "model") val model: String = "gpt-3.5-turbo",
    @Json(name = "messages") val messages: List<Message>,
    @Json(name = "max_tokens") val maxTokens: Int = 300,
    @Json(name = "temperature") val temperature: Double = 0.7
)

@JsonClass(generateAdapter = true)
data class AdviceResponse(
    @Json(name = "id") val id: String,
    @Json(name = "choices") val choices: List<Choice>
)
