package com.A_23_59.hypernote.domain.repository

import com.A_23_59.hypernote.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>

    fun searchNote(input:String):Flow<List<Note>>

    suspend fun addNote(note: Note)

    suspend fun deleteSelectedNotes(notes: List<Note>)

    suspend fun updateSelectedNotes(notes: List<Note>)

    suspend fun deleteNote(note: Note)

    suspend fun getNoteByTitle(id: Int) : Note?
}