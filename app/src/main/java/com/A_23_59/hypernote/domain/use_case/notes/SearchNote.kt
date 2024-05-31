package com.A_23_59.hypernote.domain.use_case.notes

import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.repository.NoteRepository
import com.A_23_59.hypernote.domain.util.NoteOrderType
import com.A_23_59.hypernote.presentation.tasksTagsList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchNote(private val noteRepository: NoteRepository) {
    operator fun invoke(title: String, noteOrderType: NoteOrderType): Flow<List<Note>> {
        return noteRepository.searchNote(title).map { notes ->
            when (noteOrderType) {
                is NoteOrderType.Descending -> notes.sortedByDescending { it.title }
                is NoteOrderType.Ascending -> notes.sortedBy { it.title }
                is NoteOrderType.Tags -> findSelectedTagsInNoteList(notes).sortedBy { sortedTags ->
                    notes.sortedBy { sortedTags.title }
                    sortedTags.title
                }
            }
        }
    }

    private fun findSelectedTagsInNoteList(notes: List<Note>): List<Note> {
        val sortedTagsList: ArrayList<Note> = arrayListOf()
        notes.forEach { note ->
            tasksTagsList.forEach { tag ->
                if (tag.tagName == note.tagNumber1 || tag.tagName == note.tagNumber2 || tag.tagName == note.tagNumber3)
                    sortedTagsList.add(note)
            }
        }
        return sortedTagsList
    }
}