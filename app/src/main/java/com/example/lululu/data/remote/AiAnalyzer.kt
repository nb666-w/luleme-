package com.example.lululu.data.remote

import com.example.lululu.data.entity.RecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * AI 健康分析器
 * 封装AI API调用逻辑
 */
class AiAnalyzer(
    private val baseUrl: String = "https://api.openai.com/",
    private val apiKey: String = ""
) {

    private val client: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val service: AiAnalysisService by lazy {
        retrofit.create(AiAnalysisService::class.java)
    }

    /**
     * 分析健康数据
     * @param records 最近的行为记录
     * @param totalCount 总次数
     * @param currentStreak 连续天数
     * @return AI分析结果
     */
    suspend fun analyzeHealth(
        records: List<RecordEntity>,
        totalCount: Int,
        currentStreak: Int
    ): Result<HealthAnalysisResult> = withContext(Dispatchers.IO) {
        try {
            val recentRecords = records.take(30)
            val hourlyDistribution = recentRecords.groupBy { it.hour }.mapValues { it.value.size }
            val weekdayDistribution = recentRecords.groupBy { it.weekday }.mapValues { it.value.size }
            val moodDistribution = recentRecords.map { it.mood }.average()

            val prompt = buildHealthAnalysisPrompt(
                totalCount = totalCount,
                currentStreak = currentStreak,
                hourlyDistribution = hourlyDistribution,
                weekdayDistribution = weekdayDistribution,
                averageMood = moodDistribution
            )

            val request = HealthAnalysisRequest(
                messages = listOf(
                    Message(role = "system", content = SYSTEM_PROMPT),
                    Message(role = "user", content = prompt)
                )
            )

            val response = service.analyzeHealth(request)
            val content = response.choices.firstOrNull()?.message?.content ?: ""

            Result.success(parseAnalysisResult(content))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取个性化建议
     */
    suspend fun getAdvice(
        currentHour: Int,
        hasTodayRecord: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = if (hasTodayRecord) {
                "现在是${currentHour}点，你今天已经完成记录了。给出一句简短的鼓励或健康提醒（50字以内）。"
            } else {
                "现在是${currentHour}点，你今天还没有记录。给出一句温馨的提醒（50字以内），鼓励完成今日目标。"
            }

            val request = AdviceRequest(
                messages = listOf(
                    Message(role = "system", content = "你是一个健康管理助手，请给出简短、温暖、专业的建议。"),
                    Message(role = "user", content = prompt)
                ),
                maxTokens = 100
            )

            val response = service.getAdvice(request)
            val content = response.choices.firstOrNull()?.message?.content ?: ""

            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildHealthAnalysisPrompt(
        totalCount: Int,
        currentStreak: Int,
        hourlyDistribution: Map<Int, Int>,
        weekdayDistribution: Map<Int, Int>,
        averageMood: Double
    ): String {
        val peakHour = hourlyDistribution.maxByOrNull { it.value }?.key ?: -1
        val peakWeekday = weekdayDistribution.maxByOrNull { it.value }?.key ?: -1

        return """
            请分析以下健康数据并给出建议：

            基本统计：
            - 总记录次数：$totalCount
            - 当前连续天数：$${'$'}currentStreak天
            - 平均心情评分（1-5）：${'$'}{'$'}{averageMood.format(1)}

            活跃时间分析：
            - 高峰时段：${'$'}{if (peakHour >= 0) "${peakHour}:00" else "暂无数据"}
            - 活跃星期：${'$'}{if (peakWeekday > 0) "周$peakWeekday" else "暂无数据"}

            请从以下方面给出分析和建议（用JSON格式回复）：
            1. 整体健康状况评估
            2. 时间规律性分析
            3. 改善建议（3条）
            4. 风险提示（如有）
        """.trimIndent()
    }

    private fun parseAnalysisResult(content: String): HealthAnalysisResult {
        // 简化解析逻辑，实际应使用JSON解析
        return HealthAnalysisResult(
            assessment = content.take(100),
            suggestions = listOf("保持规律作息", "适度运动", "保持良好心态"),
            riskWarnings = emptyList()
        )
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)

    companion object {
        private const val SYSTEM_PROMPT = """
            你是一个专业的健康管理助手。请根据用户提供的行为记录数据，
            从健康角度给出专业、温暖、实用的建议。

            分析维度：
            1. 频率是否适度
            2. 时间是否规律
            3. 是否影响日常生活
            4. 心理健康状况

            请用JSON格式回复，包含以下字段：
            - assessment: 整体评估
            - suggestions: 建议列表
            - riskWarnings: 风险提示列表（如有）
            - encouragement: 鼓励话语
        """
    }
}

/**
 * 健康分析结果
 */
data class HealthAnalysisResult(
    val assessment: String,
    val suggestions: List<String>,
    val riskWarnings: List<String>,
    val encouragement: String = ""
)
