package com.golden_minute.nota.domain.use_case.tasks

import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.domain.repository.TaskRepository

class DeleteTasks(private val repository: TaskRepository) {

    suspend operator fun invoke(tasks: List<Task>) {
        repository.deleteSelectedTasks(tasks)
    }
}