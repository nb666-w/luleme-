package com.example.lululu.ui.viewmodel

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lululu.data.entity.AchievementEntity
import com.example.lululu.data.entity.FriendEntity
import com.example.lululu.data.entity.RecordEntity
import com.example.lululu.data.entity.UserGoalEntity
import com.example.lululu.data.remote.AiAnalyzer
import com.example.lululu.data.remote.HealthAnalysisResult
import com.example.lululu.data.repository.AchievementRepository
import com.example.lululu.data.repository.AiSettingsRepository
import com.example.lululu.data.repository.FriendRepository
import com.example.lululu.data.repository.RecordRepository
import com.example.lululu.data.repository.UserGoalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * 主ViewModel
 * 管理应用的核心状态和业务逻辑
 */
class MainViewModel(
    private val recordRepository: RecordRepository,
    private val userGoalRepository: UserGoalRepository,
    private val achievementRepository: AchievementRepository,
    private val friendRepository: FriendRepository,
    private val aiSettingsRepository: AiSettingsRepository,
    private var aiAnalyzer: AiAnalyzer? = null
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // 签到相关
    val todayRecords: StateFlow<List<RecordEntity>> = recordRepository
        .getRecordsByDate(LocalDate.now())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCount: StateFlow<Int> = recordRepository
        .getTodayCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val thisWeekCount: StateFlow<Int> = recordRepository
        .getThisWeekCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: StateFlow<Int> = recordRepository
        .getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // 目标相关
    val userGoal: StateFlow<UserGoalEntity?> = userGoalRepository
        .getGoal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 成就相关
    val allAchievements: StateFlow<List<AchievementEntity>> = achievementRepository
        .getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unlockedCount: StateFlow<Int> = achievementRepository
        .getUnlockedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // 好友相关
    val allFriends: StateFlow<List<FriendEntity>> = friendRepository
        .getAllFriends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friendCount: StateFlow<Int> = friendRepository
        .getFriendCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // AI分析结果
    private val _aiAnalysisResult = MutableStateFlow<HealthAnalysisResult?>(null)
    val aiAnalysisResult: StateFlow<HealthAnalysisResult?> = _aiAnalysisResult.asStateFlow()

    // 今日建议
    private val _dailyAdvice = MutableStateFlow<String>("")
    val dailyAdvice: StateFlow<String> = _dailyAdvice.asStateFlow()

    // 连续签到天数
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    // 7日平均
    private val _weeklyAverage = MutableStateFlow(0.0)
    val weeklyAverage: StateFlow<Double> = _weeklyAverage.asStateFlow()

    // AI配置状态
    val apiKey: StateFlow<String> = aiSettingsRepository.apiKeyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    init {
        initializeData()
    }

    /**
     * 初始化数据
     */
    private fun initializeData() {
        viewModelScope.launch {
            userGoalRepository.ensureDefaultGoal()
            achievementRepository.initializeDefaultAchievements()
            friendRepository.initializeDemoFriends()
            refreshStats()
            initAiAnalyzer()
        }
    }

    /**
     * 刷新统计数据
     */
    private suspend fun refreshStats() {
        _currentStreak.value = recordRepository.getConsecutiveDays()
        _weeklyAverage.value = recordRepository.getAveragePerDay(7)
    }

    /**
     * 初始化AI分析器（使用保存的配置）
     */
    private suspend fun initAiAnalyzer() {
        val key = aiSettingsRepository.getApiKey()
        val url = aiSettingsRepository.getBaseUrl()
        if (key.isNotBlank()) {
            aiAnalyzer = AiAnalyzer(baseUrl = url, apiKey = key)
        }
    }

    /**
     * 签到
     */
    fun checkIn(mood: Int = 3, note: String = "", imageUri: String? = null, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                recordRepository.addRecord(mood = mood, note = note, imageUri = imageUri, timestamp = timestamp)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        lastCheckInTime = System.currentTimeMillis(),
                        showCheckInSuccess = true
                    )
                }

                // 刷新统计
                refreshStats()

                // 检查成就（使用真实streak）
                checkAchievements()

                // 检查特殊成就
                checkSpecialAchievements(mood)

                // 更新建议
                fetchDailyAdvice()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    /**
     * 删除记录
     */
    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            recordRepository.deleteRecord(id)
            refreshStats()
        }
    }

    /**
     * 更新目标
     */
    fun updateGoals(daily: Int, weekly: Int) {
        viewModelScope.launch {
            userGoalRepository.updateGoals(daily, weekly)
        }
    }

    /**
     * 更新提醒设置
     */
    fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            userGoalRepository.updateReminder(enabled, hour, minute)
        }
    }

    /**
     * 添加好友
     */
    fun addFriend(nickname: String, emoji: String) {
        viewModelScope.launch {
            val friend = FriendEntity(
                id = UUID.randomUUID().toString(),
                nickname = nickname,
                avatarEmoji = emoji,
                todayCount = (0..3).random(),
                thisWeekCount = (0..15).random(),
                totalCount = (0..100).random(),
                currentStreak = (0..10).random(),
                unlockedAchievements = (0..8).random()
            )
            friendRepository.addFriend(friend)

            val count = friendCount.value + 1
            if (count >= 5) {
                achievementRepository.unlock("social_butterfly")
            }
            achievementRepository.updateProgress("social_butterfly", count.coerceAtMost(5))
        }
    }

    /**
     * 删除好友
     */
    fun removeFriend(id: String) {
        viewModelScope.launch {
            friendRepository.removeFriend(id)
        }
    }

    // ==================== 导出 CSV ====================

    /**
     * 导出数据到 CSV 文件
     */
    fun exportDataToCsv(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val csvContent = recordRepository.generateCsvContent()
                val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                val fileName = "lululu_export_$timestamp.csv"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ 使用 MediaStore
                    val values = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = context.contentResolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
                    )
                    uri?.let {
                        context.contentResolver.openOutputStream(it)?.use { out ->
                            out.write(csvContent.toByteArray(Charsets.UTF_8))
                        }
                    }
                } else {
                    // Android 9- 直接写文件
                    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(dir, fileName)
                    file.writeText(csvContent, Charsets.UTF_8)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        exportSuccess = true,
                        exportFileName = fileName
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "导出失败: ${e.message}")
                }
            }
        }
    }

    // ==================== 清除数据 ====================

    /**
     * 清除所有数据
     */
    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                recordRepository.deleteAllRecords()
                achievementRepository.resetAll()
                _currentStreak.value = 0
                _weeklyAverage.value = 0.0
                _uiState.update {
                    it.copy(isLoading = false, clearSuccess = true)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "清除失败: ${e.message}")
                }
            }
        }
    }

    // ==================== AI API 设置 ====================

    /**
     * 保存 AI API 设置
     */
    fun saveApiSettings(apiKey: String, baseUrl: String, modelName: String) {
        viewModelScope.launch {
            aiSettingsRepository.saveAll(apiKey, baseUrl, modelName)
            // 重新初始化 AI 分析器
            if (apiKey.isNotBlank()) {
                aiAnalyzer = AiAnalyzer(
                    baseUrl = baseUrl.ifBlank { AiSettingsRepository.DEFAULT_BASE_URL },
                    apiKey = apiKey
                )
            }
        }
    }

    /**
     * 获取AI健康分析
     */
    fun fetchHealthAnalysis() {
        if (aiAnalyzer == null) {
            _aiAnalysisResult.value = getDefaultAnalysis()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true) }
            try {
                val records = todayRecords.value
                val result = aiAnalyzer!!.analyzeHealth(
                    records = records,
                    totalCount = totalCount.value,
                    currentStreak = _currentStreak.value
                )
                result.onSuccess { analysis ->
                    _aiAnalysisResult.value = analysis
                }.onFailure {
                    _aiAnalysisResult.value = getDefaultAnalysis()
                    _uiState.update { it.copy(error = "AI分析失败，请检查API Key设置") }
                }
            } catch (e: Exception) {
                _aiAnalysisResult.value = getDefaultAnalysis()
            } finally {
                _uiState.update { it.copy(isAnalyzing = false) }
            }
        }
    }

    /**
     * 获取每日建议
     */
    fun fetchDailyAdvice() {
        if (aiAnalyzer == null) {
            _dailyAdvice.value = getDefaultAdvice(todayCount.value > 0)
            return
        }

        viewModelScope.launch {
            val currentHour = LocalDateTime.now().hour
            val hasRecord = todayCount.value > 0

            val result = aiAnalyzer!!.getAdvice(currentHour, hasRecord)
            result.onSuccess { advice ->
                _dailyAdvice.value = advice
            }.onFailure {
                _dailyAdvice.value = getDefaultAdvice(hasRecord)
            }
        }
    }

    /**
     * 检查成就解锁（使用真实连续天数）
     */
    private suspend fun checkAchievements() {
        val total = totalCount.value
        val streak = _currentStreak.value

        achievementRepository.checkAchievements(
            totalCount = total,
            currentStreak = streak
        )
    }

    /**
     * 检查特殊成就
     */
    private suspend fun checkSpecialAchievements(mood: Int) {
        val hour = LocalDateTime.now().hour
        val today = todayCount.value
        val noteCount = todayRecords.value.count { it.note.isNotEmpty() }

        achievementRepository.checkSpecialAchievements(
            hour = hour,
            todayCount = today,
            mood = mood,
            noteCount = noteCount
        )
    }

    // ==================== UI State Management ====================

    fun dismissCheckInSuccess() {
        _uiState.update { it.copy(showCheckInSuccess = false) }
    }

    fun dismissExportSuccess() {
        _uiState.update { it.copy(exportSuccess = false, exportFileName = null) }
    }

    fun dismissClearSuccess() {
        _uiState.update { it.copy(clearSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun getDefaultAdvice(hasRecord: Boolean): String {
        return if (hasRecord) {
            "今天已完成记录，继续保持自律的生活节奏！"
        } else {
            "今天还没记录哦，抽出时间关注一下自己的健康吧。"
        }
    }

    private fun getDefaultAnalysis(): HealthAnalysisResult {
        return HealthAnalysisResult(
            assessment = "数据积累中，继续记录以获取更精准的分析",
            suggestions = listOf(
                "保持规律的作息时间",
                "适度运动，增强体质",
                "保持积极乐观的心态"
            ),
            riskWarnings = emptyList(),
            encouragement = "坚持就是胜利！"
        )
    }

    companion object {
        fun provideFactory(
            recordRepository: RecordRepository,
            userGoalRepository: UserGoalRepository,
            achievementRepository: AchievementRepository,
            friendRepository: FriendRepository,
            aiSettingsRepository: AiSettingsRepository,
            aiAnalyzer: AiAnalyzer? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(
                    recordRepository,
                    userGoalRepository,
                    achievementRepository,
                    friendRepository,
                    aiSettingsRepository,
                    aiAnalyzer
                ) as T
            }
        }
    }
}

/**
 * 主UI状态
 */
data class MainUiState(
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val error: String? = null,
    val lastCheckInTime: Long = 0,
    val showCheckInSuccess: Boolean = false,
    val exportSuccess: Boolean = false,
    val exportFileName: String? = null,
    val clearSuccess: Boolean = false
)
