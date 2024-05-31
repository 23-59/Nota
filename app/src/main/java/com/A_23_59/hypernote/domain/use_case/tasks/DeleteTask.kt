package com.A_23_59.hypernote.domain.use_case.tasks

import com.A_23_59.hypernote.domain.model.Task
import com.A_23_59.hypernote.domain.repository.TaskRepository

class DeleteTask(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task){
        repository.deleteTask(task)
    }
}