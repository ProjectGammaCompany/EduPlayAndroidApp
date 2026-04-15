package com.eduplay.moblie

import android.content.Context
import androidx.room.Room
import com.eduplay.moblie.repository.localrepository.Database
import com.eduplay.moblie.repository.webrepository.AuthApi
import com.eduplay.moblie.repository.webrepository.AuthInterceptor
import com.eduplay.moblie.repository.webrepository.EventFilesApi
import com.eduplay.moblie.repository.webrepository.RefreshInterceptor
import com.eduplay.moblie.repository.webrepository.WebApi
import com.eduplay.moblie.useCases.DownloadStatusObserver
import com.eduplay.moblie.useCases.TaskDownloadUseCase
import com.eduplay.moblie.useCases.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class Providers {


    @Provides
    fun provideFileDownloader(@ApplicationContext context: Context): TaskDownloadUseCase {
        return TaskDownloadUseCase(context)
    }


    @Provides
    @Singleton
    fun provideHttpClient(tokenManager: TokenManager, autApi: AuthApi): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(RefreshInterceptor(tokenManager, autApi))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): WebApi {
        val url = BuildConfig.BACKEND_URL
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(WebApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRetrofit(tokenManager: TokenManager): AuthApi {
        val url = BuildConfig.BACKEND_URL
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(tokenManager))
                    .build()
            )
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGeneratedFilesRetrofit(okHttpClient: OkHttpClient): EventFilesApi {
        val url = BuildConfig.BACKEND_EVENT_FILE_URL
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
            .create(EventFilesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java, "eduplayDb"
        )
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun profideDownloadStatusObserver(): DownloadStatusObserver {
        return DownloadStatusObserver
    }
}