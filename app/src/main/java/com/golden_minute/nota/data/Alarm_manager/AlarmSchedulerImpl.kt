package com.golden_minute.nota.data.Alarm_manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.golden_minute.nota.domain.alarm_manager.AlarmScheduler
import com.golden_minute.nota.domain.model.Task
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Calendar

class AlarmSchedulerImpl(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ScheduleExactAlarm", "MissingPermission")
    override fun schedule(task: Task) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_TITLE", task.title)
            putExtra("TASK_DESCRIPTION", task.description)
            putExtra("TASK_ID", task.id)
            putExtra("TASK_REPEAT_TIME", task.repeatTime)
            putExtra("TASK_DUE_DATE", task.dueDate)

        }

        val localDateTime = LocalDateTime.parse(task.dueDate).truncatedTo(ChronoUnit.SECONDS)

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

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    PendingIntent.getBroadcast(
                        context,
                        task.id!!,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )

    }

    override fun cancel(task: Task) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                task.id!!,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}