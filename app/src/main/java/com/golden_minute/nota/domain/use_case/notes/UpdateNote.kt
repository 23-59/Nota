package com.golden_minute.nota.domain.use_case.notes

import com.golden_minute.nota.domain.model.Note
import com.golden_minute.nota.domain.repository.NoteRepository

class UpdateNote(private val noteRepository: NoteRepository) {
    suspend operator fun invoke(note: Note){
        noteRepository.updateNote(note)
    }
}