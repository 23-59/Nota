package com.A_23_59.hypernote.domain.use_case.notes


import com.A_23_59.hypernote.data.data_source.TagDao
import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.model.Tag
import com.A_23_59.hypernote.domain.repository.NoteRepository
import com.A_23_59.hypernote.domain.util.NoteOrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map

class GetNotes(private val noteRepository: NoteRepository,private val tagDao: TagDao) {
    operator fun invoke(noteOrderType: NoteOrderType): Flow<List<Note>>{
        return noteRepository.getAllNotes()
            .map { notes ->
            when(noteOrderType){
                is NoteOrderType.Descending ->notes.sortedByDescending { it.title }
                is NoteOrderType.Ascending -> notes.sortedBy { it.title }
                is NoteOrderType.Tags -> //TODO do something when tag button in chips section has been clicked
            }
        }
    }



}