package com.A_23_59.hypernote.DI

import android.app.Application
import androidx.room.Room
import com.A_23_59.hypernote.data.data_source.NotaDatabase
import com.A_23_59.hypernote.data.data_source.TagDao
import com.A_23_59.hypernote.data.repository.NoteRepositoryImpl
import com.A_23_59.hypernote.data.repository.TaskRepositoryImpl
import com.A_23_59.hypernote.domain.repository.NoteRepository
import com.A_23_59.hypernote.domain.repository.TaskRepository
import com.A_23_59.hypernote.domain.use_case.notes.AddNote
import com.A_23_59.hypernote.domain.use_case.notes.DeleteNote
import com.A_23_59.hypernote.domain.use_case.notes.GetNote
import com.A_23_59.hypernote.domain.use_case.notes.GetNotes
import com.A_23_59.hypernote.domain.use_case.notes.NoteUseCases
import com.A_23_59.hypernote.domain.use_case.notes.SearchNote
import com.A_23_59.hypernote.domain.use_case.tags.AddTags
import com.A_23_59.hypernote.domain.use_case.tags.DeleteTags
import com.A_23_59.hypernote.domain.use_case.tags.GetTags
import com.A_23_59.hypernote.domain.use_case.tags.TagsUseCases
import com.A_23_59.hypernote.domain.use_case.tags.UpdateTags
import com.A_23_59.hypernote.domain.use_case.tasks.AddTask
import com.A_23_59.hypernote.domain.use_case.tasks.DeleteTask
import com.A_23_59.hypernote.domain.use_case.tasks.GetTask
import com.A_23_59.hypernote.domain.use_case.tasks.GetTasks
import com.A_23_59.hypernote.domain.use_case.tasks.SearchTask
import com.A_23_59.hypernote.domain.use_case.tasks.TaskUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideNotaDatabase(app: Application): NotaDatabase {
        return Room.databaseBuilder(app, NotaDatabase::class.java, NotaDatabase.DATABASE_NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NotaDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(db: NotaDatabase): TaskRepository {
        return TaskRepositoryImpl(db.taskDao)
    }

    @Provides
    @Singleton
    fun provideTagDao(db: NotaDatabase): TagDao {
        return db.tagDao
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(noteRepository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            GetNotes(noteRepository),
            DeleteNote(noteRepository),
            AddNote(noteRepository),
            GetNote(noteRepository),
            SearchNote(noteRepository)
        )
    }

    @Provides
    @Singleton
    fun provideTaskUseCases(taskRepository: TaskRepository): TaskUseCases {
        return TaskUseCases(
            GetTasks(taskRepository),
            DeleteTask(taskRepository),
            AddTask(taskRepository),
            GetTask(taskRepository),
            SearchTask(taskRepository)
        )
    }

    @Provides
    @Singleton
    fun provideTagUseCases(tagDao: TagDao): TagsUseCases {
        return TagsUseCases(
            AddTags(tagDao),
            DeleteTags(tagDao),
            UpdateTags(tagDao),
            GetTags(tagDao)
        )
    }
}