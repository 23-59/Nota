package com.A_23_59.hypernote.domain.use_case.notes

import com.A_23_59.hypernote.domain.model.InvalidItemException
import com.A_23_59.hypernote.domain.model.InvalidNoteException
import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.repository.NoteRepository

class AddNote(val repository: NoteRepository) {
    @Throws
    suspend operator fun invoke (note:Note){
        if (note.title.isBlank()){
           throw InvalidItemException("the title is empty")
        }
        if (note.description.isBlank()){
            throw InvalidNoteException("the description is empty")
        }
        repository.addNote(note)

    }

}