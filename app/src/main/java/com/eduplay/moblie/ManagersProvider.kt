package com.eduplay.moblie

import android.content.Context
import com.eduplay.moblie.useCases.AppSettingsManager
import com.eduplay.moblie.useCases.AppSettingsManagerDataStore
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.useCases.OfflineModeManagerDataStore
import com.eduplay.moblie.useCases.TokenManager
import com.eduplay.moblie.useCases.TokenManagerDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ManagersProvider {
    private var tokenManager: TokenManager? = null
    private var offlineModeManager: OfflineModeManager? = null
    private var settingsManager: AppSettingsManager? = null

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        if (tokenManager == null) {
            tokenManager = TokenManagerDataStore(context)
        }
        return tokenManager!!
    }

    @Provides
    @Singleton
    fun provideOfflineManager(@ApplicationContext context: Context): OfflineModeManager {
        if (offlineModeManager == null) {
            offlineModeManager = OfflineModeManagerDataStore(context)
        }
        return offlineModeManager!!
    }

    @Provides
    @Singleton
    fun provideSettingsManager(@ApplicationContext context: Context): AppSettingsManager {
        if (settingsManager == null) {
            settingsManager = AppSettingsManagerDataStore(context)
        }
        return settingsManager!!
    }

}