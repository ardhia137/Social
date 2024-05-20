package com.pukimen.social.data.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.pukimen.social.data.StoryPagingSource
import com.pukimen.social.data.local.UserPreference
import com.pukimen.social.data.model.StoryModel
import com.pukimen.social.data.remote.response.AllStoryResponse
import com.pukimen.social.data.remote.response.ListStoryItem
import com.pukimen.social.data.remote.response.PostStoryResponse
import com.pukimen.social.data.remote.retrofit.ApiService
import com.pukimen.social.utils.Results
import com.pukimen.social.utils.reduceFileImage
import com.pukimen.social.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StroyRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    private val results = MediatorLiveData<Results<List<StoryModel>>>()
    private val resultsPost = MediatorLiveData<Results<PostStoryResponse>>()



    fun getAllStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService,userPreference)
            }
        ).liveData
    }
    fun getLocationStory(token: String,location:Int): LiveData<Results<List<StoryModel>>> {
        results.value = Results.Loading
        val client = apiService.getLocationStory("Bearer $token",location)
        client.enqueue(object : Callback<AllStoryResponse> {
            override fun onResponse(call: Call<AllStoryResponse>, response: Response<AllStoryResponse>) {
                if (response.isSuccessful) {
                    val listStoryItem = response.body()?.listStory
                    val listStoryModel = listStoryItem?.mapNotNull { mapToListStoryModel(it) }
                    results.value = Results.Success(listStoryModel ?: emptyList())
                    Log.e(ContentValues.TAG, "$listStoryModel")
                } else {
                    results.value = Results.Error(response.message())
                    Log.e(ContentValues.TAG, "onFailurel: ${response.code()}")
                    Log.e(ContentValues.TAG, "onFailurel: ${response.message()}")
                    Log.e(ContentValues.TAG, "onFailurel: ${response.errorBody()}")
                    Log.e(ContentValues.TAG, "onFailurel: ${response.raw()}")
                }
            }

            override fun onFailure(call: Call<AllStoryResponse>, t: Throwable) {
                results.value = Results.Error(t.message.toString())
                Log.e(ContentValues.TAG, "onFailurel: ${t.message.toString()}")
            }
        })

        return results
    }

    fun postStory(photo: Uri, description:String, token: String,context: Context): LiveData<Results<PostStoryResponse>> {
        resultsPost.value = Results.Loading
        photo?.let { uri ->
            val imageFile = uriToFile(uri, context).reduceFileImage()
            Log.d("Image Classification File", "showImage: ${imageFile.path}")
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            val client = apiService.postStory("Bearer $token",multipartBody,description)
            client.enqueue(object : Callback<PostStoryResponse> {
            override fun onResponse(call: Call<PostStoryResponse>, response: Response<PostStoryResponse>) {
                if (response.isSuccessful) {
                    val response = response.body()
                    resultsPost.value = Results.Success(response!!)
                    Log.e(ContentValues.TAG, "suksess")
                } else {
                    resultsPost.value = Results.Error(response.message())
                    Log.e(ContentValues.TAG, "onFailurel: ${response.code()}")
                    Log.e(ContentValues.TAG, "onFailurel: ${response.message()}")
                    Log.e(ContentValues.TAG, "onFailurel: ${response.errorBody()}")
                    Log.e(ContentValues.TAG, "onFailurel: ${response.raw()}")
                }
            }

            override fun onFailure(call: Call<PostStoryResponse>, t: Throwable) {
                resultsPost.value = Results.Error(t.message.toString())
                Log.e(ContentValues.TAG, "onFailurel: ${t.message.toString()}")
            }
        })

        }
        return resultsPost

    }

    private fun mapToListStoryModel(listStoryItem: ListStoryItem?): StoryModel? {
        return listStoryItem?.let {
            StoryModel(
                it.photoUrl,
                it.createdAt,
                it.name,
                it.description,
                it.lon.toString(),
                it.id,
                it.lat.toString()
            )
        }
    }

    companion object {
        @Volatile
        private var instance: StroyRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StroyRepository =
            instance ?: synchronized(this) {
                instance ?: StroyRepository(apiService,userPreference)
            }.also { instance = it }
    }
}


