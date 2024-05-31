package com.A_23_59.hypernote.domain.repository


import com.A_23_59.hypernote.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTasks (): Flow<List<Task>>

    fun searchTask(input:String): Flow<List<Task>>


    suspend fun addTask(task: Task)


    suspend fun deleteSelectedTasks(tasks:List<Task>)


    suspend fun updateSelectedTasks(tasks:List<Task>)


    suspend fun deleteTask(task: Task)


    suspend fun getTaskByTitle(id:Int): Task?
}