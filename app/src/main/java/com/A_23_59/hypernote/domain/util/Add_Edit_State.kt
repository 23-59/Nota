package com.A_23_59.hypernote.domain.util

import com.A_23_59.hypernote.domain.model.Priority

data class Add_Edit_State(
    val title: String = "",
    val description: String = "",
    val taskPriority: Priority = Priority.LOW,
    val dateAndTime: String = "",
    val repeatTime: String = "",
    val itHasReminder: Boolean = false,
    val tagNumber1 : String = "",
    val tagNumber2 : String = "",
    val tagNumber3 : String = "",
)
