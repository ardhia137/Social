package com.pukimen.social

import com.pukimen.social.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(

                "createdAt + $i",
                "name $i",
                "description $i",
                "lon $i",
                "lat $i",
                i.toString(),
                "photoUrl $i",
            )
            items.add(story)
        }
        return items
    }
}