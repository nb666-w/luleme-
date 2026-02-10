package com.example.lululu.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onUpdateGoals: (Int, Int) -> Unit,
    onUpdateReminder: (Boolean, Int, Int) -> Unit,
    onOpenAiAnalysis: () -> Unit,
    onExportCsv: () -> Unit,
    onClearData: () -> Unit,
    onSaveApiSettings: (String, String, String) -> Unit,
    currentApiKey: String,
    modifier: Modifier = Modifier
) {
    var dailyGoal by remember { mutableIntStateOf(3) }
    var weeklyGoal by remember { mutableIntStateOf(21) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderHour by remember { mutableIntStateOf(9) }
    var reminderMinute by remember { mutableIntStateOf(0) }

    var showDailyGoalDialog by remember { mutableStateOf(false) }
    var showWeeklyGoalDialog by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAdviceDialog by remember { mutableStateOf(false) }
    var showDonationDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 目标设置
            item {
                SettingsSection(title = "目标设置") {
                    SettingsItem(
                        icon = Icons.Default.Flag,
                        title = "每日目标",
                        subtitle = "每天 $dailyGoal 次",
                        onClick = { showDailyGoalDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.DateRange,
                        title = "每周目标",
                        subtitle = "每周 $weeklyGoal 次",
                        onClick = { showWeeklyGoalDialog = true }
                    )
                }
            }

            // 提醒设置
            item {
                SettingsSection(title = "提醒设置") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "每日提醒",
                        subtitle = if (reminderEnabled) {
                            "每天 ${String.format("%02d", reminderHour)}:${String.format("%02d", reminderMinute)} 提醒"
                        } else {
                            "关闭提醒"
                        },
                        checked = reminderEnabled,
                        onCheckedChange = {
                            reminderEnabled = it
                            onUpdateReminder(it, reminderHour, reminderMinute)
                        },
                        onClick = { showReminderDialog = true }
                    )
                }
            }

            // AI 分析
            item {
                SettingsSection(title = "智能分析") {
                    SettingsItem(
                        icon = Icons.Default.Psychology,
                        title = "AI 健康分析",
                        subtitle = if (currentApiKey.isNotBlank()) "已配置 · 点击运行分析" else "未配置 · 点击设置API Key",
                        onClick = {
                            if (currentApiKey.isBlank()) {
                                showApiKeyDialog = true
                            } else {
                                onOpenAiAnalysis()
                                Toast.makeText(context, "🔄 正在分析...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    SettingsItem(
                        icon = Icons.Default.Key,
                        title = "API Key 设置",
                        subtitle = if (currentApiKey.isNotBlank()) "已设置 · 点击修改" else "未设置",
                        onClick = { showApiKeyDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "每日智能建议",
                        subtitle = "基于你的数据生成个性化建议",
                        onClick = { showAdviceDialog = true }
                    )
                }
            }

            // 数据管理
            item {
                SettingsSection(title = "数据管理") {
                    SettingsItem(
                        icon = Icons.Default.FileDownload,
                        title = "导出数据",
                        subtitle = "将数据导出为CSV到Downloads",
                        onClick = {
                            onExportCsv()
                        }
                    )
                    SettingsItem(
                        icon = Icons.Default.DeleteForever,
                        title = "清除数据",
                        subtitle = "删除所有记录",
                        onClick = { showClearDataDialog = true },
                        isDestructive = true
                    )
                }
            }

            // 账号与社交
            item {
                SettingsSection(title = "账号与社交") {
                    SettingsItem(
                        icon = Icons.Default.Chat,
                        title = "微信登录",
                        subtitle = "绑定微信账号 · 添加好友",
                        onClick = {
                            Toast.makeText(context, "🚧 微信登录功能开发中，敬请期待！", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }

            // 关于
            item {
                SettingsSection(title = "关于") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "版本信息",
                        subtitle = "v1.0.0",
                        onClick = { showVersionDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "隐私政策",
                        subtitle = "查看隐私政策",
                        onClick = { showPrivacyDialog = true }
                    )
                    SettingsItem(
                        icon = Icons.Default.Favorite,
                        title = "支持开发者",
                        subtitle = "请我喝杯咖啡 ☕",
                        onClick = { showDonationDialog = true }
                    )
                }
            }

            // 底部间距
            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    // ==================== 对话框 ====================

    // 每日目标
    if (showDailyGoalDialog) {
        GoalSettingDialog(
            title = "设置每日目标",
            currentValue = dailyGoal,
            range = 1f..10f,
            steps = 8,
            unit = "次/天",
            onValueChange = { dailyGoal = it },
            onConfirm = {
                weeklyGoal = dailyGoal * 7
                onUpdateGoals(dailyGoal, weeklyGoal)
                showDailyGoalDialog = false
            },
            onDismiss = { showDailyGoalDialog = false }
        )
    }

    // 每周目标
    if (showWeeklyGoalDialog) {
        GoalSettingDialog(
            title = "设置每周目标",
            currentValue = weeklyGoal,
            range = 1f..50f,
            steps = 48,
            unit = "次/周",
            onValueChange = { weeklyGoal = it },
            onConfirm = {
                onUpdateGoals(dailyGoal, weeklyGoal)
                showWeeklyGoalDialog = false
            },
            onDismiss = { showWeeklyGoalDialog = false }
        )
    }

    // 提醒时间
    if (showReminderDialog) {
        ReminderSettingDialog(
            hour = reminderHour,
            minute = reminderMinute,
            onHourChange = { reminderHour = it },
            onMinuteChange = { reminderMinute = it },
            onConfirm = {
                onUpdateReminder(reminderEnabled, reminderHour, reminderMinute)
                showReminderDialog = false
            },
            onDismiss = { showReminderDialog = false }
        )
    }

    // API Key 设置
    if (showApiKeyDialog) {
        ApiKeySettingDialog(
            initialApiKey = currentApiKey,
            onSave = { apiKey, baseUrl, modelName ->
                onSaveApiSettings(apiKey, baseUrl, modelName)
                showApiKeyDialog = false
                Toast.makeText(context, "✅ API设置已保存", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showApiKeyDialog = false }
        )
    }

    // 清除数据确认
    if (showClearDataDialog) {
        ClearDataConfirmDialog(
            onConfirm = {
                onClearData()
                showClearDataDialog = false
            },
            onDismiss = { showClearDataDialog = false }
        )
    }

    // 版本信息
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            icon = { Text("🎉", fontSize = 32.sp) },
            title = { Text("撸撸撸 Lululu") },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("版本：v1.0.0", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(4.dp))
                    Text("构建时间：2026-02-10", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("一款帮你记录和分析日常习惯的小工具", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("坚持自律，遇见更好的自己 💪", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                TextButton(onClick = { showVersionDialog = false }) { Text("知道了") }
            }
        )
    }

    // 隐私政策
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("隐私政策") },
            text = {
                Column {
                    val policies = listOf(
                        "📱 所有数据仅存储在您的设备本地",
                        "🔒 我们不会收集或上传您的个人数据",
                        "🤖 AI分析功能需要您主动配置API Key，分析请求仅发送到您指定的AI服务",
                        "👥 好友功能目前使用本地模拟数据",
                        "📊 导出的CSV文件保存在您的Downloads目录",
                        "🗑️ 您可以随时清除所有数据"
                    )
                    policies.forEach { policy ->
                        Text(
                            text = policy,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text("知道了") }
            }
        )
    }

    // 每日智能建议
    if (showAdviceDialog) {
        AlertDialog(
            onDismissRequest = { showAdviceDialog = false },
            icon = { Text("✨", fontSize = 32.sp) },
            title = { Text("每日智能建议") },
            text = {
                Column {
                    if (currentApiKey.isBlank()) {
                        Text(
                            "请先在「API Key 设置」中配置AI服务，即可获得基于你的数据的个性化建议。",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "支持 OpenAI、DeepSeek 等兼容 API",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val tips = listOf(
                            "💡 保持规律的作息时间有助于身心健康",
                            "🏃 适当运动能有效提升精力和情绪",
                            "😊 记录心情变化，关注自己的情绪状态",
                            "📝 写下每次记录的感受，养成反思的好习惯"
                        )
                        tips.forEach { tip ->
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "更多个性化建议请使用「AI健康分析」",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAdviceDialog = false }) { Text("知道了") }
            }
        )
    }

    // 捐赠/打赏
    if (showDonationDialog) {
        DonationDialog(
            onDismiss = { showDonationDialog = false }
        )
    }
}

// ==================== API Key 设置对话框 ====================

@Composable
fun ApiKeySettingDialog(
    initialApiKey: String,
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var apiKey by remember { mutableStateOf(initialApiKey) }
    var baseUrl by remember { mutableStateOf("https://api.openai.com/") }
    var modelName by remember { mutableStateOf("gpt-3.5-turbo") }
    var showKey by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 服务设置") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "支持 OpenAI、DeepSeek、通义千问等兼容 API",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    placeholder = { Text("sk-...") },
                    singleLine = true,
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showKey) "隐藏" else "显示"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = { Text("API Base URL") },
                    placeholder = { Text("https://api.openai.com/") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = modelName,
                    onValueChange = { modelName = it },
                    label = { Text("模型名称") },
                    placeholder = { Text("gpt-3.5-turbo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(apiKey, baseUrl, modelName) },
                enabled = apiKey.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

// ==================== 清除数据确认对话框 ====================

@Composable
fun ClearDataConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                "确认清除所有数据？",
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Column {
                Text("此操作不可恢复！将会删除：")
                Spacer(Modifier.height(8.dp))
                val items = listOf(
                    "📋 所有签到记录",
                    "🏆 所有成就进度（会重置）",
                )
                items.forEach {
                    Text(it, modifier = Modifier.padding(vertical = 2.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "建议先导出CSV备份！",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("确认删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
fun DonationDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("支持开发者")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "感谢你的支持！❤️",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // 使用 coil 加载或者直接用 painterResource，这里是 drawable 所以直接用 Image + painterResource
                // 记得 import androidx.compose.foundation.Image
                // import androidx.compose.ui.res.painterResource
                // import com.example.lululu.R
                // 由于我是直接修改文件，我假设 R 类可用。如果不确定 R 类包名，我需要确认一下 manifest。
                // 包名是 com.example.lululu
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.lululu.R.drawable.donation_qr),
                    contentDescription = "收款码",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // 假设正方形或者自适应
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.FillWidth
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

// ==================== 通用组件 ====================

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDestructive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun GoalSettingDialog(
    title: String,
    currentValue: Int,
    range: ClosedFloatingPointRange<Float> = 1f..10f,
    steps: Int = 8,
    unit: String = "次/天",
    onValueChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$currentValue",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = currentValue.toFloat(),
                    onValueChange = { onValueChange(it.toInt()) },
                    valueRange = range,
                    steps = steps
                )

                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun ReminderSettingDialog(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置提醒时间") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(
                    value = hour,
                    onValueChange = onHourChange,
                    range = 0..23,
                    modifier = Modifier.width(80.dp)
                )

                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                NumberPicker(
                    value = minute,
                    onValueChange = onMinuteChange,
                    range = 0..59,
                    step = 5,
                    modifier = Modifier.width(80.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    step: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                if (value < range.last) onValueChange(value + step)
            }
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "增加",
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            onClick = {
                if (value > range.first) onValueChange(value - step)
            }
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "减少",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
