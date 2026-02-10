package com.example.lululu.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.lululu.data.entity.FriendEntity
import com.example.lululu.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    friends: List<FriendEntity>,
    myTodayCount: Int,
    myThisWeekCount: Int,
    myTotalCount: Int,
    onNavigateBack: () -> Unit,
    onAddFriend: (String, String) -> Unit,
    onRemoveFriend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var showPkDialog by remember { mutableStateOf(false) }
    var pkFriend by remember { mutableStateOf<FriendEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "好友圈",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddFriendDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "添加好友")
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
            // 切换 tabs
            item {
                FriendsTabSelector(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            when (selectedTab) {
                0 -> {
                    // 好友列表
                    if (friends.isEmpty()) {
                        item { EmptyFriendsView() }
                    } else {
                        items(friends) { friend ->
                            FriendCard(
                                friend = friend,
                                onPkClick = {
                                    pkFriend = friend
                                    showPkDialog = true
                                },
                                onRemove = { onRemoveFriend(friend.id) }
                            )
                        }
                    }
                }
                1 -> {
                    // 排行榜（类似微信步数）
                    item {
                        LeaderboardCard(
                            friends = friends,
                            myTodayCount = myTodayCount,
                            myThisWeekCount = myThisWeekCount,
                            myTotalCount = myTotalCount
                        )
                    }
                }
                2 -> {
                    // 好友成就展示
                    items(friends) { friend ->
                        FriendAchievementCard(friend = friend)
                    }
                }
            }

            // 底部间距
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // 添加好友弹窗
    if (showAddFriendDialog) {
        AddFriendDialog(
            onConfirm = { nickname, emoji ->
                onAddFriend(nickname, emoji)
                showAddFriendDialog = false
            },
            onDismiss = { showAddFriendDialog = false }
        )
    }

    // PK弹窗
    if (showPkDialog && pkFriend != null) {
        PkBattleDialog(
            friend = pkFriend!!,
            myTodayCount = myTodayCount,
            myThisWeekCount = myThisWeekCount,
            onDismiss = {
                showPkDialog = false
                pkFriend = null
            }
        )
    }
}

// ==================== 标签选择器 ====================
@Composable
fun FriendsTabSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("好友列表", "🏅 排行榜", "🏆 成就")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NeutralGray100)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (selectedTab == index) Color.White else Color.Transparent
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTab == index) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        NeutralGray500
                    }
                )
            }
        }
    }
}

// ==================== 空好友状态 ====================
@Composable
fun EmptyFriendsView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "👥", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "还没有好友",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "添加好友一起PK，看看谁更能撸",
                style = MaterialTheme.typography.bodyMedium,
                color = NeutralGray500,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== 好友卡片 ====================
@Composable
fun FriendCard(
    friend: FriendEntity,
    onPkClick: () -> Unit,
    onRemove: () -> Unit
) {
    val statusColor = when (friend.status) {
        "online" -> PrimaryBlue
        "busy" -> WarmOrange
        else -> NeutralGray400
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 头像
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NeutralGray100),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = friend.avatarEmoji,
                            fontSize = 28.sp
                        )
                    }
                    // 在线状态点
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = friend.nickname,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "今日 ${friend.todayCount} 次 · 累计 ${friend.totalCount} 次",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeutralGray500
                    )
                }

                // PK按钮
                Button(
                    onClick = onPkClick,
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmOrange
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        "⚔️ PK",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 数据条
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FriendStatChip(label = "🔥 连续", value = "${friend.currentStreak}天")
                FriendStatChip(label = "📅 本周", value = "${friend.thisWeekCount}次")
                FriendStatChip(label = "🏆 成就", value = "${friend.unlockedAchievements}个")
            }
        }
    }
}

@Composable
fun FriendStatChip(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NeutralGray500
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

// ==================== 排行榜（微信步数风格） ====================
@Composable
fun LeaderboardCard(
    friends: List<FriendEntity>,
    myTodayCount: Int,
    myThisWeekCount: Int,
    myTotalCount: Int
) {
    // 加入 "我" 的数据
    data class LeaderboardEntry(
        val name: String,
        val emoji: String,
        val todayCount: Int,
        val weekCount: Int,
        val totalCount: Int,
        val isMe: Boolean = false
    )

    val allEntries = remember(friends, myTodayCount, myThisWeekCount, myTotalCount) {
        val entries = mutableListOf(
            LeaderboardEntry("我", "😎", myTodayCount, myThisWeekCount, myTotalCount, isMe = true)
        )
        friends.forEach {
            entries.add(LeaderboardEntry(it.nickname, it.avatarEmoji, it.todayCount, it.thisWeekCount, it.totalCount))
        }
        entries.sortedByDescending { it.weekCount }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "⚡ 本周排行榜",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "看看谁更能撸",
                style = MaterialTheme.typography.bodySmall,
                color = NeutralGray500
            )

            Spacer(modifier = Modifier.height(16.dp))

            allEntries.forEachIndexed { index, entry ->
                LeaderboardRow(
                    rank = index + 1,
                    name = entry.name,
                    emoji = entry.emoji,
                    weekCount = entry.weekCount,
                    todayCount = entry.todayCount,
                    isMe = entry.isMe,
                    maxCount = allEntries.maxOf { it.weekCount }.coerceAtLeast(1)
                )

                if (index < allEntries.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    name: String,
    emoji: String,
    weekCount: Int,
    todayCount: Int,
    isMe: Boolean,
    maxCount: Int
) {
    val rankEmoji = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "$rank"
    }

    val progressRatio = weekCount.toFloat() / maxCount.coerceAtLeast(1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isMe) PrimaryBlue.copy(alpha = 0.08f) else Color.Transparent
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排名
        Box(
            modifier = Modifier.width(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rankEmoji,
                fontSize = if (rank <= 3) 22.sp else 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 头像
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isMe) PrimaryBlue.copy(alpha = 0.15f) else NeutralGray100),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isMe) PrimaryBlue else MaterialTheme.colorScheme.onSurface
                )
                if (isMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "（你）",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 进度条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(NeutralGray200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressRatio)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when (rank) {
                                1 -> Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF8C00)))
                                2 -> Brush.horizontalGradient(listOf(Color(0xFFC0C0C0), Color(0xFF808080)))
                                3 -> Brush.horizontalGradient(listOf(Color(0xFFCD7F32), Color(0xFF8B4513)))
                                else -> Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryBlueLight))
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 次数
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$weekCount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (rank == 1) Color(0xFFFF8C00) else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "本周",
                style = MaterialTheme.typography.labelSmall,
                color = NeutralGray500
            )
        }
    }
}

