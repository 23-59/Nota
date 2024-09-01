package com.golden_minute.nota.domain.use_case.tasks

import com.golden_minute.nota.domain.model.Priority
import com.golden_minute.nota.domain.repository.TaskRepository

class UpdateTask(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Int?,
                                title: String,
                                description: String,
                                priority: Priority,
                                tagNumber1: String?,
                                tagNumber2: String?,
                                tagNumber3: String?,
                                dueDate: String?,
                                repeatTime: String?, isChecked: Boolean?,
                                hasReminder: Boolean) {
           return repository.updateTask(id, title, description, priority, tagNumber1, tagNumber2, tagNumber3, dueDate, repeatTime, isChecked, hasReminder)
    }
}