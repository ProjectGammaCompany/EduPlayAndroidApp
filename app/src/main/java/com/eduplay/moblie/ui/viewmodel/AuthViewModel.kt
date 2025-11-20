package com.eduplay.moblie.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EduRepository): ViewModel() {
    fun submitLoginForm() {
        //TODO()
    }

    fun submitRegisterForm() {
        //TODO()
    }
}