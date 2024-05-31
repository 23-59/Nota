package com.A_23_59.hypernote.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter
@Entity(tableName = "tbl_note")
 data class Note(
    @PrimaryKey
    val id :Int? =null,
    val editModeIsTrue: Boolean = false,
    val title: String,
    val description: String,
    val priority: Priority = Priority.LOW,
    val tagNumber1: String? = null,
    val tagNumber2: String? = null,
    val tagNumber3: String? = null,
){
    companion object{
        val timeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
    }
}

public class InvalidNoteException(message: String) : Exception(message)