// ==================== 好友成就卡 ====================
@Composable
fun FriendAchievementCard(friend: FriendEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NeutralGray100),
                contentAlignment = Alignment.Center
            ) {
                Text(text = friend.avatarEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.nickname,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "已解锁 ${friend.unlockedAchievements} 个成就",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeutralGray500
                )
            }

            // 成就数展示
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(WarmOrange.copy(alpha = 0.15f), WarmAmber.copy(alpha = 0.15f))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆 ${friend.unlockedAchievements}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = WarmOrange
                )
            }
        }
    }
}

// ==================== PK 对战弹窗 ====================
@Composable
fun PkBattleDialog(
    friend: FriendEntity,
    myTodayCount: Int,
    myThisWeekCount: Int,
    onDismiss: () -> Unit
) {
    var showResult by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(800)
        showResult = true
    }

    // 对比：本周数据
    val iWin = myThisWeekCount > friend.thisWeekCount
    val isTie = myThisWeekCount == friend.thisWeekCount

    // 动画
    val scale by animateFloatAsState(
        targetValue = if (showResult) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pkScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pk")
    val swordRotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swordRotation"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = "⚔️ PK 对决 ⚔️",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // VS对比
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 我
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (iWin) Brush.linearGradient(
                                        listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                                    ) else Brush.linearGradient(
                                        listOf(NeutralGray200, NeutralGray300)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "😎", fontSize = 36.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "我",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "本周 $myThisWeekCount 次",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray500
                        )
                    }

                    // VS
                    Text(
                        text = "⚔️",
                        fontSize = 36.sp,
                        modifier = Modifier.rotate(swordRotation)
                    )

                    // 对手
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (!iWin && !isTie) Brush.linearGradient(
                                        listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                                    ) else Brush.linearGradient(
                                        listOf(NeutralGray200, NeutralGray300)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = friend.avatarEmoji, fontSize = 36.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = friend.nickname,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "本周 ${friend.thisWeekCount} 次",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray500
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 结果
                AnimatedVisibility(
                    visible = showResult,
                    enter = fadeIn() + scaleIn()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when {
                                isTie -> "🤝"
                                iWin -> "🎉"
                                else -> "😤"
                            },
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when {
                                isTie -> "旗鼓相当！平手！"
                                iWin -> "你赢了！再接再厉！"
                                else -> "你输了！下周努力超越！"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isTie -> WarmAmber
                                iWin -> PrimaryBlue
                                else -> WarningRed
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = when {
                                isTie -> "你们的频率一样一样的"
                                iWin -> "本周你比${friend.nickname}多撸了${myThisWeekCount - friend.thisWeekCount}次"
                                else -> "${friend.nickname}比你多撸了${friend.thisWeekCount - myThisWeekCount}次"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = NeutralGray500,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (iWin || isTie) PrimaryBlue else WarmOrange
                    )
                ) {
                    Text(
                        text = if (iWin || isTie) "继续加油！" else "不服再来！",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==================== 添加好友弹窗 ====================
@Composable
fun AddFriendDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var nickname by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("😎") }

    val emojiOptions = listOf("😎", "🤓", "💪", "🐟", "🦊", "🐱", "🐶", "🐼", "🦁", "🐸", "👻", "🤖")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "添加好友",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 选择头像表情
                Text(
                    text = "选择头像",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeutralGray500
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Emoji grid - 2 rows
                Column {
                    for (row in 0..1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0..5) {
                                val index = row * 6 + col
                                if (index < emojiOptions.size) {
                                    val emoji = emojiOptions[index]
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (selectedEmoji == emoji) PrimaryBlue.copy(alpha = 0.15f) else Color.Transparent
                                            )
                                            .clickable { selectedEmoji = emoji },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = emoji,
                                            fontSize = if (selectedEmoji == emoji) 26.sp else 22.sp
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    placeholder = { Text("好友昵称") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = NeutralGray200
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (nickname.isNotBlank()) {
                            onConfirm(nickname, selectedEmoji)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = nickname.isNotBlank()
                ) {
                    Text("添加", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onDismiss) {
                    Text("取消", color = NeutralGray500)
                }
            }
        }
    }
}
