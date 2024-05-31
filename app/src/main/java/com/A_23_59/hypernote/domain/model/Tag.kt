package com.A_23_59.hypernote.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "tbl_tag")
data class Tag(
    @PrimaryKey
    val id: Int? =null,
    var tagName: String = "",
    var isChecked: Boolean = false
)