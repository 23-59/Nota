package com.A_23_59.hypernote.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.A_23_59.hypernote.domain.model.Note
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {
    @Query("SELECT * FROM tbl_note")
    fun getAllNotes(): Flow<List<Note>>
    @Query("SELECT tagNumber1, tagNumber2, tagNumber3 FROM tbl_note")
    fun getAllTags(): Flow<List<String>>


    @Query("SELECT * FROM TBL_NOTE WHERE title || description LIKE '%' || :input || '%' ")
    fun searchNote(input:String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: Note)

    @Delete
    suspend fun deleteAllNotes(notes: List<Note>)

    @Update
    suspend fun updateNotes(notes: List<Note>)

    @Delete
    suspend fun deleteNote(note: Note)


    @Query("SELECT * FROM TBL_NOTE WHERE id = :id")
    suspend fun getNoteByTitle(id: Int) : Note?
}