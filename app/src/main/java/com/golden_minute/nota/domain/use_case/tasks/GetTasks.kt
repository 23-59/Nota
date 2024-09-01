package com.golden_minute.nota.domain.use_case.tasks

import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.domain.repository.TaskRepository
import com.golden_minute.nota.domain.util.TaskOrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTasks(private val taskRepository: TaskRepository) {
    operator fun invoke(
        taskOrderType: TaskOrderType = TaskOrderType.Descending,
    ): Flow<List<Task>> {
        return taskRepository.getAllTasks().map { tasks ->
                when (taskOrderType) {
                    is TaskOrderType.Ascending -> tasks.sortedBy { it.title }
                    is TaskOrderType.Descending -> tasks.sortedByDescending { it.title }
                    is TaskOrderType.DueDate -> tasks.sortedBy { it.dueDate }
                    is TaskOrderType.Priority -> tasks.sortedByDescending { it.priority }
                    is TaskOrderType.Completed -> tasks.sortedBy { it.isChecked?.not() }
                    is TaskOrderType.Undone -> tasks.sortedBy { it.isChecked ?: false }
                    is TaskOrderType.Default -> tasks

                }

            }
    }


}