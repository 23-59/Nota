package com.golden_minute.nota.data.Alarm_manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.golden_minute.nota.R
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.Calendar

@AndroidEntryPoint
@SuppressLint("RestrictedApi")
class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val REMINDER_CHANNEL_ID = "reminder_id"
    }


    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {


        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val taskTitle = intent?.getStringExtra("TASK_TITLE")
            ?: ""
        val taskDescription = intent?.getStringExtra("TASK_DESCRIPTION")
            ?: ""
        val taskID = intent?.getIntExtra("TASK_ID", -1)
            ?: return
        val taskRepeatTime = intent.getStringExtra("TASK_REPEAT_TIME")
        val taskDueDate = intent.getStringExtra("TASK_DUE_DATE")

        val localDateTime = LocalDateTime.parse(taskDueDate)

        val calendar = Calendar.getInstance().apply {
            set(
                localDateTime.year,
                localDateTime.monthValue - 1,
                localDateTime.dayOfMonth,
                localDateTime.hour,
                localDateTime.minute,
                0
            )
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        showNotification(
            title = taskTitle,
            description = taskDescription,
            id = taskID,
            context,
            notificationManager
        )

        when (taskRepeatTime) {
            context.getString(R.string.daily) -> {
                calendar.timeInMillis =System.currentTimeMillis()
                calendar.add(Calendar.DAY_OF_WEEK,1)
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    PendingIntent.getBroadcast(
                        context,
                        taskID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            context.getString(R.string.weekly) -> {
                calendar.timeInMillis =System.currentTimeMillis()
                calendar.add(Calendar.DAY_OF_MONTH,7)
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    PendingIntent.getBroadcast(
                        context,
                        taskID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            context.getString(R.string.monthly) -> {
                calendar.timeInMillis =System.currentTimeMillis()
                calendar.add(Calendar.MONTH,1)
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    PendingIntent.getBroadcast(
                        context,
                        taskID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            context.getString(R.string.yearly) -> {
                calendar.timeInMillis =System.currentTimeMillis()
                calendar.add(Calendar.YEAR,1)
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    PendingIntent.getBroadcast(
                        context,
                        taskID,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }
        }


    }

    private fun showNotification(
        title: String,
        description: String,
        id: Int,
        context: Context,
        notificationManager: NotificationManager
    ) {
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.light_bulb_com)
            .setContentText(description)
            .build()

        notificationManager.notify(id, notification)

    }
}