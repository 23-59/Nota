package com.golden_minute.nota.domain.alarm_manager

import com.golden_minute.nota.domain.model.Task

interface AlarmScheduler {

    fun schedule(task:Task)

    fun cancel(task: Task)
}