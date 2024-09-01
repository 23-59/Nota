package com.golden_minute.nota.domain.use_case.notes

import android.content.Context
import com.golden_minute.nota.R
import com.golden_minute.nota.domain.model.InvalidItemException
import com.golden_minute.nota.domain.model.Note
import com.golden_minute.nota.domain.repository.NoteRepository

class AddNote(val repository: NoteRepository, private val context: Context){
    @Throws
    suspend operator fun invoke (note:Note):Long {
        if (note.title.isBlank()){
           throw InvalidItemException(context.getString(R.string.the_title_is_empty))
        }
        if (note.description.isBlank()){
            throw InvalidItemException(context.getString(R.string.the_description_is_empty))
        }
       return repository.addNote(note)

    }

}