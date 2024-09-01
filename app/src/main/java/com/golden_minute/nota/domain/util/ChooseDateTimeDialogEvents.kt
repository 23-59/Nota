package com.golden_minute.nota.domain.util

sealed class ChooseDateTimeDialogEvents {
    data class EnteredYear(val year: String) : ChooseDateTimeDialogEvents()
    data class EnteredMonth(val month: String) : ChooseDateTimeDialogEvents()
    data class EnteredDateType(val isPredefined: Boolean) : ChooseDateTimeDialogEvents()
    data class ToggleDueDate(val dueDateIsSet: Boolean) : ChooseDateTimeDialogEvents()
    data class EnteredHour(val hour: String) : ChooseDateTimeDialogEvents()
    data class EnteredMinute(val minute: String) : ChooseDateTimeDialogEvents()
    data class EnteredDay(val day: String) : ChooseDateTimeDialogEvents()
    data class ReminderStatusIsChanged(val isChecked: Boolean) : ChooseDateTimeDialogEvents()
    data class ShowDateAndTimeDialog(val status: Boolean) : ChooseDateTimeDialogEvents()
    data class TimeDialogVisibility(val status: Boolean) : ChooseDateTimeDialogEvents()
    data object ClearAllValues : ChooseDateTimeDialogEvents()
}