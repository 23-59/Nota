package com.A_23_59.hypernote.domain.use_case.tasks

import com.A_23_59.hypernote.domain.model.InvalidItemException
import com.A_23_59.hypernote.domain.model.Task
import com.A_23_59.hypernote.domain.repository.TaskRepository

class AddTask(val repository: TaskRepository) {

    @Throws
    suspend operator fun invoke(task: Task) {
        if (task.title.isBlank()) {
            throw InvalidItemException("the title is empty")
        }

        repository.addTask(task)
    }
}