package com.A_23_59.hypernote.domain.use_case.tags

import com.A_23_59.hypernote.data.data_source.TagDao
import com.A_23_59.hypernote.domain.model.Tag

class UpdateTags(private val tagDao: TagDao) {

    suspend operator fun invoke(tags:List<Tag>){
        tagDao.updateTags(tags)
    }
}