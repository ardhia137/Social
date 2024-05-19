package com.pukimen.social.ui.story

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.pukimen.social.data.repository.StroyRepository

class StoryViewModel(private val stroyRepository: StroyRepository) : ViewModel() {
    fun getAllStory(token:String) = stroyRepository.getAllStory(token)
    fun postStory(token:String,description:String,photo:Uri,context: Context) = stroyRepository.postStory(photo,description,token,context)
}