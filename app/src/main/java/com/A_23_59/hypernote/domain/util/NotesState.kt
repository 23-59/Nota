package com.A_23_59.hypernote.domain.util

import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.model.Tag


data class NotesState(
    val notes: List<Note> = emptyList(),
    val notesTagsList : List<Tag> = emptyList(),
    val searchBarValue : String ="",
    val noteOrder: NoteOrderType = NoteOrderType.Descending,
    val isToolbarActionVisible: Boolean = false,
    val isSearchbarVisible: Boolean = false,
    val listIsEmpty : Boolean = true
)
