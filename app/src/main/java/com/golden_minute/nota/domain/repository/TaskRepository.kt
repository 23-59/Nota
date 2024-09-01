package com.golden_minute.nota.domain.repository


import com.golden_minute.nota.domain.model.Priority
import com.golden_minute.nota.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTasks(): Flow<List<Task>>

    fun searchTask(input: String): Flow<List<Task>>

    fun getScheduledTasks(): Flow<List<Task>>

    suspend fun addTask(task: Task): Long

    suspend fun checkSelectedTasks(selectedTasks: List<Int>)

    suspend fun deleteSelectedTasks(tasks: List<Task>)

    suspend fun updateTask(id: Int?,
                           title: String,
                           description: String,
                           priority: Priority,
                           tagNumber1: String?,
                           tagNumber2: String?,
                           tagNumber3: String?,
                           dueDate: String?,
                           repeatTime: String?, isChecked: Boolean?,
                           hasReminder: Boolean)

    suspend fun updateSelectedTasks(tasks: List<Task>)


    suspend fun deleteTask(task: Task)


    suspend fun getTaskByTitle(id: Int): Task?
}