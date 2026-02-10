package com.example.lululu

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lululu.data.database.LululuDatabase
import com.example.lululu.data.remote.AiAnalyzer
import com.example.lululu.data.repository.AchievementRepository
import com.example.lululu.data.repository.AiSettingsRepository
import com.example.lululu.data.repository.FriendRepository
import com.example.lululu.data.repository.RecordRepository
import com.example.lululu.data.repository.UserGoalRepository
import com.example.lululu.ui.screen.*
import com.example.lululu.ui.theme.LululuTheme
import com.example.lululu.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val database by lazy { LululuDatabase.getInstance(applicationContext) }

    private val recordRepository by lazy { RecordRepository(database.recordDao()) }
    private val userGoalRepository by lazy { UserGoalRepository(database.userGoalDao()) }
    private val achievementRepository by lazy { AchievementRepository(database.achievementDao()) }
    private val friendRepository by lazy { FriendRepository(database.friendDao()) }
    private val aiSettingsRepository by lazy { AiSettingsRepository(applicationContext) }

    private val aiAnalyzer by lazy {
        try {
            AiAnalyzer()
        } catch (e: Exception) {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LululuTheme {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModel.provideFactory(
                        recordRepository = recordRepository,
                        userGoalRepository = userGoalRepository,
                        achievementRepository = achievementRepository,
                        friendRepository = friendRepository,
                        aiSettingsRepository = aiSettingsRepository,
                        aiAnalyzer = aiAnalyzer
                    )
                )

                var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                val uiState by viewModel.uiState.collectAsState()

                // 监听导出/清除/错误
                LaunchedEffect(uiState.exportSuccess) {
                    if (uiState.exportSuccess) {
                        snackbarHostState.showSnackbar("✅ 已导出到 Downloads/${uiState.exportFileName}")
                        viewModel.dismissExportSuccess()
                    }
                }
                LaunchedEffect(uiState.clearSuccess) {
                    if (uiState.clearSuccess) {
                        snackbarHostState.showSnackbar("🗑️ 所有数据已清除")
                        viewModel.dismissClearSuccess()
                    }
                }
                LaunchedEffect(uiState.error) {
                    uiState.error?.let {
                        snackbarHostState.showSnackbar("❌ $it")
                        viewModel.clearError()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    when (currentScreen) {
                        is Screen.Splash -> {
                            SplashScreen(
                                onSplashFinished = {
                                    currentScreen = Screen.Main
                                }
                            )
                        }

                        is Screen.Main -> {
                            MainScreen(
                                todayCount = viewModel.todayCount.collectAsState().value,
                                thisWeekCount = viewModel.thisWeekCount.collectAsState().value,
                                totalCount = viewModel.totalCount.collectAsState().value,
                                todayRecords = viewModel.todayRecords.collectAsState().value,
                                dailyAdvice = viewModel.dailyAdvice.collectAsState().value,
                                onCheckIn = { mood, note, imageUri, timestamp ->
                                    viewModel.checkIn(mood, note, imageUri, timestamp)
                                },
                                onDeleteRecord = { id ->
                                    viewModel.deleteRecord(id)
                                },
                                onNavigateToStats = {
                                    currentScreen = Screen.Stats
                                },
                                onNavigateToAchievements = {
                                    currentScreen = Screen.Achievements
                                },
                                onNavigateToFriends = {
                                    currentScreen = Screen.Friends
                                },
                                onNavigateToSettings = {
                                    currentScreen = Screen.Settings
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Stats -> {
                            StatsScreen(
                                todayCount = viewModel.todayCount.collectAsState().value,
                                thisWeekCount = viewModel.thisWeekCount.collectAsState().value,
                                totalCount = viewModel.totalCount.collectAsState().value,
                                todayRecords = viewModel.todayRecords.collectAsState().value,
                                currentStreak = viewModel.currentStreak.collectAsState().value,
                                weeklyAverage = viewModel.weeklyAverage.collectAsState().value,
                                onNavigateBack = {
                                    currentScreen = Screen.Main
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Achievements -> {
                            AchievementsScreen(
                                achievements = viewModel.allAchievements.collectAsState().value,
                                unlockedCount = viewModel.unlockedCount.collectAsState().value,
                                onNavigateBack = {
                                    currentScreen = Screen.Main
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Friends -> {
                            FriendsScreen(
                                friends = viewModel.allFriends.collectAsState().value,
                                myTodayCount = viewModel.todayCount.collectAsState().value,
                                myThisWeekCount = viewModel.thisWeekCount.collectAsState().value,
                                myTotalCount = viewModel.totalCount.collectAsState().value,
                                onNavigateBack = {
                                    currentScreen = Screen.Main
                                },
                                onAddFriend = { nickname, emoji ->
                                    viewModel.addFriend(nickname, emoji)
                                },
                                onRemoveFriend = { id ->
                                    viewModel.removeFriend(id)
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is Screen.Settings -> {
                            SettingsScreen(
                                onNavigateBack = {
                                    currentScreen = Screen.Main
                                },
                                onUpdateGoals = { daily, weekly ->
                                    viewModel.updateGoals(daily, weekly)
                                },
                                onUpdateReminder = { enabled, hour, minute ->
                                    viewModel.updateReminder(enabled, hour, minute)
                                },
                                onOpenAiAnalysis = {
                                    viewModel.fetchHealthAnalysis()
                                },
                                onExportCsv = {
                                    viewModel.exportDataToCsv(context)
                                },
                                onClearData = {
                                    viewModel.clearAllData()
                                },
                                onSaveApiSettings = { apiKey, baseUrl, modelName ->
                                    viewModel.saveApiSettings(apiKey, baseUrl, modelName)
                                },
                                currentApiKey = viewModel.apiKey.collectAsState().value,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    data object Splash : Screen()
    data object Main : Screen()
    data object Stats : Screen()
    data object Achievements : Screen()
    data object Friends : Screen()
    data object Settings : Screen()
}
