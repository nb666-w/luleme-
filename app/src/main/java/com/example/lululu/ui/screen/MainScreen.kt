package com.example.lululu.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lululu.data.entity.RecordEntity
import com.example.lululu.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    todayCount: Int,
    thisWeekCount: Int,
    totalCount: Int,
    todayRecords: List<RecordEntity>,
    dailyAdvice: String,
    onCheckIn: (Int, String, String?, Long) -> Unit,
    onDeleteRecord: (Long) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToFriends: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCheckInDialog by remember { mutableStateOf(false) }
    var showOverLimitDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var checkInTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedMood by remember { mutableIntStateOf(3) }

    val currentHour = remember { LocalDateTime.now().hour }
    val greeting = remember {
        when (currentHour) {
            in 5..11 -> "早上好 ☀️"
            in 12..13 -> "中午好 🌤️"
            in 14..17 -> "下午好 🌅"
            in 18..22 -> "晚上好 🌙"
            else -> "夜深了 💤"
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 顶部问候区域
            item {
                HeaderSection(
                    greeting = greeting,
                    onSettingsClick = onNavigateToSettings
                )
            }

            // 状态卡片（微信风格）
            item {
                StatusCard(
                    todayCount = todayCount,
                    dailyAdvice = dailyAdvice
                )
            }

            // 今日进度环形卡片
            item {
                ProgressRingCard(
                    todayCount = todayCount,
                    dailyGoal = 5,
                    thisWeekCount = thisWeekCount,
                    totalCount = totalCount
                )
            }

            // 签到按钮
            item {
                ModernCheckInButton(
                    onClick = {
                        if (todayCount >= 100) {
                            showOverLimitDialog = true
                        } else {
                            checkInTimestamp = System.currentTimeMillis()
                            showCheckInDialog = true
                        }
                    },
                    enabled = true, // 总是启用，由点击事件判断是否超限
                    todayCount = todayCount
                )
                
                // 补签按钮
                TextButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("忘记打卡？补签 📅", color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                }
            }

            // 快捷功能区
            item {
                QuickAccessGrid(
                    onNavigateToStats = onNavigateToStats,
                    onNavigateToAchievements = onNavigateToAchievements,
                    onNavigateToFriends = onNavigateToFriends
                )
            }

            // 今日时间线
            if (todayRecords.isNotEmpty()) {
                item {
                    Text(
                        text = "今日时间线",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(todayRecords) { record ->
                    TimelineRecordItem(
                        record = record,
                        onDelete = { onDeleteRecord(record.id) }
                    )
                }
            }
        }
    }

    // 签到对话框
    if (showCheckInDialog) {
        ModernCheckInDialog(
            initialMood = selectedMood,
            checkInDate = if (kotlin.math.abs(System.currentTimeMillis() - checkInTimestamp) > 60000) checkInTimestamp else null, // 仅在补签时显示日期
            onConfirm = { mood, note, imageUri ->
                onCheckIn(mood, note, imageUri, checkInTimestamp)
                showCheckInDialog = false
            },
            onDismiss = { showCheckInDialog = false },
            onMoodSelected = { selectedMood = it }
        )
    }

    // 超限提醒
    if (showOverLimitDialog) {
        val funnyMessages = listOf(
            "兄弟/集美，你的手不痛吗？铁杵都磨成针了！歇会儿吧，明天再战！😂",
            "生产队的驴都不敢这么歇人不歇磨，休息一下吧！🫏",
            "根据《Lululu健康法》第100条，你今天已经过于自律，被强制休息！👮‍♂️",
            "再签下去，App 服务器都要被你点冒烟了！🔥",
            "你是魔鬼吗？100次了！放过自己，也放过我吧！🙏"
        )
        AlertDialog(
            onDismissRequest = { showOverLimitDialog = false },
            icon = { Text("🛑", fontSize = 48.sp) },
            title = { Text("自律虽好，可不要贪杯哦") },
            text = {
                Text(
                    text = funnyMessages.random(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(onClick = { showOverLimitDialog = false }) { Text("朕知道了") }
            }
        )
    }

    // 日期选择器
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            checkInTimestamp = it + (System.currentTimeMillis() % 86400000) // 保持当前时间点
                            showDatePicker = false
                            showCheckInDialog = true
                        }
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ==================== 顶部区域 ====================
@Composable
fun HeaderSection(
    greeting: String,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "保持健康的生活习惯",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // 设置按钮
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                Icons.Outlined.Settings,
                contentDescription = "设置",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ==================== 微信状态卡片 ====================
@Composable
fun StatusCard(
    todayCount: Int,
    dailyAdvice: String
) {
    val statusEmoji = when {
        todayCount == 0 -> "🌱"
        todayCount <= 2 -> "🌿"
        todayCount <= 4 -> "🌳"
        else -> "🏆"
    }

    val statusText = when {
        todayCount == 0 -> "新的一天，元气满满"
        todayCount <= 2 -> "已记录 $todayCount 次，继续加油"
        todayCount <= 4 -> "状态不错，保持节奏"
        else -> "今日记录充分，注意休息"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = PrimaryBlue.copy(alpha = 0.1f),
                spotColor = PrimaryBlue.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // 状态表情 - 微信风格大表情
            Text(
                text = statusEmoji,
                fontSize = 52.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 状态文字
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 每日建议
            if (dailyAdvice.isNotEmpty()) {
                Text(
                    text = dailyAdvice,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ==================== 进度环形卡片 ====================
@Composable
fun ProgressRingCard(
    todayCount: Int,
    dailyGoal: Int,
    thisWeekCount: Int,
    totalCount: Int
) {
    val progress = (todayCount.toFloat() / dailyGoal).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "今日进度",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 进度条区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 数字
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$todayCount",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = " / $dailyGoal",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 完成百分比
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (progress >= 1f) HealthGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 圆角进度条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(NeutralGray200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(PrimaryBlue, PrimaryBlueLight)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 底部统计数据
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStatItem(
                    label = "本周",
                    value = "$thisWeekCount",
                    icon = Icons.Outlined.DateRange
                )

                // 分隔线
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(NeutralGray200)
                )

                MiniStatItem(
                    label = "累计",
                    value = "$totalCount",
                    icon = Icons.Outlined.CheckCircle
                )
            }
        }
    }
}

@Composable
fun MiniStatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

// ==================== 签到按钮 ====================
@Composable
fun ModernCheckInButton(
    onClick: () -> Unit,
    enabled: Boolean,
    todayCount: Int
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.97f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue,
            disabledContainerColor = NeutralGray300
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (todayCount == 0) "开始记录" else "再记一次",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== 快捷功能区 ====================
@Composable
fun QuickAccessGrid(
    onNavigateToStats: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToFriends: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernQuickCard(
                title = "数据统计",
                subtitle = "查看趋势",
                icon = Icons.Outlined.Insights,
                backgroundColor = StatusCardBlue,
                iconTint = SkyBlue,
                onClick = onNavigateToStats,
                modifier = Modifier.weight(1f)
            )
            ModernQuickCard(
                title = "成就殿堂",
                subtitle = "查看勋章",
                icon = Icons.Outlined.EmojiEvents,
                backgroundColor = StatusCardOrange,
                iconTint = WarmOrange,
                onClick = onNavigateToAchievements,
                modifier = Modifier.weight(1f)
            )
        }
        ModernQuickCard(
            title = "好友圈",
            subtitle = "PK对决 · 排行榜 · 看看谁更能撸",
            icon = Icons.Outlined.Group,
            backgroundColor = StatusCardPurple,
            iconTint = SoftPurple,
            onClick = onNavigateToFriends,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernQuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

// ==================== 时间线记录项 ====================
@Composable
fun TimelineRecordItem(
    record: RecordEntity,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 时间线左侧
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Text(
                text = record.time,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 记录卡片
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 心情表情
                    Text(
                        text = getMoodEmoji(record.mood),
                        fontSize = 28.sp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = getMoodText(record.mood),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (record.note.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = record.note,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "删除",
                            tint = NeutralGray400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 显示附加图片
                if (!record.imageUri.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(Uri.parse(record.imageUri))
                            .crossfade(true)
                            .build(),
                        contentDescription = "附加图片",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

// ==================== 签到对话框 ====================
@Composable
fun ModernCheckInDialog(
    initialMood: Int,
    checkInDate: Long?,
    onMoodSelected: (Int) -> Unit,
    onConfirm: (Int, String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var note by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // 如果是补签，显示日期格式化字符串
    val dateDisplay = remember(checkInDate) {
        if (checkInDate != null) {
            val dateTime = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(checkInDate), java.time.ZoneId.systemDefault())
            dateTime.format(DateTimeFormatter.ofPattern("M月d日 H:mm"))
        } else {
            null
        }
    }

    // 图片选择器
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (dateDisplay != null) "补签：$dateDisplay" else "记录此刻",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "你现在感觉怎么样？",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 心情选择 - 大表情
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..5).forEach { mood ->
                        ModernMoodButton(
                            mood = mood,
                            isSelected = initialMood == mood,
                            onClick = { onMoodSelected(mood) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 心情描述文字
                Text(
                    text = getMoodText(initialMood),
                    style = MaterialTheme.typography.bodySmall,
                    color = getMoodColor(initialMood),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = {
                        Text(
                            "写点什么吧...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = NeutralGray200
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 图片附加区域
                if (selectedImageUri != null) {
                    // 已选择图片 - 预览
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp)
                            .clip(RoundedCornerShape(14.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "已选图片",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        // 删除按钮
                        IconButton(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(28.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "移除图片",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    // 未选择图片 - 添加按钮
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(NeutralGray300, NeutralGray300)
                            )
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    ) {
                        Icon(
                            Icons.Outlined.AddPhotoAlternate,
                            contentDescription = "添加图片",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("添加图片", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(initialMood, note, selectedImageUri?.toString()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text(
                    "确认记录",
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "取消",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    )
}

@Composable
fun ModernMoodButton(
    mood: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "moodScale"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isSelected) getMoodColor(mood).copy(alpha = 0.15f) else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getMoodEmoji(mood),
            fontSize = if (isSelected) 30.sp else 26.sp
        )
    }
}

// ==================== 颜色和文本工具 ====================
fun getMoodColor(mood: Int): Color {
    return when (mood) {
        1 -> MoodTired
        2 -> MoodSad
        3 -> MoodNeutral
        4 -> MoodCalm
        5 -> MoodHappy
        else -> MoodNeutral
    }
}

fun getMoodEmoji(mood: Int): String {
    return when (mood) {
        1 -> "😫"
        2 -> "😔"
        3 -> "😐"
        4 -> "🙂"
        5 -> "😄"
        else -> "😐"
    }
}

fun getMoodText(mood: Int): String {
    return when (mood) {
        1 -> "疲惫"
        2 -> "低落"
        3 -> "一般"
        4 -> "不错"
        5 -> "很棒"
        else -> "一般"
    }
}
