package com.example.lululu.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.lululu.MainActivity
import com.example.lululu.R
import java.util.concurrent.TimeUnit

/**
 * 整点提醒Worker
 * 在设定时间发送健康提醒通知
 */
class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "健康提醒"
        val message = inputData.getString(KEY_MESSAGE) ?: "该关注一下自己的健康了！"

        showNotification(title, message)
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建通知渠道（Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "健康提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "每日健康提醒通知"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 创建Intent
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 构建通知
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "lululu_health_reminder"
        const val NOTIFICATION_ID = 1001
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"

        /**
         * 安排每日提醒
         */
        fun scheduleDailyReminder(
            context: Context,
            hour: Int,
            minute: Int
        ) {
            // 计算延迟时间
            val currentTime = java.time.LocalDateTime.now()
            var scheduledTime = currentTime.withHour(hour).withMinute(minute).withSecond(0)

            // 如果已过今天的时间，则安排到明天
            if (scheduledTime.isBefore(currentTime)) {
                scheduledTime = scheduledTime.plusDays(1)
            }

            val initialDelay = java.time.Duration.between(currentTime, scheduledTime).toMillis()

            val inputData = workDataOf(
                KEY_TITLE to "健康提醒",
                KEY_MESSAGE to "今天还没记录哦，养成健康的好习惯吧！"
            )

            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .setConstraints(constraints)
                .addTag(TAG_REMINDER)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    reminderRequest
                )
        }

        /**
         * 取消提醒
         */
        fun cancelReminder(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WORK_NAME)
        }

        private const val WORK_NAME = "daily_health_reminder"
        private const val TAG_REMINDER = "reminder"
    }
}
