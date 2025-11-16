package com.eduplay.moblie

import com.eduplay.moblie.repository.webrepository.WebApi
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



@Module
@InstallIn(SingletonComponent::class)
class Providers {

    fun provideRetrofit(): WebApi {
        val url = BuildConfig.BACKEND_URL
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebApi::class.java)
    }
}