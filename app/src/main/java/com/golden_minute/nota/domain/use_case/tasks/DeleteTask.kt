package com.golden_minute.nota.domain.use_case.tasks

import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.domain.repository.TaskRepository

class DeleteTask(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task){
        repository.deleteTask(task)
    }
}