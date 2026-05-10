package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.useCases.managers.TokenManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation


class AuthInterceptor @Inject constructor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.markedForInjection()) {
            val token = runBlocking {
                tokenManager.getAccessToken().firstOrNull()
            }
            if (!token.isNullOrEmpty()) {
                val newRequest = request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                return chain.proceed(newRequest)
            } else {
                throw NotAuthorisedException("no access token")
            }
        }

        return chain.proceed(request)
    }

    private fun Request.markedForInjection(): Boolean =
        tag(Invocation::class.java)
            ?.method()
            ?.getAnnotation(InjectAuth::class.java) != null
}