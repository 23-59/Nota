package com.A_23_59.hypernote.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.A_23_59.hypernote.domain.model.Tag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tbl_tag")
    fun getAllTags(): Flow<List<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTags(tags: List<Tag>)

    @Delete
    suspend fun deleteTag(tag: Tag)

    @Update
    suspend fun updateTags(tags: List<Tag>)
}