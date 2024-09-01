package com.golden_minute.nota.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.golden_minute.nota.domain.model.Priority
import com.golden_minute.nota.domain.model.Task
import kotlinx.coroutines.flow.Flow
@Dao
interface TaskDao {
    @Query("SELECT * FROM tbl_task")
    fun getAllTasks (): Flow<List<Task>>

    @Query("SELECT * FROM TBL_TASK WHERE title || description LIKE '%' || :input || '%' ")
    fun searchTask(input:String):Flow<List<Task>>

    @Query("SELECT * FROM TBL_TASK WHERE LENGTH(dueDate) > 0 & hasReminder ")
    fun getScheduledTasks():Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun addTask(task: Task):Long

   @Query("UPDATE TBL_TASK SET isChecked = 1 WHERE id IN (:selectedTasksIds)")
   suspend fun checkSelectedTasks(selectedTasksIds:List<Int>)

    @Delete
    suspend fun deleteAllTasks(tasks:List<Task>)

    @Update
    suspend fun updateTasks(tasks:List<Task>)

    @Query("UPDATE tbl_task SET title = :title, description = :description, " +
            "priority = :priority, dueDate = :dueDate, repeatTime = :repeatTime, " +
            "isChecked = :isChecked, hasReminder = :hasReminder " +
            "WHERE id = :id"
    )
    suspend fun updateTask(
        id: Int?,
        title: String,
        description: String,
        priority: Priority,
        dueDate: String?,
        repeatTime: String?, isChecked: Boolean?,
        hasReminder: Boolean
    )

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM TBL_TASK WHERE id = :id")
   suspend fun getTaskById(id:Int):Task?
}