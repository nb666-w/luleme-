package com.example.lululu.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lululu.data.entity.RecordEntity
import com.example.lululu.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    todayCount: Int,
    thisWeekCount: Int,
    totalCount: Int,
    todayRecords: List<RecordEntity>,
    currentStreak: Int,
    weeklyAverage: Double,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasData = totalCount > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "数据统计",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (hasData) {
                // 统计概览
                item {
                    RealStatsOverviewCard(
                        todayCount = todayCount,
                        thisWeekCount = thisWeekCount,
                        totalCount = totalCount,
                        weeklyAverage = weeklyAverage
                    )
                }

                // 连续签到 + 日均
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StreakMiniCard(
                            streak = currentStreak,
                            modifier = Modifier.weight(1f)
                        )
                        DailyAverageMiniCard(
                            average = weeklyAverage,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 今日签到时间线
                if (todayRecords.isNotEmpty()) {
                    item {
                        TodayTimelineCard(records = todayRecords)
                    }
                }

                // 时间分布图
                item {
                    RealTimeDistributionCard(records = todayRecords)
                }

                // 趋势分析卡片
                item {
                    TrendAnalysisCard(
                        todayCount = todayCount,
                        thisWeekCount = thisWeekCount,
                        currentStreak = currentStreak,
                        weeklyAverage = weeklyAverage
                    )
                }

                // 心情分布
                if (todayRecords.isNotEmpty()) {
                    item {
                        MoodDistributionCard(records = todayRecords)
                    }
                }

                // 成就里程碑
                item {
                    MilestoneCard(totalCount = totalCount)
                }

                item { Spacer(Modifier.height(20.dp)) }
            } else {
                item {
                    EmptyStatsView()
                }
            }
        }
    }
}

// ==================== 空状态 ====================
@Composable
fun EmptyStatsView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "📊", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "还没有数据",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "开始记录后，这里将展示你的数据分析",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EmptyFeatureHint(emoji = "📈", label = "趋势分析")
                EmptyFeatureHint(emoji = "⏰", label = "时间分布")
                EmptyFeatureHint(emoji = "😊", label = "心情统计")
            }
        }
    }
}

@Composable
fun EmptyFeatureHint(emoji: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(NeutralGray100),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NeutralGray500
        )
    }
}

// ==================== 统计概览卡片 ====================
@Composable
fun RealStatsOverviewCard(
    todayCount: Int,
    thisWeekCount: Int,
    totalCount: Int,
    weeklyAverage: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            PrimaryGreen,
                            PrimaryGreen.copy(alpha = 0.85f),
                            PrimaryGreenLight.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = "📊 统计概览",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewStatColumn(
                    value = "$todayCount",
                    label = "今日",
                    icon = Icons.Outlined.Today
                )
                OverviewStatColumn(
                    value = "$thisWeekCount",
                    label = "本周",
                    icon = Icons.Outlined.DateRange
                )
                OverviewStatColumn(
                    value = String.format("%.1f", weeklyAverage),
                    label = "日均",
                    icon = Icons.Outlined.TrendingUp
                )
                OverviewStatColumn(
                    value = "$totalCount",
                    label = "累计",
                    icon = Icons.Outlined.CheckCircle
                )
            }
        }
    }
}

@Composable
fun OverviewStatColumn(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

// ==================== 连续签到迷你卡 ====================
@Composable
fun StreakMiniCard(streak: Int, modifier: Modifier = Modifier) {
    val emoji = when {
        streak >= 30 -> "🏆"
        streak >= 14 -> "🔥"
        streak >= 7 -> "⚡"
        streak >= 3 -> "💪"
        streak >= 1 -> "✅"
        else -> "😴"
    }
    val description = when {
        streak >= 30 -> "传奇连续"
        streak >= 14 -> "势不可挡"
        streak >= 7 -> "一周挑战达成"
        streak >= 3 -> "小有成就"
        streak >= 1 -> "继续保持"
        else -> "赶紧开始"
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (streak > 0) StatusCardOrange else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${streak}天",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (streak > 0) WarmOrange else NeutralGray500
            )
            Text(
                text = "连续签到",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = if (streak > 0) WarmOrange.copy(alpha = 0.8f) else NeutralGray400
            )
        }
    }
}

