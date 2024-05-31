package com.A_23_59.hypernote.data.repository

import com.A_23_59.hypernote.data.data_source.TaskDao
import com.A_23_59.hypernote.domain.model.Task
import com.A_23_59.hypernote.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(private val taskDao: TaskDao):TaskRepository {
    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    override fun searchTask(input: String): Flow<List<Task>> {
        return taskDao.searchTask(input)
    }

    override suspend fun addTask(task: Task) {
       return taskDao.addTask(task)
    }

    override suspend fun deleteSelectedTasks(tasks: List<Task>) {
        return taskDao.deleteAllTasks(tasks)
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