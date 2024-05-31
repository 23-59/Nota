package com.A_23_59.hypernote.domain.use_case.tags

data class TagsUseCases(
    val addTags: AddTags,
    val deleteTags: DeleteTags,
    val updateTags: UpdateTags,
    val getTags: GetTags
)
