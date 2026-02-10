package com.eduplay.moblie.repository.requestTypes

data class RegistrationData(
    val email: String,
    val password: String,
    val repeatPassword: String
)
