package com.example.lululu.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.lululu.data.entity.AchievementEntity
import com.example.lululu.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    achievements: List<AchievementEntity>,
    unlockedCount: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showUnlockAnimation by remember { mutableStateOf(false) }
    var selectedAchievement by remember { mutableStateOf<AchievementEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "成就殿堂",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 统计卡片
            item {
                AchievementHeroCard(
                    unlockedCount = unlockedCount,
                    totalCount = achievements.size
                )
            }

            // 分类显示
            val groupedAchievements = achievements.groupBy { it.category }
            val categoryOrder = listOf("milestone", "streak", "funny", "extreme", "frequency")

            categoryOrder.forEach { category ->
                val categoryAchievements = groupedAchievements[category] ?: return@forEach

                item {
                    CategoryHeader(category = category)
                }

                items(categoryAchievements) { achievement ->
                    FunAchievementItem(
                        achievement = achievement,
                        onClick = {
                            if (achievement.isUnlocked) {
                                selectedAchievement = achievement
                                showUnlockAnimation = true
                            }
                        }
                    )
                }
            }

            // 底部间距
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // 成就详情/解锁动画弹窗
    if (showUnlockAnimation && selectedAchievement != null) {
        AchievementUnlockDialog(
            achievement = selectedAchievement!!,
            onDismiss = {
                showUnlockAnimation = false
                selectedAchievement = null
            }
        )
    }
}

// ==================== 英雄统计卡片 ====================
@Composable
fun AchievementHeroCard(
    unlockedCount: Int,
    totalCount: Int
) {
    val progress = unlockedCount.toFloat() / totalCount.coerceAtLeast(1)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = WarmOrange.copy(alpha = 0.15f),
                spotColor = WarmOrange.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            WarmOrange,
                            WarmAmber,
                            WarningYellow
                        )
                    )
                )
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 大奖杯
                Text(
                    text = when {
                        progress >= 0.8f -> "🏆"
                        progress >= 0.5f -> "🥇"
                        progress >= 0.3f -> "🥈"
                        else -> "🥉"
                    },
                    fontSize = 56.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$unlockedCount / $totalCount",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = when {
                        progress >= 0.8f -> "成就收集大师！"
                        progress >= 0.5f -> "超过半数了，加油！"
                        progress >= 0.2f -> "初露锋芒"
                        unlockedCount > 0 -> "刚刚开始"
                        else -> "还没有解锁任何成就"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 进度条
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

// ==================== 分类标题 ====================
@Composable
fun CategoryHeader(category: String) {
    val (title, emoji) = when (category) {
        "milestone" -> Pair("里程碑", "🏆")
        "streak" -> Pair("连续记录", "🔥")
        "frequency" -> Pair("频率目标", "📊")
        "funny" -> Pair("搞怪成就", "😂")
        "extreme" -> Pair("极限挑战", "💀")
        else -> Pair("其他", "🎯")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== 成就项（搞怪版） ====================
@Composable
fun FunAchievementItem(
    achievement: AchievementEntity,
    onClick: () -> Unit
) {
    val isUnlocked = achievement.isUnlocked
    val progress = achievement.progress.toFloat() / achievement.target.coerceAtLeast(1)

    // 稀有度边框颜色
    val rarityColor = when (achievement.rarity) {
        "legendary" -> listOf(Color(0xFFFFD700), Color(0xFFFF8C00), Color(0xFFFF4500))
        "epic" -> listOf(Color(0xFF9C27B0), Color(0xFFE040FB), Color(0xFF7C4DFF))
        "rare" -> listOf(Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4))
        else -> listOf(NeutralGray300, NeutralGray400, NeutralGray300)
    }

    // 已解锁时的呼吸动画
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isUnlocked && achievement.rarity != "common") {
                    Modifier.shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = rarityColor[0].copy(alpha = glowAlpha * 0.3f),
                        spotColor = rarityColor[1].copy(alpha = glowAlpha * 0.4f)
                    )
                } else Modifier
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isUnlocked) 3.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 成就图标
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isUnlocked) {
                            Brush.linearGradient(rarityColor)
                        } else {
                            Brush.linearGradient(
                                listOf(NeutralGray200, NeutralGray300)
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isUnlocked) achievement.icon else "🔒",
                    fontSize = if (isUnlocked) 28.sp else 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        }
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // 稀有度标签
                    if (achievement.rarity != "common") {
                        RarityBadge(rarity = achievement.rarity)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    maxLines = 2
                )

                // 进度条
                if (!isUnlocked && achievement.target > 1) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(NeutralGray200)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(rarityColor)
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${achievement.progress} / ${achievement.target}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }

            // 已解锁标记
            if (isUnlocked) {
                Text(text = "✅", fontSize = 20.sp)
            }
        }
    }
}

// ==================== 稀有度标签 ====================
@Composable
fun RarityBadge(rarity: String) {
    val (text, bgColor) = when (rarity) {
        "legendary" -> Pair("传说", Color(0xFFFF8C00))
        "epic" -> Pair("史诗", Color(0xFF9C27B0))
        "rare" -> Pair("稀有", Color(0xFF2196F3))
        else -> Pair("普通", NeutralGray400)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 1.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = bgColor,
            fontSize = 9.sp
        )
    }
}

// ==================== 成就解锁弹窗动画 ====================
@Composable
fun AchievementUnlockDialog(
    achievement: AchievementEntity,
    onDismiss: () -> Unit
) {
    // 各种动画
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showContent = true
    }

    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dialogScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (showContent) 0f else 360f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "iconRotation"
    )

    val alpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(500),
        label = "contentAlpha"
    )

    // 闪烁动画
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val sparkleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleScale"
    )

    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "sparkleRotation"
    )

    val rarityColor = when (achievement.rarity) {
        "legendary" -> listOf(Color(0xFFFFD700), Color(0xFFFF8C00), Color(0xFFFF4500))
        "epic" -> listOf(Color(0xFF9C27B0), Color(0xFFE040FB), Color(0xFF7C4DFF))
        "rare" -> listOf(Color(0xFF2196F3), Color(0xFF03A9F4), Color(0xFF00BCD4))
        else -> listOf(PrimaryBlue, PrimaryBlueLight, PrimaryBlue)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .alpha(alpha),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 闪烁的背景装饰
                Box(contentAlignment = Alignment.Center) {
                    // 外圈光晕
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(sparkleScale)
                            .rotate(sparkleRotation)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        rarityColor[0].copy(alpha = 0.3f),
                                        rarityColor[1].copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // 图标
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .rotate(rotation)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.linearGradient(rarityColor)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = achievement.icon,
                            fontSize = 48.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 稀有度
                RarityBadge(rarity = achievement.rarity)

                Spacer(modifier = Modifier.height(12.dp))

                // 标题
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 描述
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 搞怪庆祝语
                val celebration = when (achievement.rarity) {
                    "legendary" -> listOf("🎆🎆🎆", "太牛了！传说级成就！")
                    "epic" -> listOf("🎉🎉🎉", "史诗级成就达成！")
                    "rare" -> listOf("✨✨✨", "稀有成就解锁！")
                    else -> listOf("🎊", "成就已解锁！")
                }

                Text(
                    text = celebration[0],
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.scale(sparkleScale)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = celebration[1],
                    style = MaterialTheme.typography.bodySmall,
                    color = rarityColor[0],
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rarityColor[0]
                    )
                ) {
                    Text(
                        "太棒了！",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
