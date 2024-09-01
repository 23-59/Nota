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
import javax.inject.Inject

private const val TAG = "MRebootReceiver"
@AndroidEntryPoint
class MRebootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val alarmReceiverImpl = AlarmSchedulerImpl(context!!)
            CoroutineScope(Dispatchers.IO).launch {
                taskRepository.getScheduledTasks().collect {
                    it.forEach { task ->
                        alarmReceiverImpl.schedule(task)
                    }
                }
            }

        }
    }
}