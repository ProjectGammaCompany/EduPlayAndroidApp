package com.eduplay.moblie.repository.webrepository.responseTypes

data class PasswordUpdate(
    val code: String,
    val password: String,
    val repeatPassword: String
)
