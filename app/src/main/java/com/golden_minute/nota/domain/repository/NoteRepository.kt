package com.golden_minute.nota.domain.repository

import com.golden_minute.nota.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>

    fun searchNote(input: String): Flow<List<Note>>

    suspend fun addNote(note: Note):Long

    suspend fun deleteSelectedNotes(notes: List<Note>)

    suspend fun updateSelectedNotes(notes: List<Note>)

    suspend fun updateNote(note: Note)

    suspend fun updateNotes(notes: List<Note>)

    suspend fun deleteNote(note: Note)

    suspend fun getNoteByTitle(id: Int): Note?
}