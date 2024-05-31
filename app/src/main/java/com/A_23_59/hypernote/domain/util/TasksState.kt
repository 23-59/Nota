package com.A_23_59.hypernote.domain.util

import com.A_23_59.hypernote.domain.model.Tag
import com.A_23_59.hypernote.domain.model.Task


data class TasksState(
    val tasks: List<Task> = emptyList(),
    val taskOrder: TaskOrderType = TaskOrderType.Descending,
    val isToolbarActionsVisible: Boolean = false,
    val isSearchBarVisible: Boolean = false,
    val listIsEmpty : Boolean = true
)
