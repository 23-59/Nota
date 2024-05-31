package com.A_23_59.hypernote.domain.util

import com.A_23_59.hypernote.domain.model.Tag
import com.A_23_59.hypernote.domain.model.Task

sealed class HomeScreenTasksEvent {
    data class HomeScreenTasksOrder(val taskOrderType: TaskOrderType) : HomeScreenTasksEvent()
    data class DeleteTask(val task: Task) : HomeScreenTasksEvent()
    data object ToggleToolbarActions :HomeScreenTasksEvent()
    data object ToggleSearchbarVisibility : HomeScreenTasksEvent()
    data class SearchValueChanged(val input:String) : HomeScreenTasksEvent()
    data object TaskScreenLoaded: HomeScreenTasksEvent()

}