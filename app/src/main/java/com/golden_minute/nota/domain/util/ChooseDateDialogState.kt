package com.golden_minute.nota.domain.util

data class ChooseDateDialogState(
    val yearFromTextField: String = "",
    val monthFromTextField: String = "",
    val dayFromTextField: String = "",
    val hourValue: String = "",
    val minuteValue: String = "",
    val dateTypeIsPredefined : Boolean = true,
    val dueDateHasBeenSet: Boolean = false,
    val reminderIsChecked: Boolean = false,
    val showDateAndTimeDialog: Boolean = false,
    val showTimePickerDialog : Boolean = false
)