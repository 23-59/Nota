package com.golden_minute.nota.DI

import android.app.Application
import androidx.room.Room
import com.golden_minute.nota.data.data_source.NotaDatabase
import com.golden_minute.nota.data.data_source.SettingsDataStore
import com.golden_minute.nota.data.data_source.TagDao
import com.golden_minute.nota.data.repository.NoteRepositoryImpl
import com.golden_minute.nota.data.repository.TaskRepositoryImpl
import com.golden_minute.nota.domain.repository.NoteRepository
import com.golden_minute.nota.domain.repository.TaskRepository
import com.golden_minute.nota.domain.use_case.notes.AddNote
import com.golden_minute.nota.domain.use_case.notes.DeleteNote
import com.golden_minute.nota.domain.use_case.notes.DeleteNotes
import com.golden_minute.nota.domain.use_case.notes.GetNote
import com.golden_minute.nota.domain.use_case.notes.GetNotes
import com.golden_minute.nota.domain.use_case.notes.NoteUseCases
import com.golden_minute.nota.domain.use_case.notes.SearchNote
import com.golden_minute.nota.domain.use_case.notes.UpdateNote
import com.golden_minute.nota.domain.use_case.tags.AddTag
import com.golden_minute.nota.domain.use_case.tags.AddTags
import com.golden_minute.nota.domain.use_case.tags.DeleteTag
import com.golden_minute.nota.domain.use_case.tags.DeleteTags
import com.golden_minute.nota.domain.use_case.tags.GetTags
import com.golden_minute.nota.domain.use_case.tags.TagsUseCases
import com.golden_minute.nota.domain.use_case.tags.UpdateTags
import com.golden_minute.nota.domain.use_case.tasks.AddTask
import com.golden_minute.nota.domain.use_case.tasks.CheckSelectedTasks
import com.golden_minute.nota.domain.use_case.tasks.DeleteTask
import com.golden_minute.nota.domain.use_case.tasks.DeleteTasks
import com.golden_minute.nota.domain.use_case.tasks.GetTask
import com.golden_minute.nota.domain.use_case.tasks.GetTasks
import com.golden_minute.nota.domain.use_case.tasks.SearchTask
import com.golden_minute.nota.domain.use_case.tasks.TaskUseCases
import com.golden_minute.nota.domain.use_case.tasks.UpdateTask
import com.golden_minute.nota.domain.use_case.tasks.UpdateTasks
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
    fun provideNoteUseCases(noteRepository: NoteRepository,tagDao: TagDao,app: Application): NoteUseCases {
        return NoteUseCases(
            GetNotes(noteRepository),
            DeleteNote(noteRepository),
            DeleteNotes(noteRepository),
            AddNote(noteRepository,app.applicationContext),
            GetNote(noteRepository),
            UpdateNote(noteRepository),
            SearchNote(noteRepository)
        )
    }

    @Provides
    @Singleton
    fun provideTaskUseCases(taskRepository: TaskRepository,tagDao: TagDao,app: Application): TaskUseCases {
        return TaskUseCases(
            GetTasks(taskRepository),
            DeleteTask(taskRepository),
            DeleteTasks(taskRepository),
            UpdateTasks(taskRepository),
            UpdateTask(taskRepository),
            CheckSelectedTasks(taskRepository),
            AddTask(taskRepository,app.applicationContext),
            GetTask(taskRepository),
            SearchTask(taskRepository)
        )
    }

    @Provides
    @Singleton
    fun provideTagUseCases(tagDao: TagDao): TagsUseCases {
        return TagsUseCases(
            AddTag(tagDao),
            AddTags(tagDao),
            DeleteTag(tagDao),
            DeleteTags(tagDao),
            UpdateTags(tagDao),
            GetTags(tagDao)
        )
    }


    @Provides
    @Singleton
    fun provideSettingsDataStore(app: Application):SettingsDataStore{
        return SettingsDataStore(app.applicationContext)
    }

}