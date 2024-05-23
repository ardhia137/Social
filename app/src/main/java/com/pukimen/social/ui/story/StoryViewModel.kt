package com.pukimen.social.ui.story

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pukimen.social.data.remote.response.ListStoryItem
import com.pukimen.social.data.repository.StroyRepository

class StoryViewModel(private val stroyRepository: StroyRepository) : ViewModel() {
    val getAllStory: LiveData<PagingData<ListStoryItem>> =
        stroyRepository.getAllStory().cachedIn(viewModelScope)


    fun getLocationStory(token:String,location:Int) = stroyRepository.getLocationStory(token,location)
    fun postStory(token:String,description:String,photo:Uri,context: Context) = stroyRepository.postStory(photo,description,token,context)
}