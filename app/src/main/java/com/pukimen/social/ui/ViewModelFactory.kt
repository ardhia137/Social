package com.pukimen.social.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pukimen.social.ui.auth.AuthViewModel
import com.pukimen.social.data.repository.AuthRepository
import com.pukimen.social.data.repository.StroyRepository
import com.pukimen.social.di.Injection
import com.pukimen.social.ui.story.StoryViewModel

class ViewModelFactory private constructor(private val loginRepository: AuthRepository,private val storyRepository: StroyRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(loginRepository) as T
        }else  if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }


    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val authRepository = Injection.provideAuthRepository(context)
                val stroyRepository = Injection.provideStroyRepository()
                instance ?: ViewModelFactory(authRepository, stroyRepository)
            }.also { instance = it }
    }
}