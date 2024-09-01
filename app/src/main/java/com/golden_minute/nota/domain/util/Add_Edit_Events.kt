package com.golden_minute.nota.domain.util

import com.golden_minute.nota.domain.model.Priority
import java.time.LocalDateTime


sealed class Add_Edit_Events {
    data class EnteredTitle(val title: String) : Add_Edit_Events()

    data class EnteredDescription(val description: String) : Add_Edit_Events()

    data class ChangePriority(val priority: Priority) : Add_Edit_Events()

    data class SaveDueDate(val dueDate: LocalDateTime) : Add_Edit_Events()

    data object ToggleTaskReminder : Add_Edit_Events()

    data class EnteredRepeatDialog(val period: String) : Add_Edit_Events()

    data class PressedAddTagButton(val tag: String) : Add_Edit_Events()

    data class PressedDeleteTagButton(val selectedTag: String) : Add_Edit_Events()

    data object ShowValidationSnackBar : Add_Edit_Events()

    data object Save : Add_Edit_Events()
}