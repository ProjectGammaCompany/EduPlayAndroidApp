package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.requestTypes.Refresh
import com.eduplay.moblie.useCases.TokenManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RefreshInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val api: AuthApi
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response: Response = chain.proceed(request);

        if (response.code == 401) {
            response.close()

            val refreshToken: String?
            runBlocking {
                refreshToken = tokenManager.getRefreshToken().firstOrNull()
            }
            if (refreshToken == null) {
                throw NotAuthorisedException("no access token")
            }

            val tokenResponse =
                runBlocking {
                    api.refresh(Refresh(refreshToken))
                }
            val newTokens = tokenResponse.body()
            if (!tokenResponse.isSuccessful || newTokens == null) {
                throw NotAuthorisedException("refresh token failed")
            }

            runBlocking {
                tokenManager.saveAccessToken(newTokens.accessToken)
                tokenManager.saveRefreshToken(newTokens.refreshToken)
            }

            val newRequest: Request = request
                .newBuilder()
                .removeHeader("Authorization")
                .addHeader("Authorization", "Bearer ${newTokens.accessToken}")
                .build();
            return chain.proceed(newRequest);
        }

        return response;
    }

}