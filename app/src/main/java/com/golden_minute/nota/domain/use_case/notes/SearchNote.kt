package com.golden_minute.nota.domain.use_case.notes

import com.golden_minute.nota.domain.model.Note
import com.golden_minute.nota.domain.repository.NoteRepository
import com.golden_minute.nota.domain.util.NoteOrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchNote(private val noteRepository: NoteRepository) {
    operator fun invoke(title: String, noteOrderType: NoteOrderType): Flow<List<Note>> {
        return noteRepository.searchNote(title).map { notes ->
            when (noteOrderType) {
                is NoteOrderType.Descending -> notes.sortedByDescending { it.title }
                is NoteOrderType.Ascending -> notes.sortedBy { it.title }
                NoteOrderType.CreationDate -> notes.sortedByDescending { it.creationDate }
                NoteOrderType.Default -> notes
            }
        }
    }


}