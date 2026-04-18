package com.eduplay.moblie.models;

public enum AuthResult {
    SUCCESSES,
    USER_EXISTS,
    USER_NOT_FOUND,
    INCORRECT_EMAIL,
    WRONG_PASSWORD,
    UNSAFE_PASSWORD,
    UNKNOWN_ERROR
}