package com.A_23_59.hypernote.domain.use_case.tasks

import com.A_23_59.hypernote.domain.model.Task
import com.A_23_59.hypernote.domain.repository.TaskRepository

class GetTask(private val repository:TaskRepository) {
    suspend operator fun invoke (id:Int) : Task? {
        return repository.getTaskByTitle(id)
    }
}