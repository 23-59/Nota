package com.golden_minute.nota.domain.util

import com.golden_minute.nota.domain.model.Note
import com.golden_minute.nota.domain.model.Tag
import com.golden_minute.nota.presentation.SelectModeEvents

sealed class HomeScreenNotesEvent {

    data class OrderNote(val noteOrder: NoteOrderType, val tagOrderIsActive: Boolean = false) :
        HomeScreenNotesEvent()

    data class DeleteNote(val note: Note) : HomeScreenNotesEvent()

    data object ToggleToolbarActionsVisibility : HomeScreenNotesEvent()

    data class OnSelectionNoteClick(val note: Note, val isSelected: Boolean) :
        HomeScreenNotesEvent()

    data object ToggleSearchbarVisibility : HomeScreenNotesEvent()

    data class OnNotesFilteredByTags(val selectedTags: List<Tag>) : HomeScreenNotesEvent()

    data class ToolbarActionClicked(val action: SelectModeEvents) : HomeScreenNotesEvent()

    data class SearchValueChanged(val input: String) : HomeScreenNotesEvent()

    data object OnTagDialogClick : HomeScreenNotesEvent()

    data object NoteScreenLoaded : HomeScreenNotesEvent()

    data class OnDeleteTagsInDialog(val selectedTags: List<Tag>) : HomeScreenNotesEvent()


}