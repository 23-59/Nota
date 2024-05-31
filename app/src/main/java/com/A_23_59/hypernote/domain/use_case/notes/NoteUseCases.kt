package com.A_23_59.hypernote.domain.use_case.notes

/**
 * this class is for wrap use cases into one class for cleaner access when get called in parameter
 */
data class NoteUseCases(
    val getNotes: GetNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote,
    val searchNote: SearchNote
)
