package com.A_23_59.hypernote.domain.util

import com.A_23_59.hypernote.domain.model.Note
import com.A_23_59.hypernote.domain.model.Tag

sealed class HomeScreenNotesEvent {

    data class OrderNote( val noteOrder: NoteOrderType , val tagOrderIsActive : Boolean =false ):HomeScreenNotesEvent()

    data class DeleteNote(val note: Note):HomeScreenNotesEvent()

   data object ToggleToolbarActions:HomeScreenNotesEvent()

    data object ToggleSearchbarVisibility:HomeScreenNotesEvent()

    data class SearchValueChanged(val input:String):HomeScreenNotesEvent()

    data class OnTagDialogClick(val tags: List<Tag>):HomeScreenNotesEvent()

    data object ClearSearchValue: HomeScreenNotesEvent()

    data object NoteScreenLoaded:HomeScreenNotesEvent()




}