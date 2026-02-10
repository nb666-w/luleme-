package com.example.lululu.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lululu.ui.theme.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * 启动页 - 一群鹿从屏幕上奔腾而过
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    // 鹿群配置：每只鹿的大小、纵向位置、延迟、速度各不同
    val deerConfigs = remember {
        listOf(
            // emoji, fontSize, yRatio, delayMs, durationMs, flipHorizontal
            DeerConfig("🦌", 60, 0.20f, 0, 1600, false),
            DeerConfig("🦌", 48, 0.35f, 100, 1500, false),
            DeerConfig("🦌", 72, 0.28f, 200, 1400, false),
            DeerConfig("🦌", 56, 0.45f, 50, 1550, false),
            DeerConfig("🦌", 64, 0.15f, 300, 1350, false),
            DeerConfig("🦌", 44, 0.52f, 150, 1650, false),
            DeerConfig("🦌", 68, 0.38f, 250, 1450, false),
            DeerConfig("🦌", 52, 0.58f, 350, 1500, false),
            DeerConfig("🦌", 40, 0.48f, 400, 1700, false),
            DeerConfig("🦌", 58, 0.25f, 180, 1380, false),
            DeerConfig("🦌", 50, 0.62f, 320, 1520, false),
            DeerConfig("🦌", 74, 0.42f, 80, 1300, false),
        )
    }

    // 尘土粒子
    val dustConfigs = remember {
        (0..20).map {
            DustConfig(
                xRatio = Random.nextFloat(),
                yRatio = 0.3f + Random.nextFloat() * 0.4f,
                size = (16 + Random.nextInt(24)),
                delayMs = Random.nextInt(600),
                durationMs = (800 + Random.nextInt(600))
            )
        }
    }

    // Logo 渐隐出现
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.5f) }

    // 整体退出动画
    var exitTriggered by remember { mutableStateOf(false) }
    val exitAlpha by animateFloatAsState(
        targetValue = if (exitTriggered) 0f else 1f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "exitAlpha",
        finishedListener = { onSplashFinished() }
    )

    LaunchedEffect(Unit) {
        // Logo 先出来
        logoAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        logoScale.animateTo(1f, tween(600, easing = FastOutSlowInEasing))

        // 等鹿群跑完
        delay(2200)

        // 退出
        exitTriggered = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(exitAlpha)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20), // 深绿
                        Color(0xFF2E7D32),
                        Color(0xFF43A047),
                        Color(0xFF66BB6A),
                    )
                )
            )
    ) {
        // ==================== 尘土效果 ====================
        dustConfigs.forEach { dust ->
            AnimatedDust(
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                config = dust
            )
        }

        // ==================== 鹿群 ====================
        deerConfigs.forEach { deer ->
            AnimatedDeer(
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                config = deer
            )
        }

        // ==================== Logo ====================
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(logoAlpha.value)
                .scale(logoScale.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🦌",
                fontSize = 80.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "撸 撸 撸",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "坚持自律，遇见更好的自己",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        // 底部装饰
        Text(
            text = "v1.0.0",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

// ==================== 鹿动画 ====================

data class DeerConfig(
    val emoji: String,
    val fontSize: Int,
    val yRatio: Float,    // 垂直位置比例
    val delayMs: Int,     // 延迟出场
    val durationMs: Int,  // 跑过屏幕的时间
    val flipHorizontal: Boolean
)

@Composable
fun AnimatedDeer(
    screenWidth: Int,
    screenHeight: Int,
    config: DeerConfig
) {
    val density = LocalDensity.current
    val startX = with(density) { (screenWidth + 100).dp.toPx() }
    val endX = with(density) { (-120).dp.toPx() }
    val yPos = with(density) { (screenHeight * config.yRatio).dp.toPx() }

    // 主运动动画
    val xOffset = remember { Animatable(startX) }

    // 上下弹跳
    val bounceAnim = rememberInfiniteTransition(label = "bounce_${config.delayMs}")
    val bounceY by bounceAnim.animateFloat(
        initialValue = 0f,
        targetValue = with(density) { (-12).dp.toPx() },
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceY"
    )

    // 轻微旋转 - 模拟跑步姿态
    val rotateAnim = rememberInfiniteTransition(label = "rotate_${config.delayMs}")
    val rotation by rotateAnim.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    LaunchedEffect(config) {
        delay(config.delayMs.toLong())
        xOffset.animateTo(
            targetValue = endX,
            animationSpec = tween(
                durationMillis = config.durationMs,
                easing = LinearEasing
            )
        )
    }

    Text(
        text = config.emoji,
        fontSize = config.fontSize.sp,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = xOffset.value.roundToInt(),
                    y = (yPos + bounceY).roundToInt()
                )
            }
            .rotate(rotation)
            .scale(scaleX = if (config.flipHorizontal) -1f else 1f, scaleY = 1f)
    )
}

// ==================== 尘土粒子 ====================

data class DustConfig(
    val xRatio: Float,
    val yRatio: Float,
    val size: Int,
    val delayMs: Int,
    val durationMs: Int
)

@Composable
fun AnimatedDust(
    screenWidth: Int,
    screenHeight: Int,
    config: DustConfig
) {
    val density = LocalDensity.current
    val xPos = with(density) { (screenWidth * config.xRatio).dp.toPx() }
    val yPos = with(density) { (screenHeight * config.yRatio).dp.toPx() }

    val dustAlpha = remember { Animatable(0f) }
    val dustScale = remember { Animatable(0.3f) }

    LaunchedEffect(config) {
        delay((config.delayMs + 300).toLong())
        // 出现
        dustAlpha.animateTo(0.4f, tween(200))
        // 扩散并消失（并发执行）
        coroutineScope {
            launch { dustScale.animateTo(1.5f, tween(config.durationMs)) }
            launch { dustAlpha.animateTo(0f, tween(config.durationMs)) }
        }
    }

    Text(
        text = "💨",
        fontSize = config.size.sp,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = xPos.roundToInt(),
                    y = yPos.roundToInt()
                )
            }
            .alpha(dustAlpha.value)
            .scale(dustScale.value)
    )
}
