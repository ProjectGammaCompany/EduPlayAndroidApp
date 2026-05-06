package com.eduplay.moblie.repository.webrepository

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface EventFilesApi {
    @GET("/eduplay-bucket/generated/{fileName}")
    @Streaming
    fun getEventFile(@Path("fileName") fileName: String): Call<ResponseBody>

    @GET("/eduplay-bucket/uploads/{fileName}")
    @Streaming
    fun getRegularFile(@Path("fileName") fileName: String): Call<ResponseBody>
}