package com.A_23_59.hypernote.domain.util

import com.A_23_59.hypernote.domain.model.Priority
import java.time.LocalDate


sealed class Add_Edit_Events {
    data class EnteredTitle(val title:String):Add_Edit_Events()

    data class EnteredDescription(val description : String) : Add_Edit_Events()

    data class ChangePriority(val priority: Priority): Add_Edit_Events()

    data class EnteredTaskPeriod(val period: String) : Add_Edit_Events()

    data class EnteredDueDate(val dueDate : LocalDate) : Add_Edit_Events()

    data object ToggleTaskReminder : Add_Edit_Events()

    data class PressedAddTagButton(val tag:String) : Add_Edit_Events()

    data class PressedDeleteTagButton(val selectedTag:String): Add_Edit_Events()


    data object ShowValidationSnackBar : Add_Edit_Events()

    data object Save : Add_Edit_Events()
}