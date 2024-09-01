package com.golden_minute.nota.data.repository

import com.golden_minute.nota.data.data_source.TaskDao
import com.golden_minute.nota.domain.model.Priority
import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }


    override fun searchTask(input: String): Flow<List<Task>> {
        return taskDao.searchTask(input)
    }

    override fun getScheduledTasks(): Flow<List<Task>> {
        return taskDao.getScheduledTasks()
    }

    override suspend fun addTask(task: Task): Long {
        return taskDao.addTask(task)
    }

    override suspend fun checkSelectedTasks(selectedTasks: List<Int>) {
        return taskDao.checkSelectedTasks(selectedTasks)
    }

    override suspend fun deleteSelectedTasks(tasks: List<Task>) {
        return taskDao.deleteAllTasks(tasks)
    }

    override suspend fun updateTask(
        id: Int?,
        title: String,
        description: String,
        priority: Priority,
        tagNumber1: String?,
        tagNumber2: String?,
        tagNumber3: String?,
        dueDate: String?,
        repeatTime: String?, isChecked: Boolean?,
        hasReminder: Boolean
    ) {
        return taskDao.updateTask(
            id,
            title,
            description,
            priority,
            dueDate,
            repeatTime,
            isChecked,
            hasReminder
        )
    }


    override suspend fun updateSelectedTasks(tasks: List<Task>) {
        return taskDao.updateTasks(tasks)
    }

    override suspend fun deleteTask(task: Task) {
        return taskDao.deleteTask(task)
    }

    override suspend fun getTaskByTitle(id: Int): Task? {
        return taskDao.getTaskById(id)
    }
}