// ==================== 日均迷你卡 ====================
@Composable
fun DailyAverageMiniCard(average: Double, modifier: Modifier = Modifier) {
    val emoji = when {
        average >= 5 -> "🚀"
        average >= 3 -> "📈"
        average >= 1 -> "📊"
        else -> "📉"
    }
    val comment = when {
        average >= 5 -> "效率极高"
        average >= 3 -> "稳定发挥"
        average >= 1 -> "慢慢来"
        else -> "有待提升"
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = StatusCardBlue
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = String.format("%.1f", average),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SkyBlue
            )
            Text(
                text = "7 日均次",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = comment,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = SkyBlue.copy(alpha = 0.8f)
            )
        }
    }
}

// ==================== 今日时间线 ====================
@Composable
fun TodayTimelineCard(records: List<RecordEntity>) {
    val moodEmojis = listOf("😫", "😔", "😐", "🙂", "😄")

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
                text = "📋 今日时间线",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(16.dp))

            records.take(10).forEachIndexed { index, record ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 时间轴圆点
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(40.dp)
                    ) {
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(8.dp)
                                    .background(PrimaryGreen.copy(alpha = 0.2f))
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == 0) PrimaryGreen else PrimaryGreen.copy(alpha = 0.4f)
                                )
                        )
                        if (index < records.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(8.dp)
                                    .background(PrimaryGreen.copy(alpha = 0.2f))
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    // 时间
                    Text(
                        text = record.time,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(50.dp)
                    )

                    // 心情
                    val moodIdx = (record.mood - 1).coerceIn(0, 4)
                    Text(
                        text = moodEmojis[moodIdx],
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.width(8.dp))

                    // 备注
                    if (record.note.isNotBlank()) {
                        Text(
                            text = record.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (records.size > 10) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "... 还有 ${records.size - 10} 条记录",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeutralGray400,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==================== 时间分布卡片 ====================
@Composable
fun RealTimeDistributionCard(records: List<RecordEntity>) {
    val hourlyData = remember(records) {
        val hourCounts = IntArray(24)
        records.forEach { record ->
            hourCounts[record.hour] = hourCounts[record.hour] + 1
        }
        hourCounts.toList()
    }

    val maxValue = hourlyData.maxOrNull()?.coerceAtLeast(1) ?: 1
    val hasHourlyData = hourlyData.any { it > 0 }
    val peakHour = hourlyData.indexOf(hourlyData.maxOrNull() ?: 0)

    val primaryColor = PrimaryGreen
    val barInactiveColor = NeutralGray200

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
                text = "⏰ 今日时间分布",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (hasHourlyData) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    val barWidth = size.width / 24
                    val chartHeight = size.height

                    hourlyData.forEachIndexed { index, value ->
                        val barHeight = if (value > 0) {
                            (value.toFloat() / maxValue) * chartHeight * 0.85f
                        } else {
                            chartHeight * 0.05f
                        }
                        val x = index * barWidth

                        drawRoundRect(
                            color = if (value > 0)
                                primaryColor.copy(alpha = 0.5f + 0.5f * (value.toFloat() / maxValue))
                            else
                                barInactiveColor,
                            topLeft = Offset(x + 1.dp.toPx(), chartHeight - barHeight),
                            size = Size(barWidth - 2.dp.toPx(), barHeight),
                            cornerRadius = CornerRadius(2.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 时间标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("0时", "6时", "12时", "18时", "23时").forEach {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray400,
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "高峰时段: ${String.format("%02d:00", peakHour)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeutralGray100),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📊", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "今日暂无时间分布数据",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray500
                        )
                    }
                }
            }
        }
    }
}

// ==================== 趋势分析 ====================
@Composable
fun TrendAnalysisCard(
    todayCount: Int,
    thisWeekCount: Int,
    currentStreak: Int,
    weeklyAverage: Double
) {
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
                text = "📈 趋势分析",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(16.dp))

            // 今日 vs 日均
            val todayVsAvg = if (weeklyAverage > 0) {
                val diff = ((todayCount - weeklyAverage) / weeklyAverage * 100).toInt()
                if (diff > 0) "📈 +${diff}%" else if (diff < 0) "📉 ${diff}%" else "➡️ 持平"
            } else {
                "📊 数据积累中"
            }

            TrendRow(
                label = "今日 vs 日均",
                value = todayVsAvg,
                color = if (todayCount >= weeklyAverage) PrimaryGreen else WarmOrange
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = NeutralGray200
            )

            // 本周节奏
            val weeklyPace = when {
                thisWeekCount >= 20 -> "🚀 超高频"
                thisWeekCount >= 10 -> "⚡ 稳定高效"
                thisWeekCount >= 5 -> "💪 持续前进"
                thisWeekCount >= 1 -> "🌱 慢慢成长"
                else -> "💤 本周空白"
            }
            TrendRow(
                label = "本周节奏",
                value = weeklyPace,
                color = PrimaryGreen
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = NeutralGray200
            )

            // 连续状态
            val streakStatus = when {
                currentStreak >= 30 -> "🏆 超级自律"
                currentStreak >= 14 -> "🔥 习惯养成中"
                currentStreak >= 7 -> "⚡ 一周稳定"
                currentStreak >= 3 -> "💪 初具规模"
                currentStreak >= 1 -> "✅ 刚刚开始"
                else -> "⏰ 快来签到"
            }
            TrendRow(
                label = "连续状态",
                value = streakStatus,
                color = WarmOrange
            )
        }
    }
}

