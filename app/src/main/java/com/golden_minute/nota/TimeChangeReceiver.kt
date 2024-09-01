package com.golden_minute.nota

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.golden_minute.nota.data.Alarm_manager.AlarmSchedulerImpl
import com.golden_minute.nota.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

private const val TAG = "TimeChangeReceiver"

@AndroidEntryPoint
class TimeChangeReceiver:BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_TIME_CHANGED) {
            val alarmReceiverImpl = AlarmSchedulerImpl(context!!)
            val currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
            CoroutineScope(Dispatchers.IO).launch {
                taskRepository.getScheduledTasks().collect {
                    it.forEach { task ->
                        val taskTime = LocalDateTime.parse(task.dueDate)
                        if(!taskTime.isBefore(currentTime))
                        alarmReceiverImpl.schedule(task)
                    }
                }
            }
        }
    }
}