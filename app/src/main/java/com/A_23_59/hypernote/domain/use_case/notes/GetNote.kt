package com.A_23_59.hypernote.domain.use_case.notes

import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.repository.NoteRepository

class GetNote(private val repository: NoteRepository) {
    suspend operator fun invoke (id:Int): Note? {
        return repository.getNoteByTitle(id)
    }
}