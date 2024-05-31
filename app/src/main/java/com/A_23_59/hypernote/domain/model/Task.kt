package com.A_23_59.hypernote.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.format.DateTimeFormatter

@Entity(tableName = "tbl_task")
data class Task(
    @PrimaryKey
    val id: Int? = null,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val tagNumber1: String? = null,
    val tagNumber2: String? = null,
    val tagNumber3: String? = null,
    val dueDate: Long? = null,
    val repeatTime: String? = null,
    var isChecked: Boolean? = false,
    val hasReminder: Boolean = false

) {
    companion object {
        val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
    }
}

enum class Priority {
    LOW, MEDIUM, HIGH
}

class InvalidItemException(message: String) : Exception(message)
