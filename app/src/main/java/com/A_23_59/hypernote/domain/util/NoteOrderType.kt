package com.A_23_59.hypernote.domain.util

sealed class NoteOrderType {
    data object Ascending : NoteOrderType()
    data object Descending : NoteOrderType()
    data class Tags(val tags: List<String>) : NoteOrderType()
}