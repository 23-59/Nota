package com.golden_minute.nota.domain.use_case.notes

import com.golden_minute.nota.domain.model.Note
import com.golden_minute.nota.domain.repository.NoteRepository

class GetNote(private val repository: NoteRepository) {
    suspend operator fun invoke (id:Int): Note? {
        return repository.getNoteByTitle(id)
    }
}