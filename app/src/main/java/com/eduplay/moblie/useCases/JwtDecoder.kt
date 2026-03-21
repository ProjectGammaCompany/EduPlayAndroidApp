package com.eduplay.moblie.services

import com.auth0.android.jwt.JWT

class JwtDecoder {
    companion object {
        fun getUserId(jwt: String): String? {
            val jwt: JWT = JWT(jwt)
            return jwt.getClaim("id").asString()
        }

        fun getUserEmail(jwt: String): String? {
            val jwt: JWT = JWT(jwt)
            return jwt.getClaim("email").asString()
        }
    }
}