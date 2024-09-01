package com.golden_minute.nota.domain.use_case.tags

import com.golden_minute.nota.data.data_source.TagDao
import com.golden_minute.nota.domain.model.Tag

class DeleteTags(private val tagDao: TagDao) {

    suspend operator fun invoke(tags: List<Tag>): Int{
       return tagDao.deleteTags(tags)
    }
}