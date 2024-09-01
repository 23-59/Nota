package com.golden_minute.nota.domain.use_case.notes

import com.golden_minute.nota.domain.model.Note
import com.golden_minute.nota.domain.repository.NoteRepository

class DeleteNotes(val repository: NoteRepository) {

    suspend operator fun invoke(notes: List<Note>) {
        repository.deleteSelectedNotes(notes)
    }
}