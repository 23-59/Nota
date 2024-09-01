package com.golden_minute.nota.domain.util

import com.golden_minute.nota.domain.model.Tag
import com.golden_minute.nota.domain.model.Task
import com.golden_minute.nota.presentation.SelectModeEvents

sealed class HomeScreenTasksEvent {

    data class HomeScreenTasksOrder(val taskOrderType: TaskOrderType) : HomeScreenTasksEvent()

    data class DeleteTask(val task: Task) : HomeScreenTasksEvent()

    data class OnCheckChange(val checkedTask: Task) : HomeScreenTasksEvent()

    data class ToolbarActionClicked(val action: SelectModeEvents) : HomeScreenTasksEvent()

    data class OnSelectionTaskClick(val task: Task, val isSelected: Boolean) :
        HomeScreenTasksEvent()

    data object ToggleToolbarActions : HomeScreenTasksEvent()

    data class OnTasksFilteredByTags(val selectedTags: List<Tag>) : HomeScreenTasksEvent()

    data object ToggleSearchbarVisibility : HomeScreenTasksEvent()

    data object OnTagDialogClick : HomeScreenTasksEvent()

    data class SearchValueChanged(val input: String) : HomeScreenTasksEvent()

    data class OnDeleteTagsInDialog(val selectedTags: List<Tag>) : HomeScreenTasksEvent()

    data object TaskScreenLoaded : HomeScreenTasksEvent()

}