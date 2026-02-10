package com.example.lululu.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 开机自启动接收器
 * 设备重启后重新安排提醒
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 重新安排提醒
            CoroutineScope(Dispatchers.IO).launch {
                val app = context.applicationContext as? com.example.lululu.LululuApplication
                val goal = app?.userGoalRepository?.getGoalSync()

                goal?.let {
                    if (it.reminderEnabled) {
                        ReminderWorker.scheduleDailyReminder(
                            context = context,
                            hour = it.reminderHour,
                            minute = it.reminderMinute
                        )
                    }
                }
            }
        }
    }
}
