package com.A_23_59.hypernote.domain.use_case.tasks

import com.A_23_59.hypernote.presentation.tasksTagsList
import com.A_23_59.hypernote.domain.model.Task
import com.A_23_59.hypernote.domain.repository.TaskRepository
import com.A_23_59.hypernote.domain.util.TaskOrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTasks(private val taskRepository: TaskRepository) {
     operator fun invoke(taskOrderType: TaskOrderType = TaskOrderType.Descending): Flow<List<Task>> {
        return taskRepository.getAllTasks().map { tasks ->
            when (taskOrderType) {
                is TaskOrderType.Ascending -> tasks.sortedBy { it.title.lowercase() }
                is TaskOrderType.Descending -> tasks.sortedByDescending { it.title }
                is TaskOrderType.DueDate -> tasks.sortedBy { it.dueDate }
                is TaskOrderType.Priority -> tasks.sortedBy { it.priority }
                is TaskOrderType.Completed -> tasks.sortedBy { it.isChecked }
                is TaskOrderType.Undone -> tasks.sortedBy { it.isChecked?.not() }
                is TaskOrderType.Tags -> findSelectedTagsInTaskList(tasks)
                    .sortedBy { sortedTags ->
                    tasks.sortedBy { sortedTags.title }
                    sortedTags.title
                }

            }

        }
    }
    private fun findSelectedTagsInTaskList(tasks: List<Task>): List<Task> {
        val sortedTagsList: ArrayList<Task> = arrayListOf()
        tasks.forEach { task ->
            tasksTagsList.forEach { tag ->
                if (tag.tagName == task.tagNumber1 || tag.tagName == task.tagNumber2 || tag.tagName == task.tagNumber3)
                    sortedTagsList.add(task)
            }
        }
        return sortedTagsList
    }
}