package com.A_23_59.hypernote.domain.use_case.notes

import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.repository.NoteRepository

class DeleteNote(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}