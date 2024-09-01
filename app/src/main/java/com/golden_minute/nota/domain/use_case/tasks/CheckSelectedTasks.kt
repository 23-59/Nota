package com.golden_minute.nota.domain.use_case.tasks

import com.golden_minute.nota.domain.repository.TaskRepository

class CheckSelectedTasks(private val repository: TaskRepository) {
    suspend operator fun invoke(selectedTasks: List<Int>) {
        repository.checkSelectedTasks(selectedTasks)
    }
}