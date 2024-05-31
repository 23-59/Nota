package com.A_23_59.hypernote.domain.util

import com.A_23_59.hypernote.domain.model.Task

sealed class TaskOrderType {

    data object Ascending : TaskOrderType()
    data object Descending : TaskOrderType()
    data class Tags(val tags: List<String>) : TaskOrderType()
    data object DueDate:TaskOrderType()
    data object Priority:TaskOrderType()
    data object Completed:TaskOrderType()
    data object Undone:TaskOrderType()
}