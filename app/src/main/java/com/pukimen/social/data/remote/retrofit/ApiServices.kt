package com.pukimen.social.data.remote.retrofit

import com.pukimen.social.data.remote.response.AllStoryResponse
import com.pukimen.social.data.remote.response.LoginResponse
import com.pukimen.social.data.remote.response.PostStoryResponse
import com.pukimen.social.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getAllStory(
        @Header("Authorization") token: String
    ): Call<AllStoryResponse>

    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") token: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: String,
    ): Call<PostStoryResponse>



}