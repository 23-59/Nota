package com.golden_minute.nota

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.golden_minute.nota.data.Alarm_manager.AlarmReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotaApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        val channel:NotificationChannel = NotificationChannel(
            AlarmReceiver.REMINDER_CHANNEL_ID,"Task Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "this channel is for reminding tasks"
            lightColor = android.graphics.Color.RED
            enableLights(true)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

