package com.golden_minute.nota.domain.use_case.tasks

import android.content.Context
import com.golden_minute.nota.R
import com.golden_minute.nota.domain.model.InvalidItemException
import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.domain.repository.TaskRepository

class AddTask(val repository: TaskRepository,private val context:Context) {

    @Throws
    suspend operator fun invoke(task: Task):Long {
        if (task.title.isBlank()) {
            throw InvalidItemException(context.getString(R.string.the_title_is_empty))
        }

        return repository.addTask(task)
    }
}