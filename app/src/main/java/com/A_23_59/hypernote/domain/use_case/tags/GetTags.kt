package com.A_23_59.hypernote.domain.use_case.tags

import com.A_23_59.hypernote.data.data_source.TagDao
import com.A_23_59.hypernote.domain.model.Tag
import kotlinx.coroutines.flow.Flow

class GetTags(private val tagDao: TagDao) {

     operator fun invoke(): Flow<List<Tag>> {
        return tagDao.getAllTags()
    }
}