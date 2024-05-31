package com.A_23_59.hypernote.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.A_23_59.hypernote.domain.model.Task
import kotlinx.coroutines.flow.Flow
@Dao
interface TaskDao {
    @Query("SELECT * FROM tbl_task")
    fun getAllTasks (): Flow<List<Task>>

    @Query("SELECT * FROM TBL_TASK WHERE title || description LIKE '%' || :input || '%' ")
    fun searchTask(input:String):Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun addTask(task: Task)

    @Delete
    suspend fun deleteAllTasks(tasks:List<Task>)

    @Update
    suspend fun updateTasks(tasks:List<Task>)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM TBL_TASK WHERE id = :id")
   suspend fun getTaskById(id:Int):Task?
}