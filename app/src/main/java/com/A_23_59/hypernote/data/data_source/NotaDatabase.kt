package com.A_23_59.hypernote.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.model.Task

@Database(entities = [Note::class, Task::class], version = 1)
abstract class NotaDatabase : RoomDatabase() {

    abstract val noteDao : NoteDao

    abstract val taskDao : TaskDao

    abstract val tagDao : TagDao

    companion object{
        const val DATABASE_NAME = "nota_db"
    }
}