@Composable
fun TrendRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// ==================== 心情分布 ====================
@Composable
fun MoodDistributionCard(records: List<RecordEntity>) {
    val moodCounts = remember(records) {
        val counts = IntArray(5)
        records.forEach { record ->
            val idx = (record.mood - 1).coerceIn(0, 4)
            counts[idx]++
        }
        counts.toList()
    }

    val total = records.size
    val avgMood = if (total > 0) records.map { it.mood }.average() else 0.0
    val moodEmojis = listOf("😫", "😔", "😐", "🙂", "😄")
    val moodLabels = listOf("疲惫", "低落", "一般", "不错", "很棒")
    val moodColors = listOf(MoodTired, MoodSad, MoodNeutral, MoodCalm, MoodHappy)

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "😊 今日心情分布",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                // 平均心情
                val avgIdx = (avgMood - 1).toInt().coerceIn(0, 4)
                Text(
                    text = "平均 ${moodEmojis[avgIdx]} ${String.format("%.1f", avgMood)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = moodColors[avgIdx]
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                moodCounts.forEachIndexed { index, count ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = moodEmojis[index], fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = moodLabels[index],
                            style = MaterialTheme.typography.labelSmall,
                            color = NeutralGray500,
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        val ratio = if (total > 0) count.toFloat() / total else 0f
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeutralGray200)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(ratio)
                                    .align(Alignment.BottomCenter)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(moodColors[index].copy(alpha = 0.7f))
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = if (count > 0) MaterialTheme.colorScheme.onSurface else NeutralGray400
                        )
                    }
                }
            }
        }
    }
}

// ==================== 成就里程碑 ====================
@Composable
fun MilestoneCard(totalCount: Int) {
    val milestones = listOf(
        Triple(1, "初来乍到", "🎯"),
        Triple(10, "小试牛刀", "⭐"),
        Triple(50, "渐入佳境", "🌟"),
        Triple(100, "百次达成", "💯"),
        Triple(200, "铁手", "🤖"),
        Triple(500, "大师之路", "👑"),
        Triple(1000, "传说", "🏆")
    )

    val nextMilestone = milestones.firstOrNull { it.first > totalCount }
    val lastMilestone = milestones.lastOrNull { it.first <= totalCount }

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
                text = "🎯 里程碑进度",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(16.dp))

            if (lastMilestone != null) {
                Text(
                    text = "已达成: ${lastMilestone.third} ${lastMilestone.second}（${lastMilestone.first}次）",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryGreen
                )
                Spacer(Modifier.height(8.dp))
            }

            if (nextMilestone != null) {
                val progress = totalCount.toFloat() / nextMilestone.first
                Text(
                    text = "下一目标: ${nextMilestone.third} ${nextMilestone.second}（${nextMilestone.first}次）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PrimaryGreen,
                    trackColor = NeutralGray200,
                )

                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$totalCount / ${nextMilestone.first}（${(progress * 100).toInt()}%）",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeutralGray500,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            } else {
                Text(
                    text = "🏆 已达成所有里程碑！你就是传说！",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = WarmOrange
                )
            }
        }
    }
}
