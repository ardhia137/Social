package com.pukimen.social.di

import android.content.Context
import com.pukimen.social.data.local.UserPreference
import com.pukimen.social.data.repository.AuthRepository
import com.pukimen.social.data.remote.retrofit.ApiConfig
import com.pukimen.social.data.local.dataStore
import com.pukimen.social.data.repository.StroyRepository

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)

        return AuthRepository.getInstance(apiService,  pref)
    }

    fun provideStroyRepository(context: Context): StroyRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        return StroyRepository.getInstance(apiService,pref)
    }
}
