package com.A_23_59.hypernote.data.repository

import com.A_23_59.hypernote.data.data_source.NoteDao
import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {
    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes()
    }

    override fun searchNote(input: String): Flow<List<Note>> {
        return noteDao.searchNote(input)
    }

    override suspend fun addNote(note: Note) {
        return noteDao.addNote(note)
    }

    override suspend fun deleteSelectedNotes(notes: List<Note>) {
        return noteDao.deleteAllNotes(notes)
    }

    override suspend fun updateSelectedNotes(notes: List<Note>) {
        return noteDao.updateNotes(notes)
    }

    override suspend fun deleteNote(note: Note) {
        return noteDao.deleteNote(note)
    }

    override suspend fun getNoteByTitle(id: Int): Note? {
        return noteDao.getNoteByTitle(id)
    }
}