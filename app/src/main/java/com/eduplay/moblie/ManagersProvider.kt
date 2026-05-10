package com.eduplay.moblie

import android.content.Context
import com.eduplay.moblie.useCases.managers.AppSettingsManager
import com.eduplay.moblie.useCases.managers.AppSettingsManagerDataStore
import com.eduplay.moblie.useCases.managers.OfflineModeManager
import com.eduplay.moblie.useCases.managers.OfflineModeManagerDataStore
import com.eduplay.moblie.useCases.managers.TokenManager
import com.eduplay.moblie.useCases.managers.TokenManagerDataStore
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