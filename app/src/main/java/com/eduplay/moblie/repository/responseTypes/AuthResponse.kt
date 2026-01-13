package com.eduplay.moblie.repository.responseTypes

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)